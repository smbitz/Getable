package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.MyBalloonItemizedOverlay;
import com.codegears.getable.ui.MyItemizedOverlay;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetCurrentLocation;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;

public class ShareProductAddNewStoreLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_NEW_STORE_NAME = "SHARE_PREF_NEW_STORE_NAME";
	public static final String SHARE_PREF_KEY_NEW_STORES_NAME = "SHARE_PREF_KEY_NEW_STORES_NAME";
	private static final int DEFAULT_ZOOM_NUM = 19;
	
	private ImageButton backButton;
	private Button addButton;
	private TextView nameText;
	private TextView addressText;
	private TextView locationText;
	private EditText nameEditText;
	private TextView addressTextResult;
	private LinearLayout addressTextLayout;
	private LinearLayout mapViewLayout;
	private BodyLayoutStackListener listener;
	private MapView mapView;
	private String addNewStoreURL;
	private Config config;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private AlertDialog alertDialog;
	private ProgressDialog loadingDialog;
	private MapController mapController;
	private GetCurrentLocation getCurrentLocation;
	private MyBalloonItemizedOverlay myBalloonItemizedOverlay;
	private Double currentLat;
	private Double currentLng;
	private Button mapButton;
	private Button addressButton;

	public ShareProductAddNewStoreLayout(Activity activity, MapView setMapView) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareproductaddnewstore, this );
		
		//Get new store name
		SharedPreferences myPrefNewStoreName = this.getActivity().getSharedPreferences( SHARE_PREF_NEW_STORE_NAME, this.getActivity().MODE_PRIVATE );
		String newStoreName = myPrefNewStoreName.getString( SHARE_PREF_KEY_NEW_STORES_NAME, "" );
		
		//Clear old data
		SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductAddNewStoreAddressLayout.SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
		SharedPreferences.Editor prefsEditor = myPref.edit();
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_ADDRESS, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CROSS_STREET, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CITY, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_STATE, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_POST_CODE, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_PHONE, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_TWITTER, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LAT, null );
		prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LONG, null );
        prefsEditor.commit();
		
		backButton = (ImageButton) findViewById( R.id.shareProductAddNewStoreBackButton );
		addButton = (Button) findViewById( R.id.shareProductAddNewStoreAddButton );
		nameText = (TextView) findViewById( R.id.shareProductAddNewStoreNameText );
		addressText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressText );
		locationText = (TextView) findViewById( R.id.shareProductAddNewStoreLocationText );
		nameEditText = (EditText) findViewById( R.id.shareProductAddNewStoreNameEditText );
		addressTextResult = (TextView) findViewById( R.id.shareProductAddNewStoreAddressTextResult );
		addressTextLayout = (LinearLayout) findViewById( R.id.shareProductAddNewStoreAddressLayout );
		mapViewLayout = (LinearLayout) findViewById( R.id.shareProductAddNewStoreMapViewLayout );
		mapButton = (Button) findViewById( R.id.shareProductAddNewStoreMapButton );
		addressButton = (Button) findViewById( R.id.shareProductAddNewStoreAddressButton );
		
		mapView = setMapView;
		config = new Config( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		alertDialog = new AlertDialog.Builder( this.getContext() ).create();
		getCurrentLocation = new GetCurrentLocation( this.getContext() );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		mapViewLayout.addView( mapView );
		
		nameText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		addressText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		locationText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		nameEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		addressTextResult.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		
		nameEditText.setText( newStoreName );
		
		//Default done button state.
		if( nameEditText.getText().length() > 0 ){
			addButton.setTextColor( Color.WHITE );
			addButton.setEnabled( true );
		}else{
			addButton.setTextColor( R.color.NameColorGrey );
			addButton.setEnabled( false );
		}
		
		nameEditText.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if( s.length() == 0 ){
					addButton.setTextColor( R.color.NameColorGrey );
					addButton.setEnabled( false );
				}else{
					addButton.setTextColor( Color.WHITE );
					addButton.setEnabled( true );
				}
			}
		});
		
		nameEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
		          return true;
		        }
		        return false;
			}
		});
		
		backButton.setOnClickListener( this );
		addButton.setOnClickListener( this );
		addressButton.setOnClickListener( this );
		nameEditText.setOnClickListener( this );
		mapButton.setOnClickListener( this );
		
		mapController = mapView.getController();
		mapController.setZoom( DEFAULT_ZOOM_NUM );
		
		currentLat = Double.parseDouble( getCurrentLocation.getCurrentLat() ) * 1E6;
		currentLng = Double.parseDouble( getCurrentLocation.getCurrentLng() ) * 1E6;
		
		try {
			Address currentAddress = new Geocoder( this.getContext(), Locale.getDefault() ).getFromLocation( Double.parseDouble( getCurrentLocation.getCurrentLat() ), Double.parseDouble( getCurrentLocation.getCurrentLng() ), 1 ).get( 0 );
			String addressText = currentAddress.getThoroughfare();
			String stateText = currentAddress.getLocality();
			String postCodeText = currentAddress.getPostalCode();
			String[] cityArray = currentAddress.getAddressLine(1).split(",");
			String cityText = cityArray[1].trim();
			
			String showAddressText = "";
			if( !(addressText.equals("")) && addressText != null ){
				if( showAddressText != "" ){
					showAddressText += ", ";
				}
				showAddressText += addressText;
			}
			if( !(stateText.equals("")) && stateText != null ){
				if( showAddressText != "" ){
					showAddressText += ", ";
				}
				showAddressText += stateText;
			}
			if( !(postCodeText.equals("")) && postCodeText != null ){
				if( showAddressText != "" ){
					showAddressText += ", ";
				}
				showAddressText += postCodeText;
			}
			if( !(cityText.equals("")) && cityText != null ){
				if( showAddressText != "" ){
					showAddressText += ", ";
				}
				showAddressText += cityText;
			}
			
			addressTextResult.setText( showAddressText );
			
			SharedPreferences myPrefAddress = this.getActivity().getSharedPreferences( ShareProductAddNewStoreAddressLayout.SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsAddressEditor = myPrefAddress.edit();
			prefsAddressEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_ADDRESS, addressText );
			prefsAddressEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CITY, cityText );
			prefsAddressEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_STATE, stateText );
			prefsAddressEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_POST_CODE, postCodeText );
			prefsAddressEditor.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addMapBalloon();
		
		addNewStoreURL = config.get( MyApp.URL_DEFAULT ).toString()+"stores.json?_f=create";
	}
	
	private void addMapBalloon(){
		GeoPoint targetPoint = new GeoPoint( currentLat.intValue(), currentLng.intValue() );
		
		Drawable targetDrawable = this.getActivity().getResources().getDrawable( R.drawable.map_pin );
		myBalloonItemizedOverlay = new MyBalloonItemizedOverlay( targetDrawable, mapView );
		OverlayItem overlayItem = new OverlayItem( targetPoint, "Tap to adjust", null );
		myBalloonItemizedOverlay.setBalloonBottomOffset(30);
		myBalloonItemizedOverlay.addOverlay( overlayItem );
		myBalloonItemizedOverlay.setFocus( overlayItem );
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add( myBalloonItemizedOverlay );
		
		mapController.animateTo( targetPoint );
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductAddNewStoreAddressLayout.SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
		String addressValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_ADDRESS, "" );
		String crossStreetValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CROSS_STREET, "" );
		String cityValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CITY, "" );
		String stateValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_STATE, "" );
		String postCodeValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_POST_CODE, "" );
		String phoneValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_PHONE, "" );
		String twitterValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_TWITTER, "" );
		String mapLatValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LAT, "" );
		String mapLongValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LONG, "" );
		
		String setAddressValue = "";
		if( !(addressValue.equals("")) && addressValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += addressValue;
		}
		if( !(crossStreetValue.equals("")) && crossStreetValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += crossStreetValue;
		}
		if( !(cityValue.equals("")) && cityValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += cityValue;
		}
		if( !(stateValue.equals("")) && stateValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += stateValue;
		}
		if( !(postCodeValue.equals("")) && postCodeValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += postCodeValue;
		}
		if( !(phoneValue.equals("")) && phoneValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += phoneValue;
		}
		if( !(twitterValue.equals("")) && twitterValue != null ){
			if( setAddressValue != "" ){
				setAddressValue += ", ";
			}
			setAddressValue += twitterValue;
		}
		
		addressTextResult.setText( setAddressValue );
		
		currentLat = Double.valueOf( mapLatValue );
		currentLng = Double.valueOf( mapLongValue );
		
		mapViewLayout.addView( mapView );
		addMapBalloon();
	}

	@Override
	public void onClick(View v) {
		if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( nameEditText.getWindowToken(), 0 );
			
			removeMapViewFromLayout();
			
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
		}else if( v.equals( addressButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( nameEditText.getWindowToken(), 0 );
			
			removeMapViewFromLayout();
			
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_ADD_NEW_STORE_ADDRESS );
		}else if( v.equals( addButton ) ){
			loadingDialog.show();
			
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( nameEditText.getWindowToken(), 0 );
			
			String nameValue = nameEditText.getText().toString();
			
			SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductAddNewStoreAddressLayout.SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
			String addressValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_ADDRESS, "" );
			String crossStreetValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CROSS_STREET, "" );
			String cityValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_CITY, "" );
			String stateValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_STATE, "" );
			String postCodeValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_POST_CODE, "" );
			String phoneValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_PHONE, "" );
			String twitterValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_TWITTER, "" );
			String mapLatValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LAT, "" );
			String mapLongValue = myPref.getString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LONG, "" );
			
			Double getMapLat = (Double.parseDouble( mapLatValue ));
			Double getMapLong = (Double.parseDouble( mapLongValue ));
			
			GeoPoint getTargetPoint = new GeoPoint( getMapLat.intValue(), getMapLong.intValue() );
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				reqEntity.addPart("name", new StringBody(nameValue));
				reqEntity.addPart("streetAddress", new StringBody(addressValue));
				reqEntity.addPart("crossStreet", new StringBody(crossStreetValue));
				reqEntity.addPart("city", new StringBody(cityValue));
				reqEntity.addPart("state", new StringBody(stateValue));
				reqEntity.addPart("postalCode", new StringBody(postCodeValue));
				reqEntity.addPart("phoneNumber", new StringBody(phoneValue));
				reqEntity.addPart("twitter", new StringBody(twitterValue));
				reqEntity.addPart("coordinate.latitude", new StringBody( String.valueOf( getTargetPoint.getLatitudeE6()/1E6) ) );
				reqEntity.addPart("coordinate.longitude", new StringBody( String.valueOf( getTargetPoint.getLongitudeE6()/1E6) ) );
				
				asyncHttpClient.post( this.getContext(), addNewStoreURL, reqEntity, null, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						System.out.println("AddNewStoreResult : "+jsonObject);
						try {
							String nameNewStore = jsonObject.getString( "name" );
							String idNewStore = jsonObject.getString( "id" );
							
							SharedPreferences myPref = ShareProductAddNewStoreLayout.this.getActivity().getSharedPreferences( ShareImageDetailLayout.SHARE_PREF_DETAIL_VALUE, ShareProductAddNewStoreLayout.this.getActivity().MODE_PRIVATE );
							SharedPreferences.Editor prefsEditor = myPref.edit();
							prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_NAME, nameNewStore );
							prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_ID, idNewStore );
					        prefsEditor.commit();
					        
					        removeMapViewFromLayout();
					        
					        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT );
							
					        if( loadingDialog.isShowing() ){
					        	loadingDialog.dismiss();
					        }
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if( loadingDialog.isShowing() ){
					        	loadingDialog.dismiss();
					        }
							
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot add new store." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
						}
					}
					
					@Override
					public void onFailure(Throwable arg0, JSONObject arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						if( loadingDialog.isShowing() ){
				        	loadingDialog.dismiss();
				        }
						
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot add new store." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
					}
					
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						if( loadingDialog.isShowing() ){
				        	loadingDialog.dismiss();
				        }
						
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot add new store." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
					}
				});
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Cannot add new store." );
				alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
					}
				});
				alertDialog.show();
			}
		}else if( v.equals( nameEditText ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.showSoftInputFromInputMethod( nameEditText.getWindowToken(), 0 );
		}else if( v.equals( mapButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( nameEditText.getWindowToken(), 0 );
			
			removeMapViewFromLayout();
			
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_ADD_NEW_STORE_ADDRESS_MAP );
		}
	}
	
	public void removeMapViewFromLayout(){
		myBalloonItemizedOverlay.hideAllBalloons();
		mapView.getOverlays().clear();
		mapViewLayout.removeAllViews();
	}

}
