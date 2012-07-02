package com.codegears.getable.ui.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

public class ProductSetupSocialShareLayout extends AbstractViewLayout implements OnClickListener {
	
	private MyApp app;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	private TextView facebookConfigText;
	private TextView twitterConfigText;
	private Facebook facebook;
	private String connectFacebookURL;
	private String getCurrentUserDataURL;
	private ToggleButton facebookConfigButton;
	private ToggleButton twitterConfigButton;
	private ProgressDialog loadingDialog;
	private ActorData currentActorData;
	private int facebookButtonStatus;
	private int twitterButtonStatus;
	
	public ProductSetupSocialShareLayout(Activity activity) {
		super(activity);
		this.inflate( this.getContext(), R.layout.productsetupsocialsharelayout, this );
		
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		facebook = app.getFacebook();
		currentActorData = app.getCurrentProfileData();
		
		SharedPreferences socialPrefs = this.getActivity().getSharedPreferences( MyApp.SHARE_PREF_SOCIAL_STATUS_BUTTON, this.getActivity().MODE_PRIVATE );
		facebookButtonStatus = socialPrefs.getInt( MyApp.SHARE_PREF_KEY_FACEBOOK_BUTTON_STATUS, 0 );
		twitterButtonStatus = socialPrefs.getInt( MyApp.SHARE_PREF_KEY_TWITTER_BUTTON_STATUS, 0 );
		
		facebookConfigButton = (ToggleButton) findViewById( R.id.productSetupSocialShareFacebookConfigButton );
		facebookConfigText = (TextView) findViewById( R.id.productSetupSocialShareFacebookConfigText );
		twitterConfigButton = (ToggleButton) findViewById( R.id.productSetupSocialShareTwitterConfigButton );
		twitterConfigText = (TextView) findViewById( R.id.productSetupSocialShareTwitterConfigText );
		
		facebookConfigButton.setOnClickListener( this );
		twitterConfigButton.setOnClickListener( this );
		facebookConfigText.setOnClickListener( this );
		twitterConfigText.setOnClickListener( this );
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		setViewFromData();
		
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

	@Override
	public void onClick(View v) {
		if( v.equals( facebookConfigButton ) ){
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
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ProductSetupSocialShareLayout.this.getActivity() );
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
			
		}
	}

}
