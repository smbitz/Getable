package com.codegears.getable.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.ProfileSettingNotificationButton;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.codegears.getable.util.TwitterApp;
import com.codegears.getable.util.TwitterApp.TwDialogListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;

public class ProfileChangeSettingsLayout extends AbstractViewLayout implements OnClickListener {
	
	private BodyLayoutStackListener listener;
	private LinearLayout changePasswordButton;
	private ProfileSettingNotificationButton notificationFollowPushButton;
	private ProfileSettingNotificationButton notificationFollowEmailButton;
	private ProfileSettingNotificationButton notificationCommentPushButton;
	private ProfileSettingNotificationButton notificationCommentEmailButton;
	private ProfileSettingNotificationButton notificationLikePushButton;
	private ProfileSettingNotificationButton notificationLikeEmailButton;
	private MyApp app;
	//private List<String> appCookie;
	private Config config;
	private ActorData currentActorData;
	private String changeNotificationSettingURL;
	private ProgressDialog loadingDialog;
	private AsyncHttpClient asyncHttpClient;
	private ToggleButton facebookConfigButton;
	private ToggleButton twitterConfigButton;
	private TextView facebookConfigText;
	private TextView twitterConfigText;
	private Facebook facebook;
	private TwitterApp twitter;
	private int facebookButtonStatus;
	private int twitterButtonStatus;
	private String getCurrentUserDataURL;
	private String connectFacebookURL;
	
