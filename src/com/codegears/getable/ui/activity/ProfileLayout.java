package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.HelpLayout;
import com.codegears.getable.LoginActivity;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.ProfileChangeImagePopupDialog;
import com.codegears.getable.R;
import com.codegears.getable.SplashActivity;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.UserProfileImageLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.codegears.getable.util.ImageLoader;
import com.facebook.android.Facebook;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ProfileLayout extends AbstractViewLayout implements OnClickListener {

	private static final String EMAIL_FEEDBACK = "feedback@getableapp.com";
	
	private BodyLayoutStackListener listener;
	private MyApp app;
	private List<String> appCookie;
	private Config config;
	private ImageView userImage;
	private TextView userName;
	private ImageLoader imageLoader;
	private LinearLayout myProfileButton;
	private LinearLayout findFriendsButton;
	private LinearLayout inviteFriendsButton;
	private LinearLayout editProfileButton;
	private LinearLayout sendFeedBackButton;
	private LinearLayout helpButton;
	private LinearLayout termsButton;
	private LinearLayout policyButton;
	private LinearLayout changeSettingsButton;
	private LinearLayout changeProfilePicButton;
	private LinearLayout logoutButton;
	private String userId;
	private String getUserDataURL;
	private String logoutURL;
	private ActorData actorData;
	private AsyncHttpClient asyncHttpClient;
	private Facebook facebook;
	
	public ProfileLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.profilelayout, this );
		
		app = (MyApp) this.getActivity().getApplication();
		appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getActivity() );
		imageLoader = new ImageLoader( this.getContext() );
		facebook = app.getFacebook();
		
		userImage = (ImageView) findViewById( R.id.profileLayoutUserImage );
		userName = (TextView) findViewById( R.id.profileLayoutUserName );
		myProfileButton = (LinearLayout) findViewById( R.id.profileLayoutMyProfileButton );
		findFriendsButton = (LinearLayout) findViewById( R.id.profileLayoutFindFriendsButton );
		inviteFriendsButton = (LinearLayout) findViewById( R.id.profileLayoutInviteFriendsButton );
		editProfileButton = (LinearLayout) findViewById( R.id.profileLayoutEditProfileButton );
		sendFeedBackButton = (LinearLayout) findViewById( R.id.profileLayoutSendFeedbackButton );
		helpButton = (LinearLayout) findViewById( R.id.profileLayoutHelpButton );
		termsButton = (LinearLayout) findViewById( R.id.profileLayoutTermsButton );
		policyButton = (LinearLayout) findViewById( R.id.profileLayoutPolicyButton );
		changeSettingsButton = (LinearLayout) findViewById( R.id.profileLayoutChangeSettingsButton );
		changeProfilePicButton = (LinearLayout) findViewById( R.id.profileLayoutChangeProfilePicButton );
		logoutButton = (LinearLayout) findViewById( R.id.profileLayoutLogoutButton );
		
		myProfileButton.setOnClickListener( this );
		findFriendsButton.setOnClickListener( this );
		inviteFriendsButton.setOnClickListener( this );
		editProfileButton.setOnClickListener( this );
		sendFeedBackButton.setOnClickListener( this );
		helpButton.setOnClickListener( this );
		termsButton.setOnClickListener( this );
		policyButton.setOnClickListener( this );
		changeSettingsButton.setOnClickListener( this );
		changeProfilePicButton.setOnClickListener( this );
		logoutButton.setOnClickListener( this );
		
		userId = app.getUserId();
		
		getUserDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+".json";
		logoutURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
		
		loadData();
	}
	
	public void loadData(){
		asyncHttpClient.post( getUserDataURL,  new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				super.onSuccess(jsonObject);
				actorData = new ActorData( jsonObject );
				
				if( actorData != null ){
					String userImageURL = actorData.getPicture().getImageUrls().getImageURLT();
					final String userNameText = actorData.getName();
					
					imageLoader.DisplayImage( userImageURL, ProfileLayout.this.getActivity(), userImage, true, asyncHttpClient );

					userName.setText( userNameText );
					
					app.setCurrentProfileData( actorData );
				}
			}
		});
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		loadData();
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}

	/*@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( getUserDataURL ) ){
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject( result );
				actorData = new ActorData( jsonObject );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if( actorData != null ){
				String userImageURL = actorData.getPicture().getImageUrls().getImageURLT();
				final String userNameText = actorData.getName();
				
				imageLoader.DisplayImage( userImageURL, ProfileLayout.this.getActivity(), userImage, true );
				
				this.getActivity().runOnUiThread( new Runnable() {
					@Override
					public void run() {
						userName.setText( userNameText );
					}
				});
				
				app.setCurrentProfileData( actorData );
			}
		}else if( urlString.equals( logoutURL ) ){
			//Send to logout page.
			this.getActivity().finish();
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public void onClick(View v) {
		if( v.equals( myProfileButton ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userId );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
		}else if( v.equals( findFriendsButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS );
			}
		}else if( v.equals( inviteFriendsButton ) ){
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			this.getActivity().startActivityForResult( intent, MainActivity.REQUEST_INVITE_FRIENDS_SELECT_CONTACT );
		}else if( v.equals( editProfileButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_EDIT_PROFILE );
			}
		}else if( v.equals( sendFeedBackButton ) ){
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			String[] recipients = new String[]{ EMAIL_FEEDBACK };
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback from "+actorData.getName());

			emailIntent.setType("text/plain");

			this.getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}else if( v.equals( helpButton ) ){
			Intent intent = new Intent( this.getActivity(), HelpLayout.class );
			this.getActivity().startActivity( intent );
		}else if( v.equals( termsButton ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileLayoutWebView.SHARE_PREF_WEB_VIEW_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProfileLayoutWebView.WEB_VIEW_TYPE, ProfileLayoutWebView.WEB_VIEW_TYPE_TERMS );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_WEBVIEW );
			}
		}else if( v.equals( policyButton ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileLayoutWebView.SHARE_PREF_WEB_VIEW_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProfileLayoutWebView.WEB_VIEW_TYPE, ProfileLayoutWebView.WEB_VIEW_TYPE_POLICY );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_WEBVIEW );
			}
		}else if( v.equals( changeSettingsButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_SETTINGS );
			}
		}else if( v.equals( changeProfilePicButton ) ){
			Intent newIntent = new Intent( this.getActivity(), ProfileChangeImagePopupDialog.class );
			this.getActivity().startActivityForResult( newIntent, MainActivity.REQUEST_USER_CAHNGE_IMAGE );
		}else if( v.equals( logoutButton ) ){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this.getContext() );
			
			// set title
			alertDialogBuilder.setTitle("Logout");
			
			// set dialog message
			alertDialogBuilder
				.setMessage("Would you like to logout?")
				.setCancelable(false)
				.setPositiveButton("Logout",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						HashMap<String, String> paramMap = new HashMap<String, String>();
						paramMap.put( "_a", "logOut" );
						RequestParams params = new RequestParams(paramMap);
						asyncHttpClient.post( logoutURL, params, new AsyncHttpResponseHandler(){
							@Override
							public void onSuccess(String arg0) {
								// TODO Auto-generated method stub
								super.onSuccess(arg0);
								
								//Delete SharedPreferences
								SharedPreferences loginPref = ProfileLayout.this.getActivity().getSharedPreferences( SplashActivity.SHARE_PREF_USER_INFO, ProfileLayout.this.getActivity().MODE_PRIVATE );
								SharedPreferences.Editor prefsEditor = loginPref.edit();
								prefsEditor.putString( SplashActivity.SHARE_PREF_KEY_USER_EMAIL, null );
								prefsEditor.putString( SplashActivity.SHARE_PREF_KEY_USER_PASSWORD, null );
						        prefsEditor.commit();
						        
						        SharedPreferences facebookPref = ProfileLayout.this.getActivity().getSharedPreferences( SplashActivity.SHARE_PREF_USER_INFO, ProfileLayout.this.getActivity().MODE_PRIVATE );
								SharedPreferences.Editor facebookPrefsEditor = facebookPref.edit();
								facebookPrefsEditor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
								facebookPrefsEditor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0 );
						        prefsEditor.commit();
						        
						        try {
									facebook.logout( ProfileLayout.this.getContext() );
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								Intent intent = new Intent( ProfileLayout.this.getContext(), LoginActivity.class );
								ProfileLayout.this.getActivity().startActivity( intent );
							}
						});
					}
				  })
				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
		}
	}

}
