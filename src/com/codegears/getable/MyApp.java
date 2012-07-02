package com.codegears.getable;

import java.util.List;

import org.apache.http.protocol.BasicHttpContext;

import twitter4j.Twitter;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.util.TwitterApp;
import com.facebook.android.Facebook;
import com.loopj.android.http.AsyncHttpClient;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApp extends Application {
	
	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String URL_GET_PRODUCT_ACTIVITIES_BY_ID = "URL_GET_PRODUCT_ACTIVITIES_BY_ID";
	public static final String URL_GET_CURRENT_USER_DATA = "URL_GET_CURRENT_USER_DATA";
	public static final String DEFAULT_URL_VAR_1 = "?page.number=1&page.size=32";
	
	public static final String FACEBOOK_APP_ID = "356831891057227";
	public static final String TWITTER_CONSUMER_KEY = "TWCMwpOklHUm5Aa6iRQDQ";
	public static final String TWITTER_SECRET_KEY = "kEt7VdEVFqbnLBXhQxycWQCK7aiWZWMpOje2B5aSpI";
	
	public static String PROFILE_GENDER_MALE_TEXT = "MALE";
	public static String PROFILE_GENDER_FEMALE_TEXT = "FEMALE";
	public static String PROFILE_GENDER_NOT_SAY_TEXT = "";
	public static String PROFILE_GENDER_MALE_ID = "1";
	public static String PROFILE_GENDER_FEMALE_ID = "2";
	
	public static String CONNECT_FACEBOOK_URL = "http://wongnaimedia.dyndns.org/getable/me/integrations/facebook.json";
	
	public static final String SHARE_PREF_SOCIAL_STATUS_BUTTON = "SHARE_PREF_FACEBOOK_STATUS_BUTTON";
	public static final String SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS = "SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS";
	public static final int FACEBOOK_BUTTON_STATUS_ON = 1;
	public static final int FACEBOOK_BUTTON_STATUS_OFF = 2;
	private static final int FACEBOOK_BUTTON_STATUS_EMPTY = 3;
	public static final String SHARE_PREF_KEY_TWITTER_BUTTON_STATUS = "SHARE_PREF_KEY_TWITTER_BUTTON_STATUS";
	public static final int TWITTER_BUTTON_STATUS_ON = 1;
	public static final int TWITTER_BUTTON_STATUS_OFF = 2;
	private static final int TWITTER_BUTTON_STATUS_EMPTY = 3;
	
	private List<String> appCookie;
	private String userId;
	private BasicHttpContext basicHttpContext;
	private ActorData currentProfileData;
	private AsyncHttpClient asyncHttpClient;
	private Facebook facebook;
	
	public MyApp() {
		asyncHttpClient = new AsyncHttpClient();
		facebook = new Facebook( FACEBOOK_APP_ID );
	}
	
	public void setAppCookie( List<String> setValue ){
		appCookie = setValue;
	}

	public void setUserId(String string) {
		userId = string;
	}
	
	public void setAppBasicHttpContext(BasicHttpContext setBasicHttpContext) {
		basicHttpContext = setBasicHttpContext;
	}
	
	public List<String> getAppCookie(){
		return appCookie;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public BasicHttpContext getAppBasicHttpContext() {
		return basicHttpContext;
	}

	public void setCurrentProfileData(ActorData actorData) {
		currentProfileData = actorData;
	}
	
	public ActorData getCurrentProfileData(){
		return currentProfileData;
	}
	
	public AsyncHttpClient getAsyncHttpClient(){
		return asyncHttpClient;
	}
	
	public Facebook getFacebook(){
		return facebook;
	}
	
	public void clearSharePreferences(){
		SharedPreferences socialPref = this.getSharedPreferences( MyApp.SHARE_PREF_SOCIAL_STATUS_BUTTON, this.MODE_PRIVATE );
		SharedPreferences.Editor socialPrefsEditor = socialPref.edit();
		socialPrefsEditor.putInt( MyApp.SHARE_PREF_KEY_TWITTER_BUTTON_STATUS, MyApp.TWITTER_BUTTON_STATUS_EMPTY );
		socialPrefsEditor.putInt( MyApp.SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS, MyApp.FACEBOOK_BUTTON_STATUS_EMPTY );
		socialPrefsEditor.commit();
	}
	
}
