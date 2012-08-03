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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener {
	
	private static final int REQUEST_MAIN_ACTIVITY = 0;
	public static final int RESULT_MAIN_ACTIVITY_BACK = 0;
	private static final CharSequence EMAIL_EXAMPLE_HINT = "steve@example.com";
	
	private MyApp app;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	//private PersistentCookieStore myCookieStore;
	private EditText emailEditText;
	private EditText passwordEditText;
	private Button doneButton;
	private String email;
	private String password;
	private ImageButton signUpButton;
	private ImageButton signInUseFacebook;
	private String loginSignUpURL;
	private AlertDialog alertDialog;
	private Facebook facebook;
	private ProgressDialog loadingDialog;
	private Button forgotPasswordButton;
	private TextView orSignInText;
	private TextView newToGetableText;
	private PersistentCookieStore appCookie;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.loginactivity );
		
		app = (MyApp) this.getApplication();
		config = new Config( this );
		asyncHttpClient = app.getAsyncHttpClient();
		appCookie = app.getAppCookie();
		facebook = app.getFacebook();
		alertDialog = new AlertDialog.Builder( this ).create();
		
		//Check cookie.
		if( appCookie == null ){
			appCookie = new PersistentCookieStore( this );
			asyncHttpClient.setCookieStore( appCookie );
			app.setAppCookie( appCookie );
		}
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		emailEditText = (EditText) findViewById( R.id.loginActivityIdEditText );
		passwordEditText = (EditText) findViewById( R.id.loginActivityPasswordEditText );
		doneButton = (Button) findViewById( R.id.loginActivityDoneButton );
		signUpButton = (ImageButton) findViewById( R.id.loginActivitySignUpButton );
		signInUseFacebook = (ImageButton) findViewById( R.id.loginActivitySignInUseFacebookButton );
		forgotPasswordButton = (Button) findViewById( R.id.loginActivityForgetPasswordButton );
		orSignInText = (TextView) findViewById( R.id.loginActivityOrSignInText );
		newToGetableText = (TextView) findViewById( R.id.loginActivityNewToGetableText );
		
		//Set font
		orSignInText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		newToGetableText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		doneButton.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH_2) );
		emailEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		passwordEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		forgotPasswordButton.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		
		emailEditText.setHint( EMAIL_EXAMPLE_HINT );
		
		//Default done button state.
		if( emailEditText.getText().length() > 0 &&
			passwordEditText.getText().length() > 0 ){
			doneButton.setTextColor( Color.WHITE );
			doneButton.setEnabled( true );
		}else{
			doneButton.setTextColor( R.color.NameColorGrey );
			doneButton.setEnabled( false );
		}
		
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
			InputMethodManager imm = (InputMethodManager) this.getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( emailEditText.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( passwordEditText.getWindowToken(), 0 );
			
			loadingDialog.show();
			
			email = emailEditText.getText().toString();
			password = passwordEditText.getText().toString();
			
			//Async httpClient
			//asyncHttpClient.setCookieStore( myCookieStore );
			
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
						LoginActivity.this.startActivityForResult( intent, REQUEST_MAIN_ACTIVITY );
					} catch (JSONException e) {
						e.printStackTrace();
						System.out.println("LoginFail1 : "+e);
						try {
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
							
							JSONObject errorMessageJson = jsonObject.getJSONObject( "error" );
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Incorrect email or password for "+email );
							alertDialog.setButton( "Done", new DialogInterface.OnClickListener() {
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
							System.out.println("LoginFail2 : "+e1);
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
			
			/*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
			final String currentAccessToken = prefs.getString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN, null );
			final long expires = prefs.getLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES, 0);
			
			facebook.setAccessToken(currentAccessToken);
	        facebook.setAccessExpires(expires);
	        
			if( facebook.isSessionValid() ){
				//Connect facebook to server.
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "accessToken", facebook.getAccessToken() );
				paramMap.put( "_a", "connect" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( MyApp.CONNECT_FACEBOOK_URL, params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						getDataFromServer();
					}
				});
			}else{*/
				facebookAuthorize();
				//getDataFromServer();
			//}
		}else if( v.equals( forgotPasswordButton ) ){
			Intent intent = new Intent( this, ForgotPasswordActvity.class );
			this.startActivity( intent );
		}
	}
	
	private void facebookAuthorize(){
		System.out.println("FacebookAuthorize!!");
		
		facebook.authorize( this, MyApp.FACEBOOK_PERMISSION, new DialogListener() {
			
			@Override
			public void onFacebookError(FacebookError e) { 
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				System.out.println("LoginFail3 : "+e);
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Cannot login with facebook." );
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
			public void onError(DialogError e) {
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				System.out.println("LoginFail4 : "+e);
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Cannot login with facebook." );
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
			public void onComplete(Bundle values) {
				String token = facebook.getAccessToken();  //get access token
				Long expires = facebook.getAccessExpires();  //get access expire
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( LoginActivity.this );
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN , token);
				editor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES , expires);
				editor.commit();
				
				getDataFromServer();
			}
			
			@Override
			public void onCancel() { 
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
			}
			
		});
	}
	
	private void connectFacebook(){
		//Connect facebook to server.
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put( "accessToken", facebook.getAccessToken() );
		paramMap.put( "_a", "connect" );
		RequestParams params = new RequestParams(paramMap);
		/*asyncHttpClient.post( MyApp.CONNECT_FACEBOOK_URL, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				System.out.println("ConnectSuccess!!");
				getDataFromServer();
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				System.out.println("ConnectFail!!");
			}
		});*/
		asyncHttpClient.post( MyApp.CONNECT_FACEBOOK_URL, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				try {
					String checkValue = jsonObject.getString( "status" );
					//getDataFromServer();
					Intent intent = new Intent( LoginActivity.this, MainActivity.class );
					LoginActivity.this.startActivityForResult( intent, REQUEST_MAIN_ACTIVITY );
				} catch (JSONException e) {
					e.printStackTrace();
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
					System.out.println("LoginFail5 : "+e);
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
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("LoginFail6 : "+e1);
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot sign in using Facebook." );
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
			public void onFailure(Throwable arg0, JSONObject jsonObject) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, jsonObject);
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				System.out.println("LoginFail7 : "+jsonObject);
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Sign in using Facebook fail." );
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
	
	private void getDataFromServer(){
		//Get data from server.
		HashMap<String, String> userParamMap = new HashMap<String, String>();
		userParamMap.put( "_a", "facebookLogIn" );
		userParamMap.put( "accessToken", facebook.getAccessToken() );
		RequestParams userParams = new RequestParams( userParamMap );
		asyncHttpClient.post( loginSignUpURL, userParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);

				try {
					app.setUserId( jsonObject.getString( "id" ) );
					ActorData actorData = new ActorData( jsonObject );
					app.setCurrentProfileData( actorData );
					
					/*if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}*/
					
					if( actorData.getSocialConnections().getFacebook().getStatus() ){
						Intent intent = new Intent( LoginActivity.this, MainActivity.class );
						LoginActivity.this.startActivityForResult( intent, REQUEST_MAIN_ACTIVITY );
					}else{
						connectFacebook();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("LoginFail8 : "+e);
					//facebookAuthorize();
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
					
					alertDialog.setTitle( "Error" );
					alertDialog.setMessage( "Cannot login with facebook." );
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
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				System.out.println("LoginFail9 : "+arg1);
				//facebookAuthorize();
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Cannot login with facebook." );
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_MAIN_ACTIVITY ){
			if( resultCode == RESULT_MAIN_ACTIVITY_BACK ){
				this.setResult( MainActivity.RESULT_FROM_LOGOUT_BUTTON );
				this.finish();
			}
		}else{
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.setResult( MainActivity.RESULT_FROM_LOGOUT_BUTTON );
			this.finish();
		}
		return super.onKeyDown( keyCode, event );
	}
	
}
