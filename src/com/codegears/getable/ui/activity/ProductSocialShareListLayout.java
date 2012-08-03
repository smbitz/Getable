package com.codegears.getable.ui.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.view.View;
import android.view.View.OnClickListener;

public class ProductSocialShareListLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_VALUE_SHARE_PRODUCT_ACT_ID = "SHARE_PREF_VALUE_SHARE_PRODUCT_ACT_ID";
	public static final String SHARE_PREF_KEY_ACT_ID = "SHARE_PREF_KEY_ACT_ID";
	
	private LinearLayout facebookShareLayout;
	private LinearLayout twitterShareLayout;
	private LinearLayout setupSharingButton;
	private Button facebookShareButton;
	private Button twitterShareButton;
	private BodyLayoutStackListener listener;
	private String activityId;
	private MyApp app;
	private ActorData currentActorData;
	private String getCurrentUserDataURL;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	private ProgressDialog loadingDialog;
	private ImageButton backButton;

	public ProductSocialShareListLayout(Activity activity) {
		super(activity);
		this.inflate( this.getContext(), R.layout.productsocialsharelistlayout, this );
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_SHARE_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
		activityId = myPreferences.getString( SHARE_PREF_KEY_ACT_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		config = new Config( this.getContext() );
		asyncHttpClient = app.getAsyncHttpClient();
		currentActorData = app.getCurrentProfileData();
		
		facebookShareLayout = (LinearLayout) findViewById( R.id.productSocialShareLayoutFacebookShareLayout );
		twitterShareLayout = (LinearLayout) findViewById( R.id.productSocialShareLayoutTwitterShareLayout );
		setupSharingButton = (LinearLayout) findViewById( R.id.productSocialShareLayoutSetupShareButton );
		facebookShareButton = (Button) findViewById( R.id.productSocialShareLayoutFacebookShareButton );
		twitterShareButton = (Button) findViewById( R.id.productSocialShareLayoutTwitterShareButton );
		backButton = (ImageButton) findViewById( R.id.productSocialShareListLayoutBackButton );
		
		setupSharingButton.setOnClickListener( this );
		facebookShareButton.setOnClickListener( this );
		twitterShareButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		setViewFromData();
		
		getCurrentUserDataURL = config.get( MyApp.URL_GET_CURRENT_USER_DATA ).toString();
	}
	
	private void setViewFromData(){
		//Set default view;
		facebookShareLayout.setVisibility( View.GONE );
		twitterShareLayout.setVisibility( View.GONE );
		
		if( currentActorData.getSocialConnections().getFacebook().getStatus() ){
			facebookShareLayout.setVisibility( View.VISIBLE );
		}
		
		if( currentActorData.getSocialConnections().getTwitter().getStatus() ){
			twitterShareLayout.setVisibility( View.VISIBLE );
		}
	}
	
	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		loadingDialog.show();
		
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
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if( v.equals( setupSharingButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SETUP_SHARE_SOCIAL_LAYOUT );
			}
		}else if( v.equals( facebookShareButton ) ){
			if(listener != null){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductSocialShareLayout.SHARE_PREF_SOCAIL_SHARE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductSocialShareLayout.SHARE_PREF_KEY_ACTIVITY_ID, activityId );
				prefsEditor.putInt( ProductSocialShareLayout.SHARE_PREF_SOCIAL_TYPE, ProductSocialShareLayout.SHARE_PREF_SOCIAL_TYPE_FACEBOOK );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_SOCIAL_LAYOUT );
			}
		}else if( v.equals( twitterShareButton ) ){
			if(listener != null){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductSocialShareLayout.SHARE_PREF_SOCAIL_SHARE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductSocialShareLayout.SHARE_PREF_KEY_ACTIVITY_ID, activityId );
				prefsEditor.putInt( ProductSocialShareLayout.SHARE_PREF_SOCIAL_TYPE, ProductSocialShareLayout.SHARE_PREF_SOCIAL_TYPE_TWITTER );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_SOCIAL_LAYOUT );
			}
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}

}
