package com.codegears.getable;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.facebook.android.Facebook;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
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
	
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private Config config;
	private String loginURL;
	private Facebook facebook;
	private String loginSignUpURL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.splashactivity );
		
		app = (MyApp) this.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this );
		facebook = app.getFacebook();
		
		loginURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
		String currentAccessToken = prefs.getString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
		long expires = prefs.getLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0);
		
		SharedPreferences myPrefs = this.getSharedPreferences( SHARE_PREF_USER_INFO, this.MODE_PRIVATE );
		String userEmail = myPrefs.getString( SHARE_PREF_KEY_USER_EMAIL, null );
		String userPassword = myPrefs.getString( SHARE_PREF_KEY_USER_PASSWORD, null );
		
		if(currentAccessToken != null &&
		   expires != 0 && 
		   userEmail == null && 
		   userPassword == null) {
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
							Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
							SplashActivity.this.startActivity( intent );
						}
					}
				});
			}else{
				Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
				SplashActivity.this.startActivity( intent );
			}
		}else if( userEmail != null && 
				  userPassword != null && 
				  currentAccessToken == null &&
				  expires == 0 ){
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
						Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
						SplashActivity.this.startActivity( intent );
					}
				}
			});
		}else if( userEmail != null && 
				  userPassword != null && 
				  currentAccessToken != null &&
				  expires != 0 ){
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
						Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
						SplashActivity.this.startActivity( intent );
					}
				}
			});
		}else{
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
}
