package com.codegears.getable;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.ui.activity.ProductCommentLayout;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ForgotPasswordActvity extends Activity implements OnClickListener {
	
	private static String RESET_PASSWORD_MESSAGE = "We just emailed you instructions on how to reset your password.";
	
	private Button doneButton;
	private EditText emailEditText;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private Config config;
	private String forgotPasswordURL;
	private ImageButton backButton;
	private TextView forgotPasswordText;
	private TextView emailText;
	private AlertDialog alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.forgotpasswordactivity );
		
		app = (MyApp) this.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this );
		alertDialog = new AlertDialog.Builder( this ).create();
		
		doneButton = (Button) findViewById( R.id.forgotPasswordActivityDoneButton );
		emailEditText = (EditText) findViewById( R.id.forgotPasswordActivityEditTextEmail );
		backButton = (ImageButton) findViewById( R.id.forgotPasswordActivityBackButton );
		forgotPasswordText = (TextView) findViewById( R.id.forgotPasswordActivityForgotPasswordText );
		emailText = (TextView) findViewById( R.id.forgotPasswordActivityEmailText );
		
		//Set font
		doneButton.setTypeface( Typeface.createFromAsset( this.getAssets() , MyApp.APP_FONT_PATH_2) );
		emailEditText.setTypeface( Typeface.createFromAsset( this.getAssets() , MyApp.APP_FONT_PATH) );
		forgotPasswordText.setTypeface( Typeface.createFromAsset( this.getAssets() , MyApp.APP_FONT_PATH) );
		emailText.setTypeface( Typeface.createFromAsset( this.getAssets() , MyApp.APP_FONT_PATH) );
		
		//Default done button
		doneButton.setTextColor( R.color.NameColorGrey );
		doneButton.setEnabled( false );
		
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
				if( s.length() == 0 ){
					doneButton.setTextColor( R.color.NameColorGrey );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		emailEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
	        	  InputMethodManager imm = (InputMethodManager) ForgotPasswordActvity.this.getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				  imm.hideSoftInputFromWindow( emailEditText.getWindowToken(), 0 );
		          return true;
		        }
		        return false;
			}
		});
	
		doneButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		forgotPasswordURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
	}

	@Override
	public void onClick(View v) {
		if( v.equals( doneButton ) ){
			InputMethodManager imm = (InputMethodManager) ForgotPasswordActvity.this.getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( emailEditText.getWindowToken(), 0 );
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "_a", "resetPassword" );
			paramMap.put( "email", emailEditText.getText().toString() );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( forgotPasswordURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					System.out.println("ResetPasswordResult : "+jsonObject);
					
					try {
						//Check status
						jsonObject.getString( "status" );
						alertDialog.setTitle( "Got it!" );
						alertDialog.setMessage( RESET_PASSWORD_MESSAGE );
						alertDialog.setButton( "Done", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
								ForgotPasswordActvity.this.finish();
							}
						});
						alertDialog.show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
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
			});
		}else if( v.equals( backButton ) ){
			this.finish();
		}
	}
}