	public ProfileChangeSettingsLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.profilechangesettingslayout, this );
		
		SharedPreferences socialPrefs = this.getActivity().getSharedPreferences( MyApp.SHARE_PREF_SOCIAL_STATUS_BUTTON, this.getActivity().MODE_PRIVATE );
		facebookButtonStatus = socialPrefs.getInt( MyApp.SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS, 0 );
		twitterButtonStatus = socialPrefs.getInt( MyApp.SHARE_PREF_KEY_TWITTER_BUTTON_STATUS, 0 );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		facebook = app.getFacebook();
		twitter = new TwitterApp( this.getContext(), MyApp.TWITTER_CONSUMER_KEY, MyApp.TWITTER_SECRET_KEY );
		currentActorData = app.getCurrentProfileData();
		
		changePasswordButton = (LinearLayout) findViewById( R.id.profileChangeSettingChangePasswordButton );
		notificationFollowPushButton = (ProfileSettingNotificationButton) findViewById( R.id.profileChangeSettingsFollowPushButton );
		notificationFollowEmailButton = (ProfileSettingNotificationButton) findViewById( R.id.profileChangeSettingsFollowEmailButton );
		notificationCommentPushButton = (ProfileSettingNotificationButton) findViewById( R.id.profileChangeSettingsCommentPushButton );
		notificationCommentEmailButton = (ProfileSettingNotificationButton) findViewById( R.id.profileChangeSettingsCommentEmailButton );
		notificationLikePushButton = (ProfileSettingNotificationButton) findViewById( R.id.profileChangeSettingsLikePushButton );
		notificationLikeEmailButton = (ProfileSettingNotificationButton) findViewById( R.id.profileChangeSettingsLikeEmailButton );
		facebookConfigButton = (ToggleButton) findViewById( R.id.profileChangeSettingFacebookConfigButton );
		facebookConfigText = (TextView) findViewById( R.id.profileChangeSettingFacebookConfigText );
		twitterConfigButton = (ToggleButton) findViewById( R.id.profileChangeSettingTwitterConfigButton );
		twitterConfigText = (TextView) findViewById( R.id.profileChangeSettingTwitterConfigText );
		
		changePasswordButton.setOnClickListener( this );
		notificationFollowPushButton.setOnClickListener( this );
		notificationFollowEmailButton.setOnClickListener( this );
		notificationCommentPushButton.setOnClickListener( this );
		notificationCommentEmailButton.setOnClickListener( this );
		notificationLikePushButton.setOnClickListener( this );
		notificationLikeEmailButton.setOnClickListener( this );
		facebookConfigButton.setOnClickListener( this );
		twitterConfigButton.setOnClickListener( this );
		facebookConfigText.setOnClickListener( this );
		twitterConfigText.setOnClickListener( this );
		
		twitter.setListener( new TwDialogListener() {
			@Override
			public void onError(String value) {
				
			}
			
			@Override
			public void onComplete(String value) {
				
			}
		});
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		setViewFromData();
		
		getCurrentUserDataURL = config.get( MyApp.URL_GET_CURRENT_USER_DATA ).toString();
		changeNotificationSettingURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/notification-settings.json";
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
		
		//Set notification button
		//Set push button
		if( currentActorData.getNotificationSettings().getPushNotificationSetting().getFollowSetting() ){
			notificationFollowPushButton.setImageResource( R.drawable.notification_icon_push_2 );
			notificationFollowPushButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
		}else{
			notificationFollowPushButton.setImageResource( R.drawable.notification_icon_push_1 );
			notificationFollowPushButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
		}
		
		if( currentActorData.getNotificationSettings().getPushNotificationSetting().getCommentSetting() ){
			notificationCommentPushButton.setImageResource( R.drawable.notification_icon_push_2 );
			notificationCommentPushButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
		}else{
			notificationCommentPushButton.setImageResource( R.drawable.notification_icon_push_1 );
			notificationCommentPushButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
		}

		if( currentActorData.getNotificationSettings().getPushNotificationSetting().getLikeSetting() ){
			notificationLikePushButton.setImageResource( R.drawable.notification_icon_push_2 );
			notificationLikePushButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
		}else{
			notificationLikePushButton.setImageResource( R.drawable.notification_icon_push_1 );
			notificationLikePushButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
		}
		
		//Set email button
		if( currentActorData.getNotificationSettings().getEmailNotificationSetting().getFollowSetting() ){
			notificationFollowEmailButton.setImageResource( R.drawable.notification_icon_email_2 );
			notificationFollowEmailButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
		}else{
			notificationFollowEmailButton.setImageResource( R.drawable.notification_icon_email_1 );
			notificationFollowEmailButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
		}
		
		if( currentActorData.getNotificationSettings().getEmailNotificationSetting().getCommentSetting() ){
			notificationCommentEmailButton.setImageResource( R.drawable.notification_icon_email_2 );
			notificationCommentEmailButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
		}else{
			notificationCommentEmailButton.setImageResource( R.drawable.notification_icon_email_1 );
			notificationCommentEmailButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
		}

		if( currentActorData.getNotificationSettings().getEmailNotificationSetting().getLikeSetting() ){
			notificationLikeEmailButton.setImageResource( R.drawable.notification_icon_email_2 );
			notificationLikeEmailButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
		}else{
			notificationLikeEmailButton.setImageResource( R.drawable.notification_icon_email_1 );
			notificationLikeEmailButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
		}
	}
	
	private void onChangeNotificationSettingURLSuccess(){
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
		}
	}
	
	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
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
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if( v.equals( changePasswordButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_SETTINGS_CHANGE_PASSWORD );
			}
		}else if( v.equals( notificationFollowPushButton ) ){
			loadingDialog.show();
			ProfileSettingNotificationButton notificationButton = (ProfileSettingNotificationButton) v;
			if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "followed", "false" );
		        newMapData.put( "_followed", "on" );
		        newMapData.put( "channel", "2" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "followed", "false" );
				paramMap.put( "_followed", "on" );
				paramMap.put( "channel", "2" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
				notificationButton.setImageResource( R.drawable.notification_icon_push_1 );
			}else if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF  ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "followed", "true" );
		        newMapData.put( "_followed", "on" );
		        newMapData.put( "channel", "2" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "followed", "true" );
				paramMap.put( "_followed", "on" );
				paramMap.put( "channel", "2" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
				notificationButton.setImageResource( R.drawable.notification_icon_push_2 );
			}
		}else if( v.equals( notificationFollowEmailButton ) ){
			loadingDialog.show();
			ProfileSettingNotificationButton notificationButton = (ProfileSettingNotificationButton) v;
			if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "followed", "false" );
		        newMapData.put( "_followed", "on" );
		        newMapData.put( "channel", "1" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "followed", "false" );
				paramMap.put( "_followed", "on" );
				paramMap.put( "channel", "1" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
				notificationButton.setImageResource( R.drawable.notification_icon_email_1 );
			}else if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "followed", "true" );
		        newMapData.put( "_followed", "on" );
		        newMapData.put( "channel", "1" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "followed", "true" );
				paramMap.put( "_followed", "on" );
				paramMap.put( "channel", "1" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
				notificationButton.setImageResource( R.drawable.notification_icon_email_2 );
			}
		}else if( v.equals( notificationCommentPushButton ) ){
			loadingDialog.show();
			ProfileSettingNotificationButton notificationButton = (ProfileSettingNotificationButton) v;
			if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "commented", "false" );
		        newMapData.put( "_commented", "on" );
		        newMapData.put( "channel", "2" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "commented", "false" );
				paramMap.put( "_commented", "on" );
				paramMap.put( "channel", "2" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
				notificationButton.setImageResource( R.drawable.notification_icon_push_1 );
			}else if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "commented", "true" );
		        newMapData.put( "_commented", "on" );
		        newMapData.put( "channel", "2" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "commented", "true" );
				paramMap.put( "_commented", "on" );
				paramMap.put( "channel", "2" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
				notificationButton.setImageResource( R.drawable.notification_icon_push_2 );
			}
		}else if( v.equals( notificationCommentEmailButton ) ){
			loadingDialog.show();
			ProfileSettingNotificationButton notificationButton = (ProfileSettingNotificationButton) v;
			if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "commented", "false" );
		        newMapData.put( "_commented", "on" );
		        newMapData.put( "channel", "1" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "commented", "false" );
				paramMap.put( "_commented", "on" );
				paramMap.put( "channel", "1" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
				notificationButton.setImageResource( R.drawable.notification_icon_email_1 );
			}else if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "commented", "true" );
		        newMapData.put( "_commented", "on" );
		        newMapData.put( "channel", "1" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "commented", "true" );
				paramMap.put( "_commented", "on" );
				paramMap.put( "channel", "1" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
				notificationButton.setImageResource( R.drawable.notification_icon_email_2 );
			}
		}else if( v.equals( notificationLikePushButton ) ){
			loadingDialog.show();
			ProfileSettingNotificationButton notificationButton = (ProfileSettingNotificationButton) v;
			if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "liked", "false" );
		        newMapData.put( "_liked", "on" );
		        newMapData.put( "channel", "2" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "liked", "false" );
				paramMap.put( "_liked", "on" );
				paramMap.put( "channel", "2" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
				notificationButton.setImageResource( R.drawable.notification_icon_push_1 );
			}else if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "liked", "true" );
		        newMapData.put( "_liked", "on" );
		        newMapData.put( "channel", "2" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "liked", "true" );
				paramMap.put( "_liked", "on" );
				paramMap.put( "channel", "2" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);

				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
				notificationButton.setImageResource( R.drawable.notification_icon_push_2 );
			}
		}else if( v.equals( notificationLikeEmailButton ) ){
			loadingDialog.show();
			ProfileSettingNotificationButton notificationButton = (ProfileSettingNotificationButton) v;
			if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "liked", "false" );
		        newMapData.put( "_liked", "on" );
		        newMapData.put( "channel", "1" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "liked", "false" );
				paramMap.put( "_liked", "on" );
				paramMap.put( "channel", "1" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF );
				notificationButton.setImageResource( R.drawable.notification_icon_email_1 );
			}else if( notificationButton.getButtonStatus() == ProfileSettingNotificationButton.NOTIFICATION_STATUS_OFF ){
				/*Map< String, String > newMapData = new HashMap<String, String>();
		        newMapData.put( "liked", "true" );
		        newMapData.put( "_liked", "on" );
		        newMapData.put( "channel", "1" );
		        newMapData.put( "_a", "put" );
		        String postData = NetworkUtil.createPostData( newMapData );
				NetworkThreadUtil.getRawDataWithCookie( changeNotificationSettingURL, postData, appCookie, this );*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "liked", "true" );
				paramMap.put( "_liked", "on" );
				paramMap.put( "channel", "1" );
				paramMap.put( "_a", "put" );
				RequestParams params = new RequestParams(paramMap);
				
				asyncHttpClient.post( changeNotificationSettingURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							//Check data.
							String userId = jsonObject.getString( "id" );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						onChangeNotificationSettingURLSuccess();
					}
				});
				
				notificationButton.setButtonStatus( ProfileSettingNotificationButton.NOTIFICATION_STATUS_ON );
				notificationButton.setImageResource( R.drawable.notification_icon_email_2 );
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
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ProfileChangeSettingsLayout.this.getActivity() );
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
								refreshView();
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
						refreshView();
					}
				});
			}
		}else if( v.equals( twitterConfigText ) ){
			if( !(twitter.hasAccessToken()) ){
				twitter.authorize();
			}
		}
	}
	
	
	
}
