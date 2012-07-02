package com.codegears.getable;

import java.util.HashMap;

import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.activity.ProductDetailLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
	
	private MyApp app;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	private EditText emailEditText;
	private EditText passwordEditText;
	private Button doneButton;
	private String email;
	private String password;
	private Button signUpButton;
	private Button signInUseFacebook;
	private String loginSignUpURL;
	private AlertDialog alertDialog;
	private Facebook facebook;
	private ProgressDialog loadingDialog;
	private Button forgotPasswordButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.loginactivity );
		
		app = (MyApp) this.getApplication();
		config = new Config( this );
		asyncHttpClient = app.getAsyncHttpClient();
		facebook = app.getFacebook();
		alertDialog = new AlertDialog.Builder( this ).create();
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		emailEditText = (EditText) findViewById( R.id.loginActivityIdEditText );
		passwordEditText = (EditText) findViewById( R.id.loginActivityPasswordEditText );
		doneButton = (Button) findViewById( R.id.loginActivityDoneButton );
		signUpButton = (Button) findViewById( R.id.loginActivitySignUpButton );
		signInUseFacebook = (Button) findViewById( R.id.loginActivitySignInUseFacebookButton );
		forgotPasswordButton = (Button) findViewById( R.id.loginActivityForgetPasswordButton );
		
		emailEditText.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if( s.length() == 0 ||
					passwordEditText.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorGrey );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		passwordEditText.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if( s.length() == 0 ||
					emailEditText.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorGrey );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		doneButton.setOnClickListener( this );
		signUpButton.setOnClickListener( this );
		signInUseFacebook.setOnClickListener( this );
		forgotPasswordButton.setOnClickListener( this );
		
		loginSignUpURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
	}

	@Override
	public void onClick(View v) {
		if( v.equals( doneButton ) ){
			loadingDialog.show();
			
			email = emailEditText.getText().toString();
			password = passwordEditText.getText().toString();
			
			//Async httpClient
			AsyncHttpClient asyncHttpClient = app.getAsyncHttpClient();
			PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
			asyncHttpClient.setCookieStore( myCookieStore );
			//asyncHttpClient.
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "email", email );
			paramMap.put( "password", password );
			paramMap.put( "rememberme", "true" );
			paramMap.put( "_a", "logIn" );
			RequestParams params = new RequestParams(paramMap);
			
			asyncHttpClient.post( loginSignUpURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						app.setUserId( jsonObject.getString( "id" ) );
						ActorData actorData = new ActorData( jsonObject );
						app.setCurrentProfileData( actorData );
						
						SharedPreferences loginPref = LoginActivity.this.getSharedPreferences( SplashActivity.SHARE_PREF_USER_INFO, LoginActivity.this.MODE_PRIVATE );
						SharedPreferences.Editor prefsEditor = loginPref.edit();
						prefsEditor.putString( SplashActivity.SHARE_PREF_KEY_USER_EMAIL, email );
						prefsEditor.putString( SplashActivity.SHARE_PREF_KEY_USER_PASSWORD, password );
				        prefsEditor.commit();
						
						Intent intent = new Intent( LoginActivity.this, MainActivity.class );
						LoginActivity.this.startActivity( intent );
					} catch (JSONException e) {
						e.printStackTrace();
						try {
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
							
							JSONObject errorMessageJson = jsonObject.getJSONObject( "error" );
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( errorMessageJson.optString( "message" ) );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				
				@Override
				public void onFailure(Throwable arg0, JSONObject arg1) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, arg1);
				}
			});
		}else if( v.equals( signUpButton ) ){
			Intent intent = new Intent( this, SignUpActivity.class );
			this.startActivity( intent );
		}else if( v.equals( signInUseFacebook ) ){
			loadingDialog.show();
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
			String currentAccessToken = prefs.getString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
			long expires = prefs.getLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0);
			if(currentAccessToken != null) {
	            facebook.setAccessToken(currentAccessToken);
	        }
	        if(expires != 0) {
	            facebook.setAccessExpires(expires);
	        }
			
			if( facebook.isSessionValid() ){
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "facebookLogIn" );
				paramMap.put( "accessToken", facebook.getAccessToken() );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( loginSignUpURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						try {
							app.setUserId( jsonObject.getString( "id" ) );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
							Intent intent = new Intent( LoginActivity.this, MainActivity.class );
							LoginActivity.this.startActivity( intent );
						} catch (JSONException e) {
							e.printStackTrace();
							facebookAuthorizeApp();
						}
					}
				});
			}else{
				facebookAuthorizeApp();
			}
		}else if( v.equals( forgotPasswordButton ) ){
			Intent intent = new Intent( this, ForgotPasswordActvity.class );
			this.startActivity( intent );
		}
	}
	
	private void facebookAuthorizeApp(){
		facebook.authorize( this, new DialogListener() {
			
			@Override
			public void onFacebookError(FacebookError e) { }
			
			@Override
			public void onError(DialogError e) { }
			
			@Override
			public void onComplete(Bundle values) {
				String token = facebook.getAccessToken();  //get access token
				Long expires = facebook.getAccessExpires();  //get access expire
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( LoginActivity.this );
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN , token);
				editor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES , expires);
				editor.commit();
				
				//Connect facebook to server.
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "accessToken", facebook.getAccessToken() );
				paramMap.put( "_a", "connect" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( MyApp.CONNECT_FACEBOOK_URL, params, null );
				
				//Get data from server.
				HashMap<String, String> userParamMap = new HashMap<String, String>();
				userParamMap.put( "_a", "facebookLogIn" );
				userParamMap.put( "accessToken", facebook.getAccessToken() );
				RequestParams userParams = new RequestParams( userParamMap );
				asyncHttpClient.post( loginSignUpURL, userParams, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
						
						try {
							app.setUserId( jsonObject.getString( "id" ) );
							ActorData actorData = new ActorData( jsonObject );
							app.setCurrentProfileData( actorData );
							Intent intent = new Intent( LoginActivity.this, MainActivity.class );
							LoginActivity.this.startActivity( intent );
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
			
			@Override
			public void onCancel() { }
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown( keyCode, event );
	}
	
}
