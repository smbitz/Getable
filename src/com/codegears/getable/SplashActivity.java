package com.codegears.getable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.CategoryData;
import com.codegears.getable.data.MetroData;
import com.codegears.getable.ui.activity.ProfileLayout;
import com.codegears.getable.ui.activity.ShareProductSearchLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.facebook.android.Facebook;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

public class SplashActivity extends Activity {
	
	public static final String SHARE_PREF_USER_INFO = "SHARE_PREF_USER_INFO";
	public static final String SHARE_PREF_KEY_USER_EMAIL = "SHARE_PREF_KEY_USER_EMAIL";
	public static final String SHARE_PREF_KEY_USER_PASSWORD = "SHARE_PREF_KEY_USER_PASSWORD";
	
	public static final String URL_GET_METROS = "URL_GET_METROS";
	
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private PersistentCookieStore appCookie;
	private Config config;
	private String loginURL;
	private Facebook facebook;
	private String loginSignUpURL;
	private ArrayList<MetroData> arrayMetroName;
	private ArrayList<CategoryData> arrayDefaultCategoryData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.splashactivity );
		
		app = (MyApp) this.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		appCookie = new PersistentCookieStore( this );
		config = new Config( this );
		facebook = app.getFacebook();
		arrayMetroName = new ArrayList<MetroData>();
		arrayDefaultCategoryData = new ArrayList<CategoryData>();
		
		asyncHttpClient.setCookieStore( appCookie );
		app.setAppCookie( appCookie );
		
		loginURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
		
		//Load default data
		clearSharePrefAppValue();
		loadAppDefaultData();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
		final String currentAccessToken = prefs.getString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
		final long expires = prefs.getLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0);
		
		SharedPreferences myPrefs = this.getSharedPreferences( SHARE_PREF_USER_INFO, this.MODE_PRIVATE );
		String userEmail = myPrefs.getString( SHARE_PREF_KEY_USER_EMAIL, null );
		String userPassword = myPrefs.getString( SHARE_PREF_KEY_USER_PASSWORD, null );
		
		System.out.println("Start SplashLogin !!");
		
		if(currentAccessToken != null &&
		   expires != 0 && 
		   userEmail == null && 
		   userPassword == null) {
			System.out.println("SplashLogin 1 !!");
	        facebook.setAccessToken(currentAccessToken);
	        facebook.setAccessExpires(expires);
	        
			if( facebook.isSessionValid() ){
				System.out.println("Splash 1 facebook valid !!");
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "facebookLogIn" );
				paramMap.put( "accessToken", facebook.getAccessToken() );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( loginURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						System.out.println("Splash 1 success : "+jsonObject);
						try {
							app.setUserId( jsonObject.getString( "id" ) );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
							System.out.println("SetActorDataSuccess!!");
							Intent intent = new Intent( SplashActivity.this, MainActivity.class );
							SplashActivity.this.startActivity( intent );
						} catch (JSONException e) {
							e.printStackTrace();
							clearData();
							Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
							SplashActivity.this.startActivity( intent );
						}
					}
					
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						System.out.println("Splash 1.1 fail : "+arg1);
						clearData();
						Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
						SplashActivity.this.startActivity( intent );
					}
					
					@Override
					public void onFailure(Throwable arg0, JSONObject jsonObject) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, jsonObject);
						System.out.println("Splash 1.2 fail : "+jsonObject);
						clearData();
						Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
						SplashActivity.this.startActivity( intent );
					}
				});
			}else{
				clearData();
				Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
				SplashActivity.this.startActivity( intent );
			}
		}else if( userEmail != null && 
				  userPassword != null && 
				  currentAccessToken == null &&
				  expires == 0 ){
			System.out.println("SplashLogin 2 !!");
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "email", userEmail );
			paramMap.put( "password", userPassword );
			paramMap.put( "rememberme", "true" );
			paramMap.put( "_a", "logIn" );
			RequestParams params = new RequestParams(paramMap);
			
			asyncHttpClient.post( loginURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						app.setUserId( jsonObject.getString( "id" ) );
						ActorData actorData = new ActorData( jsonObject );
						app.setCurrentProfileData( actorData );
						Intent intent = new Intent( SplashActivity.this, MainActivity.class );
						SplashActivity.this.startActivity( intent );
					} catch (JSONException e) {
						e.printStackTrace();
						clearData();
						Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
						SplashActivity.this.startActivity( intent );
					}
				}
				
				@Override
				public void onFailure(Throwable arg0, JSONObject arg1) {
					super.onFailure(arg0, arg1);
					clearData();
					Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
					SplashActivity.this.startActivity( intent );
				}
			});
		}else if( userEmail != null && 
				  userPassword != null && 
				  currentAccessToken != null &&
				  expires != 0 ){
			System.out.println("SplashLogin 3 !!");
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "email", userEmail );
			paramMap.put( "password", userPassword );
			paramMap.put( "rememberme", "true" );
			paramMap.put( "_a", "logIn" );
			RequestParams params = new RequestParams(paramMap);
			
			asyncHttpClient.post( loginURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						app.setUserId( jsonObject.getString( "id" ) );
						ActorData actorData = new ActorData( jsonObject );
						app.setCurrentProfileData( actorData );
						Intent intent = new Intent( SplashActivity.this, MainActivity.class );
						SplashActivity.this.startActivity( intent );
					} catch (JSONException e) {
						e.printStackTrace();
						facebook.setAccessToken(currentAccessToken);
				        facebook.setAccessExpires(expires);
				        
						if( facebook.isSessionValid() ){
							HashMap<String, String> paramMap = new HashMap<String, String>();
							paramMap.put( "_a", "facebookLogIn" );
							paramMap.put( "accessToken", facebook.getAccessToken() );
							RequestParams params = new RequestParams(paramMap);
							asyncHttpClient.post( loginURL, params, new JsonHttpResponseHandler(){
								@Override
								public void onSuccess(JSONObject jsonObject) {
									super.onSuccess(jsonObject);
									
									try {
										app.setUserId( jsonObject.getString( "id" ) );
										ActorData actorData = new ActorData( jsonObject );
										app.setCurrentProfileData( actorData );
										Intent intent = new Intent( SplashActivity.this, MainActivity.class );
										SplashActivity.this.startActivity( intent );
									} catch (JSONException e) {
										e.printStackTrace();
										clearData();
										Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
										SplashActivity.this.startActivity( intent );
									}
								}
							});
						}else{
							clearData();
							Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
							SplashActivity.this.startActivity( intent );
						}
					}
				}
			});
		}else{
			System.out.println("SplashLogin 4 !!");
			clearData();
			Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
			SplashActivity.this.startActivity( intent );
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown( keyCode, event );
	}
	
	private void clearSharePrefAppValue(){
		SharedPreferences galleryFilterPref = this.getSharedPreferences( GalleryFilterActivity.SHARE_PREF_GALLERY_FILTER, this.MODE_PRIVATE );
		SharedPreferences.Editor galleryFilterPrefEditor = galleryFilterPref.edit();
		galleryFilterPrefEditor.putString( GalleryFilterActivity.SHARE_PREF_KEY_LOCATION, null );
		galleryFilterPrefEditor.putString( GalleryFilterActivity.SHARE_PREF_KEY_SORT, null );
		galleryFilterPrefEditor.putString( GalleryFilterActivity.SHARE_PREF_KEY_LOCATION_CITY_ID, null );
        galleryFilterPrefEditor.commit();
        
        SharedPreferences shareFilterPref = this.getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY, this.MODE_PRIVATE );
		SharedPreferences.Editor shareFilterPrefEditor = shareFilterPref.edit();
		shareFilterPrefEditor.putString( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_ID, null );
		shareFilterPrefEditor.putString( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_NAME, null );
		shareFilterPrefEditor.commit();
	}
	
	private void loadAppDefaultData(){
		//Metro data
		asyncHttpClient.get( 
		    	config.get( URL_GET_METROS ).toString(),
		    	new JsonHttpResponseHandler(){
		    		@Override
		    		public void onSuccess(JSONObject jsonObject) {
		    			super.onSuccess(jsonObject);
		    			try {
		    				JSONArray newArray = jsonObject.getJSONArray("entities");
		    				//Load Metro Data
		    				for(int i = 0; i<newArray.length(); i++){
		    					MetroData newData = new MetroData( (JSONObject) newArray.get(i) );
		    					arrayMetroName.add( newData );
		    				}
		    				
		    				app.setAppArrayMetroData( arrayMetroName );
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		    		}
		    	}
		);
		
		//Share category data.
		String getCategoryData = config.get( MyApp.URL_DEFAULT ).toString()+"categories.json?page.number=1&page.size=32";
		asyncHttpClient.get( getCategoryData, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				try {
					JSONArray newArray = jsonObject.getJSONArray( "entities" );
					for(int i = 0; i<newArray.length(); i++){
						//Load Category Data
						if( newArray.optJSONObject( i ) != null ){
							CategoryData categoryData = new CategoryData( newArray.optJSONObject( i ) );
							arrayDefaultCategoryData.add( categoryData );
						}
					}
					
					app.setAppArrayProductCategoryData( arrayDefaultCategoryData );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void clearData(){
		//Clear Data
		SharedPreferences loginPref = this.getSharedPreferences( SplashActivity.SHARE_PREF_USER_INFO, this.MODE_PRIVATE );
		SharedPreferences.Editor prefsEditor = loginPref.edit();
		prefsEditor.putString( SplashActivity.SHARE_PREF_KEY_USER_EMAIL, null );
		prefsEditor.putString( SplashActivity.SHARE_PREF_KEY_USER_PASSWORD, null );
        prefsEditor.commit();
        
        SharedPreferences facebookPref = this.getSharedPreferences( SplashActivity.SHARE_PREF_USER_INFO, this.MODE_PRIVATE );
		SharedPreferences.Editor facebookPrefsEditor = facebookPref.edit();
		facebookPrefsEditor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
		facebookPrefsEditor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0 );
        prefsEditor.commit();
        
        try {
			facebook.logout( this );
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		app.setUserId( null );
		app.setCurrentProfileData( null );
	}
}
