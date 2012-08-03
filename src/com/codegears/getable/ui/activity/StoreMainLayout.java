package com.codegears.getable.ui.activity;

import java.util.List;

import org.json.JSONObject;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MapScene;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.StoreData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.MyItemizedOverlay;
import com.codegears.getable.util.Config;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class StoreMainLayout extends AbstractViewLayout implements OnClickListener {

	public static final String SHARE_PREF_VALUE_STORE_ID = "SHARE_PREF_VALUE_STORE_ID";
	public static final String SHARE_PREF_KEY_STORE_ID = "SHARE_PREF_KEY_STORE_ID";
	private static final int DEFAULT_ZOOM_NUM = 19;
	
	private BodyLayoutStackListener listener;
	private MapView mapView;
	private LinearLayout mapViewLayout;
	private MapController mapController;
	private String storeId;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private Config config;
	private String getStoreURL;
	private StoreData currentStoreData;
	private TextView storeName;
	private TextView storeAddress;
	private ImageView storeImage;
	private ImageButton directionButton;
	private ImageButton ViewStoreButton;
	private ImageButton backButton;

	public StoreMainLayout(Activity activity, MapView setMapview) {
		super(activity);
		View.inflate(this.getContext(), R.layout.storemainlayout, this);
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_STORE_ID, this.getActivity().MODE_PRIVATE );
		storeId = myPrefs.getString( SHARE_PREF_KEY_STORE_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		mapView = setMapview;
		
		mapViewLayout = (LinearLayout) findViewById( R.id.storeMainLayoutMapViewLayout );
		storeName = (TextView) findViewById( R.id.storeMainLayoutStoreName );
		storeAddress = (TextView) findViewById( R.id.storeMainLayoutStoreAddress );
		storeImage = (ImageView) findViewById( R.id.storeMainLayoutStoreImage );
		directionButton = (ImageButton) findViewById( R.id.storeMainLayoutStoreDirectionButton );
		ViewStoreButton = (ImageButton) findViewById( R.id.storeMainLayoutStoreViewStoreButton );
		backButton = (ImageButton) findViewById( R.id.storeMainLayoutBackButton );
		
		mapViewLayout.addView( mapView );
		
		//Set font
		storeName.setTypeface( Typeface.createFromAsset( this.getActivity().getAssets(), MyApp.APP_FONT_PATH) );
		storeAddress.setTypeface( Typeface.createFromAsset( this.getActivity().getAssets(), MyApp.APP_FONT_PATH) );
		
		directionButton.setOnClickListener( this );
		ViewStoreButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		mapController = mapView.getController();
		mapController.setZoom( DEFAULT_ZOOM_NUM );
		
		getStoreURL = config.get( MyApp.URL_DEFAULT ).toString()+"stores/"+storeId+".json";
		
		asyncHttpClient.post( getStoreURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				currentStoreData = new StoreData(jsonObject);
				
				storeName.setText( currentStoreData.getName() );
				storeAddress.setText( currentStoreData.getPostalCode()+" "+currentStoreData.getStreetAddress() );
				Double storeLat = Double.parseDouble( currentStoreData.getCoondinate().getLat() ) * 1E6;
				Double storeLng = Double.parseDouble( currentStoreData.getCoondinate().getLng() ) * 1E6;
				
				GeoPoint targetPoint = new GeoPoint( storeLat.intValue(), storeLng.intValue() );
				
				List<Overlay> mapOverlays = mapView.getOverlays();
				Drawable targetDrawable = StoreMainLayout.this.getActivity().getResources().getDrawable( R.drawable.map_pin );
				MyItemizedOverlay targetItemOverLay = new MyItemizedOverlay( targetDrawable );
				OverlayItem overlayitemTarget = new OverlayItem(targetPoint, "", "");
				targetItemOverLay.addOverlay( overlayitemTarget );
				mapOverlays.add( targetItemOverLay );
				
				mapController.animateTo( targetPoint );
			}
		});
	}

	@Override
	public void refreshView(Intent getData) {
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if( v.equals( directionButton ) ){
			Intent newIntent = new Intent( this.getActivity(), MapScene.class );
			String newArrayExtra[] = { currentStoreData.getCoondinate().getLat(), currentStoreData.getCoondinate().getLng() };
			newIntent.putExtra( MapScene.PUT_MAP_EXTRA, newArrayExtra );
			this.getActivity().startActivity( newIntent );
		}else if( v.equals( ViewStoreButton ) ){
			
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}
	
	public void removeMapViewFromLayout(){
		mapView.getOverlays().clear();
		mapViewLayout.removeAllViews();
	}
	
}
