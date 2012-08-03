package com.codegears.getable.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	private ImageButton backButton;
	private TextView accountText;
	private TextView changePssswordText;
	private TextView sharingText;
	private TextView facebookText;
	private TextView twitterText;
	private TextView notificationText;
	private TextView followingText;
	private TextView commentText;
	private TextView likeText;
	private ImageView facebookArrowImage;
	private ImageView twitterArrorImage;
	private AlertDialog alertDialog;
	
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
		alertDialog = new AlertDialog.Builder( this.getContext() ).create();
		
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
		backButton = (ImageButton) findViewById( R.id.profileChangeSettingBackButton );
		accountText = (TextView) findViewById( R.id.profileChangeSettingAccountText );
		changePssswordText = (TextView) findViewById( R.id.profileChangeSettingChangePasswordText );
		sharingText = (TextView) findViewById( R.id.profileChangeSettingSharingText );
		facebookText = (TextView) findViewById( R.id.profileChangeSettingFacebookText );
		twitterText = (TextView) findViewById( R.id.profileChangeSettingTwitterText );
		notificationText = (TextView) findViewById( R.id.profileChangeSettingNotificationText );
		followingText = (TextView) findViewById( R.id.profileChangeSettingFollowingText );
		commentText = (TextView) findViewById( R.id.profileChangeSettingCommentText );
		likeText = (TextView) findViewById( R.id.profileChangeSettingLikeText );
		facebookArrowImage = (ImageView) findViewById( R.id.profileChangeSettingFacebookArrowImage );
		twitterArrorImage = (ImageView) findViewById( R.id.profileChangeSettingTwitterArrowImage );
		
		//Set font
		accountText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		changePssswordText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		sharingText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		facebookText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		twitterText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		notificationText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		followingText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		commentText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		likeText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		facebookConfigText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		twitterConfigText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
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
		backButton.setOnClickListener( this );
		
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
		facebookArrowImage.setVisibility( View.GONE );
		twitterConfigButton.setVisibility( View.GONE );
		twitterConfigText.setVisibility( View.GONE );
		twitterArrorImage.setVisibility( View.GONE );
		
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
			facebookArrowImage.setVisibility( View.VISIBLE );
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
			twitterArrorImage.setVisibility( View.VISIBLE );
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
			facebook.authorize( this.getActivity(), MyApp.FACEBOOK_PERMISSION, new DialogListener() {
				
				@Override
				public void onFacebookError(FacebookError e) { 
					System.out.println("ProfileChangeSettingError1 : "+e);
					alertDialog.setTitle( "Error" );
					alertDialog.setMessage( "Cannot connect facebook." );
					alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
					alertDialog.show();
					
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
				
				@Override
				public void onError(DialogError e) {
					System.out.println("ProfileChangeSettingError2 : "+e);
					alertDialog.setTitle( "Error" );
					alertDialog.setMessage( "Cannot connect facebook." );
					alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
					alertDialog.show();
					
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
				
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
					asyncHttpClient.post( connectFacebookURL, params, new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject jsonObject) {
							super.onSuccess(jsonObject);
							try {
								String checkValue = jsonObject.getString( "status" );
								refreshView();
							} catch (JSONException e) {
								System.out.println("ProfileChangeSettingError3 : "+e);
								e.printStackTrace();
								if( loadingDialog.isShowing() ){
									loadingDialog.dismiss();
								}
								
								JSONObject errorObject;
								try {
									errorObject = jsonObject.getJSONObject( "error" );
									String message = errorObject.optString( "message" );
									alertDialog.setTitle( "Error" );
									alertDialog.setMessage( message );
									alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											alertDialog.dismiss();
										}
									});
									alertDialog.show();
								} catch (JSONException e1) {
									System.out.println("ProfileChangeSettingError4 : "+e);
									// TODO Auto-generated catch block
									e1.printStackTrace();
									alertDialog.setTitle( "Error" );
									alertDialog.setMessage( "Cannot connect facebook." );
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
						}
						
						@Override
						public void onFailure(Throwable arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							System.out.println("ProfileChangeSettingError5 : "+arg1);
							super.onFailure(arg0, arg1);
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
							
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot connect facebook." );
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
							System.out.println("ProfileChangeSettingError6 : "+arg1);
							super.onFailure(arg0, arg1);
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
							
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot connect facebook." );
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
				}
				
				@Override
				public void onCancel() {
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
				
			});
		}else if( v.equals( twitterConfigText ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileLayoutWebView.SHARE_PREF_WEB_VIEW_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProfileLayoutWebView.WEB_VIEW_TYPE, ProfileLayoutWebView.WEB_VIEW_TYPE_CONNECT_TWITTER );
				prefsEditor.putInt( ProfileLayoutWebView.CONNECT_TWITTER_FROM, ProfileLayoutWebView.CONNECT_TWITTER_FROM_SETTINGS );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_WEBVIEW );
			}
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}	
}
