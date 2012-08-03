package com.codegears.getable;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.protocol.BasicHttpContext;

import twitter4j.Twitter;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.CategoryData;
import com.codegears.getable.data.MetroData;
import com.codegears.getable.util.TwitterApp;
import com.facebook.android.Facebook;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;

public class MyApp extends Application {
	
	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String URL_GET_PRODUCT_ACTIVITIES_BY_ID = "URL_GET_PRODUCT_ACTIVITIES_BY_ID";
	public static final String URL_GET_CURRENT_USER_DATA = "URL_GET_CURRENT_USER_DATA";
	public static final String DEFAULT_URL_VAR_1 = "?page.number=1&page.size=32";
	
	//public static final String FACEBOOK_APP_ID = "271755516202361 "; //Real server
	public static final String FACEBOOK_APP_ID = "153927554705045"; //Dev server
	//public static final String FACEBOOK_APP_ID = "356831891057227"; //My test
	public static final String[] FACEBOOK_PERMISSION = new String[]{ "email", "publish_stream", "read_friendlists" };
	//public static final String TWITTER_CONSUMER_KEY = "TWCMwpOklHUm5Aa6iRQDQ"; //My test
	//public static final String TWITTER_SECRET_KEY = "kEt7VdEVFqbnLBXhQxycWQCK7aiWZWMpOje2B5aSpI"; //My test
	public static final String TWITTER_CONSUMER_KEY = "OLNB3nwVwQzY3ZSmfU6mg";
	public static final String TWITTER_SECRET_KEY = "FCU2F4ax1lVLaVwHB7OQdZ4AhsplwHvzqw62RbrYzg";
	
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
	
	public static final String TEMP_IMAGE_DIRECTORY_NAME = "/getable/";
	
	public static final String APP_FONT_PATH = "fonts/GothamRnd-Book.ttf";
	public static final String APP_FONT_PATH_2 = "fonts/GothamRnd-Medium.ttf";
	
	private static final String HTTP_USER_AGENT = "GetableAndroid";
	
	public static final int PROFILE_HEADER_TEXT_NAME_LENGTH = 15;
	public static final int PROFILE_LAYOUT_HEADER_TEXT_NAME_LENGTH = 25;
	
	public static final float IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE = 720f;
	
	private String userId;
	private BasicHttpContext basicHttpContext;
	private ActorData currentProfileData;
	private AsyncHttpClient asyncHttpClient;
	private Facebook facebook;
	private ArrayList<MetroData> arrayMetroData;
	private ArrayList<CategoryData> arrayDefaultCategoryData;
	private PersistentCookieStore appCookieStore;
	
	public MyApp() {
		asyncHttpClient = new AsyncHttpClient();
		facebook = new Facebook( FACEBOOK_APP_ID );
		arrayMetroData = new ArrayList<MetroData>();
		arrayDefaultCategoryData = new ArrayList<CategoryData>();
		asyncHttpClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		asyncHttpClient.setUserAgent( HTTP_USER_AGENT );
	}

	public void setUserId(String string) {
		userId = string;
	}
	
	public void setAppBasicHttpContext(BasicHttpContext setBasicHttpContext) {
		basicHttpContext = setBasicHttpContext;
	}
	
	public void setAppCookie( PersistentCookieStore setCookie ){
		appCookieStore = setCookie;
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

	public void setAppArrayMetroData(ArrayList<MetroData> setArrayMetroData) {
		arrayMetroData = setArrayMetroData;
	}
	
	public void setAppArrayProductCategoryData(ArrayList<CategoryData> setArrayDefaultCategoryData) {
		arrayDefaultCategoryData = setArrayDefaultCategoryData;
	}
	
	public ArrayList<MetroData> getAppArrayMetroData(){
		return arrayMetroData;
	}
	
	public ArrayList<CategoryData> getAppArrayProductCategoryData() {
		return arrayDefaultCategoryData;
	}
	
	public PersistentCookieStore getAppCookie(){
		return appCookieStore;
	}
	
}
