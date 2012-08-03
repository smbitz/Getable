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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class ProfileChangePasswordLayout extends AbstractViewLayout implements OnClickListener {

	private BodyLayoutStackListener listener;
	private EditText newPassword;
	private EditText oldPassword;
	private EditText repeatePassword;
	private Button doneButton;
	private String changePasswordURL;
	private MyApp app;
	//private List<String> appCookie;
	private Config config;
	private ProgressDialog loadingDialog;
	private AsyncHttpClient asyncHttpClient;
	private AlertDialog alertDialog;
	private ImageButton backButton;
	private TextView changePasswordText;
	private TextView oldPasswordText;
	private TextView newPasswordText;
	private TextView repeatePasswordText;
	
	public ProfileChangePasswordLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.profilechangepasswordlayout, this );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		alertDialog = new AlertDialog.Builder( this.getActivity() ).create();
		
		newPassword = (EditText) findViewById( R.id.profileChangePasswordNewPassword );
		oldPassword = (EditText) findViewById( R.id.profileChangePasswordOldPassword );
		repeatePassword = (EditText) findViewById( R.id.profileChangePasswordRepeatePassword );
		doneButton = (Button) findViewById( R.id.profileChangePasswordDoneButton );
		backButton = (ImageButton) findViewById( R.id.profileChangePasswordBackButton );
		changePasswordText = (TextView) findViewById( R.id.profileChangePasswordText);
		oldPasswordText = (TextView) findViewById( R.id.profileChangePasswordOldPasswordText );
		newPasswordText = (TextView) findViewById( R.id.profileChangePasswordNewPasswordText );
		repeatePasswordText = (TextView) findViewById( R.id.profileChangePasswordRepeateText );
		
		//Set font
		doneButton.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH_2) );
		changePasswordText.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		oldPasswordText.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		newPasswordText.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		repeatePasswordText.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		oldPassword.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		newPassword.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		repeatePassword.setTypeface( Typeface.createFromAsset(this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		//Default done button.
		doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
		doneButton.setEnabled( false );
		
		newPassword.addTextChangedListener( new TextWatcher() {
			
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
					oldPassword.getText().length() == 0 ||
					repeatePassword.getText().length() == 0 ){
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		oldPassword.addTextChangedListener( new TextWatcher() {
			
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
					newPassword.getText().length() == 0 ||
					repeatePassword.getText().length() == 0 ){
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		repeatePassword.addTextChangedListener( new TextWatcher() {
			
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
					newPassword.getText().length() == 0 ||
					oldPassword.getText().length() == 0 ){
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		doneButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		changePasswordURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/password.json";
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if( v.equals( doneButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( newPassword.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( oldPassword.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( repeatePassword.getWindowToken(), 0 );
			
			String oldPasswordText = oldPassword.getText().toString();
			String newPasswordText = newPassword.getText().toString();
			String repeatePasswordText = repeatePassword.getText().toString();
			
			if( oldPasswordText.length() > 0 &&
				newPasswordText.length() > 0 &&
				repeatePasswordText.length() > 0 ){
				
				if( newPasswordText.equals( repeatePasswordText ) ){
					HashMap<String, String> paramMap = new HashMap<String, String>();
					paramMap.put( "existingPassword", oldPasswordText );
					paramMap.put( "newPassword", newPasswordText );
					paramMap.put( "confirmedPassword", repeatePasswordText );
					paramMap.put( "_a", "put" );
					RequestParams params = new RequestParams(paramMap);
					asyncHttpClient.post( changePasswordURL, params, new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject jsonObject) {
							super.onSuccess(jsonObject);

							try {
								//Check data.
								String userId = jsonObject.getString( "id" );
								ActorData actorData = new ActorData( jsonObject );
								app.setCurrentProfileData( actorData );
								if(listener != null){
									listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_SETTINGS_BACK );
								}
							} catch (JSONException e) {
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
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						}
					});
				}else{
					alertDialog.setTitle( "Error" );
					alertDialog.setMessage( "Password dose not match." );
					alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
					alertDialog.show();
				}
			}else{
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Pleasr input Old password, new password and repeate password." );
				alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
					}
				});
				alertDialog.show();
			}
		}else if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( newPassword.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( oldPassword.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( repeatePassword.getWindowToken(), 0 );

			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}
}
