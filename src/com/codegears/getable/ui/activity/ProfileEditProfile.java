package com.codegears.getable.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class ProfileEditProfile extends AbstractViewLayout implements OnClickListener {
	
	private BodyLayoutStackListener listener;
	private MyApp app;
	private ActorData userProfileData;
	private EditText editTextFirstName;
	private EditText editTextLastName;
	private EditText editTextPhoneNumber;
	private TextView textViewGender;
	private LinearLayout editProfileGenderLayout;
	private Button customDialogMaleButton;
	private Button customDialogFemaleButton;
	private Button customDialogNotSayButton;
	private Button doneButton;
	private String updateUserURL;
	private ProgressDialog loadingDialog;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	private ImageButton backButton;
	private TextView editProfileText;
	private TextView firstNameText;
	private TextView lastNameText;
	private TextView phoneText;
	private TextView genderText;
	
	public ProfileEditProfile(Activity activity) {
		super(activity);
		View.inflate(this.getContext(), R.layout.profileeditprofilelayout, this);
		
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		userProfileData = app.getCurrentProfileData();
		config = new Config( this.getContext() );
		
		editTextFirstName = (EditText) findViewById( R.id.editProfileFirstName );
		editTextLastName = (EditText) findViewById( R.id.editProfileLastName );
		editTextPhoneNumber = (EditText) findViewById( R.id.editProfilePhoneNumber );
		textViewGender = (TextView) findViewById( R.id.textViewProfileGender );
		editProfileGenderLayout = (LinearLayout) findViewById( R.id.editProfileGenderLayout );
		doneButton = (Button) findViewById( R.id.editProfileDoneButton );
		backButton = (ImageButton) findViewById( R.id.editProfileBackButton );
		editProfileText = (TextView) findViewById( R.id.editProfileText );
		firstNameText = (TextView) findViewById( R.id.editProfileFirstNameText );
		lastNameText = (TextView) findViewById( R.id.editProfileLastNameText );
		phoneText = (TextView) findViewById( R.id.editProfilePhoneText );
		genderText = (TextView) findViewById( R.id.editProfileGenderText );
		
		//Set font
		editProfileText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		firstNameText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		lastNameText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		phoneText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		genderText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		doneButton.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH_2) );
		editTextFirstName.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		editTextLastName.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		editTextPhoneNumber.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		textViewGender.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		editTextFirstName.setText( userProfileData.getFirstName() );
		editTextLastName.setText( userProfileData.getLastName() );
		System.out.println("AppUserPhone2 : "+userProfileData.getPhone());
		editTextPhoneNumber.setText( userProfileData.getPhone() );
		
		editTextFirstName.addTextChangedListener( new TextWatcher() {
			
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
					editTextLastName.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		editTextLastName.addTextChangedListener( new TextWatcher() {
			
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
					editTextFirstName.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		loadingDialog = new ProgressDialog( this.getActivity() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		String genderText = "";
		if( userProfileData.getGender() != null ){
			if( userProfileData.getGender().getId().equals( MyApp.PROFILE_GENDER_MALE_ID ) ){
				genderText = "MALE";
			}else if( userProfileData.getGender().getId().equals( MyApp.PROFILE_GENDER_FEMALE_ID ) ){
				genderText = "FEMALE";
			}
		}
		
		textViewGender.setText( genderText );
		
		editProfileGenderLayout.setOnClickListener( this );
		doneButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		updateUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"me.json";
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if( v.equals( editProfileGenderLayout ) ){
			final Dialog dialog = new Dialog( this.getContext() );
			dialog.setContentView( R.layout.customdialoggender );
			
			customDialogMaleButton = (Button) dialog.findViewById( R.id.customDialogGenderMaleButton );
			customDialogFemaleButton = (Button) dialog.findViewById( R.id.customDialogGenderFemaleButton );
			customDialogNotSayButton = (Button) dialog.findViewById( R.id.customDialogGenderNotSayButton );
			
			customDialogMaleButton.setTypeface( Typeface.createFromAsset( ProfileEditProfile.this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
			customDialogFemaleButton.setTypeface( Typeface.createFromAsset( ProfileEditProfile.this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
			customDialogNotSayButton.setTypeface( Typeface.createFromAsset( ProfileEditProfile.this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
			
			customDialogMaleButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					textViewGender.setText( MyApp.PROFILE_GENDER_MALE_TEXT );
				}
			});
			
			customDialogFemaleButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					textViewGender.setText( MyApp.PROFILE_GENDER_FEMALE_TEXT );
				}
			});
			
			customDialogNotSayButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					textViewGender.setText( MyApp.PROFILE_GENDER_NOT_SAY_TEXT );
				}
			});
			
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.show();
		}else if( v.equals( doneButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( editTextFirstName.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( editTextLastName.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( editTextPhoneNumber.getWindowToken(), 0 );
			
			loadingDialog.show();
			
			String editedName = editTextFirstName.getText().toString();
			String editedLastName = editTextLastName.getText().toString();
			String editedPhone = editTextPhoneNumber.getText().toString();
			String editedGender = textViewGender.getText().toString();
			
			if( editedGender.equals( MyApp.PROFILE_GENDER_MALE_TEXT ) ){
				editedGender = MyApp.PROFILE_GENDER_MALE_ID;
			}else if( editedGender.equals( MyApp.PROFILE_GENDER_FEMALE_TEXT ) ){
				editedGender = MyApp.PROFILE_GENDER_FEMALE_ID;
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "firstName", editedName );
			paramMap.put( "lastName", editedLastName );
			paramMap.put( "phoneNumber", editedPhone );
			paramMap.put( "gender.value", editedGender );
			paramMap.put( "_a", "put" );
			RequestParams params = new RequestParams(paramMap);

			asyncHttpClient.post( updateUserURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						//Check data.
						String checkData = jsonObject.getString( "id" );
						ActorData actorData = new ActorData( jsonObject );
						System.out.println("UpdateUserResult : "+jsonObject);
						System.out.println("UserPhone : "+actorData.getPhone());
						app.setCurrentProfileData( actorData );
						System.out.println("AppUserPhone1 : "+app.getCurrentProfileData().getPhone());
						if(listener != null){
							listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PROFILE_LAYOUT_BACK );
						}
					} catch (JSONException e) {
						e.printStackTrace();
						try {
							JSONObject errorMessageJson = jsonObject.getJSONObject( "error" );
							final AlertDialog alertDialog = new AlertDialog.Builder( ProfileEditProfile.this.getContext() ).create();
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
		}else if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( editTextFirstName.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( editTextLastName.getWindowToken(), 0 );
				imm.hideSoftInputFromWindow( editTextPhoneNumber.getWindowToken(), 0 );
			
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}
	
}
