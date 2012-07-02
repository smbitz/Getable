package com.codegears.getable;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActvity extends Activity implements OnClickListener {
	
	private Button doneButton;
	private EditText emailEditText;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private Config config;
	private String forgotPasswordURL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.forgotpasswordactivity );
		
		app = (MyApp) this.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this );
		
		doneButton = (Button) findViewById( R.id.forgotPasswordActivityDoneButton );
		emailEditText = (EditText) findViewById( R.id.forgotPasswordActivityEditTextEmail );
		
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
	
		doneButton.setOnClickListener( this );
		
		forgotPasswordURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
	}

	@Override
	public void onClick(View v) {
		if( v.equals( doneButton ) ){
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
						ForgotPasswordActvity.this.finish();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
							JSONObject errorMessageJson = jsonObject.getJSONObject( "error" );
							final AlertDialog alertDialog = new AlertDialog.Builder( ForgotPasswordActvity.this ).create();
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
		}
	}
}
