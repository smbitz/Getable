package com.codegears.getable.ui.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.codegears.getable.util.GetGender;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;

public class ShareImageDetailLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_DETAIL_VALUE = "SHARE_PREF_DETAIL_VALUE";
	public static final String SHARE_PREF_KEY_CATEGORY_NAME = "SHARE_PREF_KEY_CATEGORY_NAME";
	public static final String SHARE_PREF_KEY_BRAND_NAME = "SHARE_PREF_KEY_BRAND_NAME";
	public static final String SHARE_PREF_KEY_STORES_NAME = "SHARE_PREF_KEY_STORES_NAME";
	public static final String SHARE_PREF_KEY_STORES_ID = "SHARE_PREF_KEY_STORES_ID";
	
	public static final String SHARE_PREF_KEY_GENDER_ID = "SHARE_PREF_KEY_GENDER_ID";	
	public static final String SHARE_PREF_KEY_CATEGORY_ID = "SHARE_PREF_KEY_CATEGORY_ID";
	
	public static final String SHARE_PREF_KEY_STORES_EXTERNAL_ID = "SHARE_PREF_KEY_STORES_EXTERNAL_ID";
	public static final String SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID = "SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID";
	
	private Button productButton;
	private Button brandButton;
	private Button locationButton;
	private Button genderButton;
	private BodyLayoutStackListener listener;
	private TextView productName;
	private TextView brandName;
	private TextView locationName;
	private TextView genderName;
	private String setCategoryName;
	private String setBrandName;
	private String setStoreName;
	private String setStoreId;
	private String setGenderId;
	private String setCategoryId;
	private SharedPreferences myPrefs;
	private GetGender getGender;
	private Button shareButton;
	private String shareURL;
	private MyApp app;
	//private List<String> appCookie;
	private Config config;
	private EditText priceEditText;
	private EditText captionEditText;
	private String setExternalId;
	private String setExternalTypeId;
	private String tempImageLocation;
	private AsyncHttpClient asyncHttpClient;
	private TextView facebookConfigText;
	private TextView twitterConfigText;
	private ToggleButton facebookConfigButton;
	private ToggleButton twitterConfigButton;
	private String connectFacebookURL;
	private String getCurrentUserDataURL;
	private ActorData currentActorData;
	private int facebookButtonStatus;
	private int twitterButtonStatus;
	private ProgressDialog loadingDialog;
	private Facebook facebook;
	private ImageView productRightArrow;
	private ImageView brandRightArrow;
	private ImageView locationRightArrow;
	private ImageView genderRightArrow;
	
	public ShareImageDetailLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareimagedetaillayout, this );
		
		SharedPreferences socialPrefs = this.getActivity().getSharedPreferences( MyApp.SHARE_PREF_SOCIAL_STATUS_BUTTON, this.getActivity().MODE_PRIVATE );
		facebookButtonStatus = socialPrefs.getInt( MyApp.SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS, 0 );
		twitterButtonStatus = socialPrefs.getInt( MyApp.SHARE_PREF_KEY_TWITTER_BUTTON_STATUS, 0 );
		
		myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
		
		productButton = (Button) findViewById( R.id.shareImageDetailLayoutProductButton );
		brandButton = (Button) findViewById( R.id.shareImageDetailLayoutBrandButton );
		locationButton = (Button) findViewById( R.id.shareImageDetailLayoutLocationButton );
		genderButton = (Button) findViewById( R.id.shareImageDetailLayoutGenderButton );
		productName = (TextView) findViewById( R.id.shareImageDetailLayoutProductName );
		brandName = (TextView) findViewById( R.id.shareImageDetailLayoutBrandName );
		locationName = (TextView) findViewById( R.id.shareImageDetailLayoutLocationName );
		genderName = (TextView) findViewById( R.id.shareImageDetailLayoutGenderName );
		getGender = new GetGender( this.getContext() );
		shareButton = (Button) findViewById( R.id.shareImageDetailLayoutShareButton );
		priceEditText = (EditText) findViewById( R.id.shareImageDetailLayoutPriceEditText );
		captionEditText = (EditText) findViewById( R.id.shareImageDetailLayoutCaptionEditText );
		facebookConfigButton = (ToggleButton) findViewById( R.id.shareImageDetailFacebookConfigButton );
		facebookConfigText = (TextView) findViewById( R.id.shareImageDetailFacebookConfigText );
		twitterConfigButton = (ToggleButton) findViewById( R.id.shareImageDetailTwitterConfigButton );
		twitterConfigText = (TextView) findViewById( R.id.shareImageDetailTwitterConfigText );
		productRightArrow = (ImageView) findViewById( R.id.shareImageDetailLayoutProductRightArrow );
		brandRightArrow = (ImageView) findViewById( R.id.shareImageDetailLayoutBrandRightArrow );
		locationRightArrow = (ImageView) findViewById( R.id.shareImageDetailLayoutLocationRightArrow );
		genderRightArrow = (ImageView) findViewById( R.id.shareImageDetailLayoutGenderRightArrow );
		
		productButton.setOnClickListener( this );
		brandButton.setOnClickListener( this );
		locationButton.setOnClickListener( this );
		genderButton.setOnClickListener( this );
		shareButton.setOnClickListener( this );
		facebookConfigButton.setOnClickListener( this );
		twitterConfigButton.setOnClickListener( this );
		facebookConfigText.setOnClickListener( this );
		twitterConfigText.setOnClickListener( this );
		
		priceEditText.addTextChangedListener( new TextWatcher() {
			
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
				if( s.length() == 0 ||
					productName.getText().length() == 0 ||
					brandName.getText().length() == 0 ||
					locationName.getText().length() == 0 ||
					genderName.getText().length() == 0 ){
					shareButton.setTextColor( R.color.NameColorGrey );
					shareButton.setEnabled( false );
				}else{
					shareButton.setTextColor( Color.WHITE );
					shareButton.setEnabled( true );
				}
			}
		});
		
		shareButton.setTextColor( R.color.NameColorGrey );
		shareButton.setEnabled( false );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		currentActorData = app.getCurrentProfileData();
		facebook = app.getFacebook();
		
		String tempDirectory = ShareImageCropLayout.TEMP_IMAGE_DIRECTORY_NAME;
		String tempImageName = ShareImageCropLayout.TEMP_IMAGE_FILE_NAME;
		tempImageLocation = Environment.getExternalStorageDirectory().toString()+tempDirectory+tempImageName;
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		setViewFromData();
		
		shareURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/product.json";
		getCurrentUserDataURL = config.get( MyApp.URL_GET_CURRENT_USER_DATA ).toString();
		connectFacebookURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/integrations/facebook.json";
	}
	
	private void setViewFromData(){
		//Set facebook and twitter button status default.
		facebookConfigButton.setVisibility( View.GONE );
		facebookConfigText.setVisibility( View.GONE );
		twitterConfigButton.setVisibility( View.GONE );
		twitterConfigText.setVisibility( View.GONE );
		
		//Set Facebook and Twitter config button;
		if( currentActorData.getSocialConnections().getFacebook().getStatus() ){
			facebookConfigButton.setVisibility( View.VISIBLE );
			if( facebookButtonStatus != 0 ){
				if( facebookButtonStatus == MyApp.FACEBOOK_BUTTON_STATUS_ON ){
					facebookConfigButton.setChecked( true );
				}else{
					facebookConfigButton.setChecked( false );
				}
			}else{
				facebookConfigButton.setChecked( false );
			}
		}else{
			facebookConfigText.setVisibility( View.VISIBLE );
		}
		
		if( currentActorData.getSocialConnections().getTwitter().getStatus() ){
			twitterConfigButton.setVisibility( View.VISIBLE );
			if( twitterButtonStatus != 0 ){
				if( twitterButtonStatus == MyApp.TWITTER_BUTTON_STATUS_ON ){
					twitterConfigButton.setChecked( true );
				}else{
					twitterConfigButton.setChecked( false );
				}
			}else{
				twitterConfigButton.setChecked( false );
			}
		}else{
			twitterConfigText.setVisibility( View.VISIBLE );
		}
	}
	
	private void refreshViewFromSetupSocial(){
		asyncHttpClient.post( getCurrentUserDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				ActorData actorData = new ActorData( jsonObject );
				app.setCurrentProfileData( actorData );
				currentActorData = app.getCurrentProfileData();
				
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				
				setViewFromData();
			}
		});
	}
	
	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void refreshView() {
		setCategoryName = myPrefs.getString( SHARE_PREF_KEY_CATEGORY_NAME, null );
		setBrandName = myPrefs.getString( SHARE_PREF_KEY_BRAND_NAME, null );
		setStoreName = myPrefs.getString( SHARE_PREF_KEY_STORES_NAME, null );
		setStoreId = myPrefs.getString( SHARE_PREF_KEY_STORES_ID, null );
		
		setGenderId = myPrefs.getString( SHARE_PREF_KEY_GENDER_ID, null );
		setCategoryId = myPrefs.getString( SHARE_PREF_KEY_CATEGORY_ID, null );
		
		setExternalId = myPrefs.getString( SHARE_PREF_KEY_STORES_EXTERNAL_ID, null );
		setExternalTypeId = myPrefs.getString( SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID, null );

		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				if( setCategoryName != null ){
					productRightArrow.setVisibility( View.GONE );
					productName.setText( setCategoryName );
				}
				if( setBrandName != null ){
					brandRightArrow.setVisibility( View.GONE );
					brandName.setText( setBrandName );
				}
				if( setStoreName != null ){
					locationRightArrow.setVisibility( View.GONE );
					locationName.setText( setStoreName );
				}
				if( setGenderId != null ){
					genderRightArrow.setVisibility( View.GONE );
					String setName = getGender.get( setGenderId ).toString();
					genderName.setText( setName );
				}
				if( productName.getText().length() > 0 &&
					brandName.getText().length() > 0 &&
					locationName.getText().length() > 0 &&
					genderName.getText().length() > 0 &&
					priceEditText.getText().length() > 0 ){
					shareButton.setTextColor( Color.WHITE );
					shareButton.setEnabled( true );
				}else{
					shareButton.setTextColor( R.color.NameColorGrey );
					shareButton.setEnabled( false );
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if( v.equals( shareButton ) ){
			//Post image.
			Bitmap bitmap = BitmapFactory.decodeFile( tempImageLocation );
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 10, bao);
			byte [] ba = bao.toByteArray();
			ByteArrayBody bab = new ByteArrayBody(ba, "shareImage.png");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("picture", bab);
			
			try {
				reqEntity.addPart("brand", new StringBody(setBrandName));
				reqEntity.addPart("category", new StringBody(setCategoryId));
				reqEntity.addPart("gender", new StringBody(setGenderId));
				reqEntity.addPart("price", new StringBody(priceEditText.getText().toString()));
				reqEntity.addPart("description", new StringBody(captionEditText.getText().toString()));
				
				//Set social sharing
				if( facebookButtonStatus == MyApp.FACEBOOK_BUTTON_STATUS_ON ){
					reqEntity.addPart("sharing.facebook", new StringBody("true"));
				}
				if( twitterButtonStatus == MyApp.TWITTER_BUTTON_STATUS_ON ){
					reqEntity.addPart("sharing.twitter", new StringBody("true"));
				}
				
				//Set store post data.
				if( setExternalId == null &&
					setExternalTypeId == null){
					reqEntity.addPart("selector", new StringBody("1")); //Store internal.
					reqEntity.addPart("store", new StringBody(setStoreId));
				}else{
					reqEntity.addPart("selector", new StringBody("2")); //Store external.
					reqEntity.addPart("externalStore.id", new StringBody(setExternalId));
					reqEntity.addPart("externalStore.type", new StringBody(setExternalTypeId));
				}
				
				asyncHttpClient.post( this.getContext(), shareURL, reqEntity, null, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject getJsonObject) {
						super.onSuccess(getJsonObject);
						//Test Data
						try {
							String productId = getJsonObject.getString( "id" );
							onShareURLSuccess(getJsonObject);
						} catch (JSONException e) {
							e.printStackTrace();
							final AlertDialog alertDialog = new AlertDialog.Builder( ShareImageDetailLayout.this.getContext() ).create();
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Upload fail !!" );
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
				});
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if( v.equals( facebookConfigButton ) ){
			SharedPreferences myPref = this.getActivity().getSharedPreferences( MyApp.SHARE_PREF_SOCIAL_STATUS_BUTTON, this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			if( facebookConfigButton.isChecked() ){
				prefsEditor.putInt( MyApp.SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS, MyApp.FACEBOOK_BUTTON_STATUS_ON );
			}else{
				prefsEditor.putInt( MyApp.SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS, MyApp.FACEBOOK_BUTTON_STATUS_OFF );
			}
			prefsEditor.commit();
		}else if( v.equals( twitterConfigButton ) ){
			SharedPreferences myPref = this.getActivity().getSharedPreferences( MyApp.SHARE_PREF_SOCIAL_STATUS_BUTTON, this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			if( twitterConfigButton.isChecked() ){
				prefsEditor.putInt( MyApp.SHARE_PREF_KEY_TWITTER_BUTTON_STATUS, MyApp.TWITTER_BUTTON_STATUS_ON );
			}else{
				prefsEditor.putInt( MyApp.SHARE_PREF_KEY_TWITTER_BUTTON_STATUS, MyApp.TWITTER_BUTTON_STATUS_OFF );
			}
			prefsEditor.commit();
		}else if( v.equals( facebookConfigText ) ){
			loadingDialog.show();
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this.getActivity() );
			String currentAccessToken = prefs.getString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
			long expires = prefs.getLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0);
			if(currentAccessToken != null) {
	            facebook.setAccessToken(currentAccessToken);
	        }
	        if(expires != 0) {
	            facebook.setAccessExpires(expires);
	        }
			
			if( !facebook.isSessionValid() ){
				facebook.authorize( this.getActivity(), new DialogListener() {
					
					@Override
					public void onFacebookError(FacebookError e) { }
					
					@Override
					public void onError(DialogError e) { }
					
					@Override
					public void onComplete(Bundle values) {
						String token = facebook.getAccessToken();  //get access token
						Long expires = facebook.getAccessExpires();  //get access expire
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ShareImageDetailLayout.this.getActivity() );
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN , token);
						editor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES , expires);
						editor.commit();
						
						HashMap<String, String> paramMap = new HashMap<String, String>();
						paramMap.put( "accessToken", facebook.getAccessToken() );
						paramMap.put( "_a", "connect" );
						RequestParams params = new RequestParams(paramMap);
						asyncHttpClient.post( connectFacebookURL, params, new AsyncHttpResponseHandler(){
							@Override
							public void onSuccess(String arg0) {
								super.onSuccess(arg0);
								refreshViewFromSetupSocial();
							}
						});
					}
					
					@Override
					public void onCancel() { }
					
				});
			}else{
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "accessToken", facebook.getAccessToken() );
				paramMap.put( "_a", "connect" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( connectFacebookURL, params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						refreshViewFromSetupSocial();
					}
				});
			}
		}else if( v.equals( twitterConfigText ) ){
			
		}else if( listener != null ){
			if( v.equals( productButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_PRODUCT );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}else if( v.equals( brandButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_BRAND );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}else if( v.equals( locationButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_STORE );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}else if( v.equals( genderButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_GENDER );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}
		}
	}
	
	private void onShareURLSuccess(JSONObject jsonObject) {
		//Can't redirect to product_detail page.
		if( listener != null ){
			//Load Product Data
			ProductActivityData newData = new ProductActivityData( jsonObject );
			SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, ShareImageDetailLayout.this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, newData.getId() );
			prefsEditor.commit();
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
		}
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

}
