package com.codegears.getable.ui.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class ProductSocialShareLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_SOCAIL_SHARE = "SHARE_PREF_SOCAIL_SHARE";
	public static final String SHARE_PREF_KEY_ACTIVITY_ID = "SHARE_PREF_KEY_ACTIVITY_ID";
	
	public static final String SHARE_PREF_SOCIAL_TYPE = "SHARE_PREF_SOCIAL_TYPE";
	public static final int SHARE_PREF_SOCIAL_TYPE_FACEBOOK = 1;
	public static final int SHARE_PREF_SOCIAL_TYPE_TWITTER = 2;
	
	public static final int MAX_TWITTER_LENGHT = 140;
	
	private Button shareButton;
	private EditText shareEditText;
	private String activityId;
	private int socialType;
	private MyApp app;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	private String shareFacebookURL;
	private String shareTwitterURL;
	private BodyLayoutStackListener listener;
	private ProgressDialog loadingDialog;
	private ImageButton backButton;
	
	public ProductSocialShareLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.productsocialsharelayout, this );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_SOCAIL_SHARE, this.getActivity().MODE_PRIVATE );
		activityId = myPrefs.getString( SHARE_PREF_KEY_ACTIVITY_ID, null );
		socialType = myPrefs.getInt( SHARE_PREF_SOCIAL_TYPE, 0 );
		
		app = (MyApp) this.getActivity().getApplication();
		config = new Config( this.getContext() );
		asyncHttpClient = app.getAsyncHttpClient();
		
		shareButton = (Button) findViewById( R.id.productSocialShareLayoutShareButton );
		shareEditText = (EditText) findViewById( R.id.productSocialShareLayoutShareText );
		backButton = (ImageButton) findViewById( R.id.productSocialShareLayoutBackButton );
		
		if( socialType == SHARE_PREF_SOCIAL_TYPE_TWITTER ){
			shareEditText.setMaxLines( MAX_TWITTER_LENGHT );
		}
		
		this.post( new Runnable() {
			@Override
			public void run() {
				shareEditText.requestFocus();
			}
		});
		
		shareButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		shareFacebookURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/social/facebook/sharings.json";
		shareTwitterURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/social/twitter/sharings.json";
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if( v.equals( shareButton ) ){
			loadingDialog.show();
			
			String shareURL = null;
			if( socialType == SHARE_PREF_SOCIAL_TYPE_FACEBOOK ){
				shareURL = shareFacebookURL;
			}else if( socialType == SHARE_PREF_SOCIAL_TYPE_TWITTER ){
				shareURL = shareTwitterURL;
			}
			
			if( shareURL != null ){
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "activityId", activityId );
				paramMap.put( "message", shareEditText.getText().toString() );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( shareURL, params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
						
						if(listener != null){
							listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT_BACK );
						}
					}
				});
			}
		}else if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( shareEditText.getWindowToken(), 0 );
			
			if (listener != null) {
				listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_BACK_BUTTON);
			}
		}
	}

}
