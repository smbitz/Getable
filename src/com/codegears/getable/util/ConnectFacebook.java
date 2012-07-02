package com.codegears.getable.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.MyApp;
import com.codegears.getable.data.ActorData;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ConnectFacebook {
	
	public static final String FACEBOOK_ACCESS_TOKEN = "FACEBOOK_ACCESS_TOKEN";
	public static final String FACEBOOK_ACCESS_EXPIRES = "FACEBOOK_ACCESS_EXPIRES";
	
	private Activity currentActivity;
	private MyApp app;
	private Config config;
	private String getUserDataURL;
	private String connectFacebookURL;
	private Boolean facebookStatus;
	private Facebook facebook;
	private ActorData actorData;
	private AsyncHttpClient asyncHttpClient;
	
	public ConnectFacebook( Activity activity ) {
		currentActivity = activity;
		app = (MyApp) currentActivity.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( currentActivity );
		facebook = app.getFacebook();//new Facebook( MyApp.FACEBOOK_APP_ID );
		
		String userId = app.getUserId();
		
		getUserDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+".json";
		connectFacebookURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/integrations/facebook.json";
	}
	
	public Boolean checkFacebookStatus(){
		String result = "";
		
		HttpClient httpClient = asyncHttpClient.getHttpClient();
		HttpPost httpPost = new HttpPost( getUserDataURL );
		
		HttpResponse res;
		InputStream is;
		try {
			res = httpClient.execute( httpPost, asyncHttpClient.getHttpContext() );
			is = res.getEntity().getContent();
			BufferedReader bReader = new BufferedReader( new InputStreamReader( is ) );
			String line = bReader.readLine();
			while ( line != null ) {
				result += line;
				line = bReader.readLine();
			}
			
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject( result );
				actorData = new ActorData( jsonObject );
				if( actorData.getSocialConnections() != null ){
					//Connect Success
					return actorData.getSocialConnections().getFacebook().getStatus();
				}else{
					//Connect Fail
					return false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void connectToFacebook(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( currentActivity );
		String currentAccessToken = prefs.getString( FACEBOOK_ACCESS_TOKEN, null );
		long expires = prefs.getLong( FACEBOOK_ACCESS_EXPIRES, 0);
		if(currentAccessToken != null) {
            facebook.setAccessToken(currentAccessToken);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
		
		if( !facebook.isSessionValid() ){
			facebook.authorize( currentActivity, new DialogListener() {
				
				@Override
				public void onFacebookError(FacebookError e) { }
				
				@Override
				public void onError(DialogError e) { }
				
				@Override
				public void onComplete(Bundle values) {
					String token = facebook.getAccessToken();  //get access token
					Long expires = facebook.getAccessExpires();  //get access expire
					saveFacebookAccessToken(token, expires);
					
					HashMap<String, String> paramMap = new HashMap<String, String>();
					paramMap.put( "accessToken", facebook.getAccessToken() );
					paramMap.put( "_a", "connect" );
					RequestParams params = new RequestParams(paramMap);
					asyncHttpClient.post( connectFacebookURL, params, null );
				}
				
				@Override
				public void onCancel() { }
				
			});
		}else{
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "accessToken", facebook.getAccessToken() );
			paramMap.put( "_a", "connect" );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( connectFacebookURL, params, null );
		}
	}
	
	private void saveFacebookAccessToken(String token, Long expires){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( currentActivity );
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString( FACEBOOK_ACCESS_TOKEN , token);
		editor.putLong( FACEBOOK_ACCESS_EXPIRES , expires);
		editor.commit();
	}
	
}