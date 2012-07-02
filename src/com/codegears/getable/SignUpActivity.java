package com.codegears.getable;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignUpActivity extends Activity implements OnClickListener {

	private static final int REQUEST_SIGNUP_PICTURE = 0;

	public static final int RESULT_SINGUP_PICTURE_FINISH = 0;
	public static final int RESULT_SINGUP_PICTURE_CLOSE = 1;
	
	private MyApp app;
	private Config config;
	private AsyncHttpClient asyncHttpClient;
	private EditText emailEditText;
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText passwordEditText;
	private TextView genderTextview;
	private EditText mobileEditText;
	private Button doneButton;
	private LinearLayout genderLayout;
	private Button customDialogMaleButton;
	private Button customDialogFemaleButton;
	private Button customDialogNotSayButton;
	private ImageView profileImage;
	private Bitmap userPicture;
	private String signUpURL;
	private AlertDialog alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.signupactivity );
		
		app = (MyApp) this.getApplication();
		config = new Config( this );
		asyncHttpClient = app.getAsyncHttpClient();
		alertDialog = new AlertDialog.Builder( this ).create();
		
		emailEditText = (EditText) findViewById( R.id.signUpActivityEditTextEmail );
		firstNameEditText = (EditText) findViewById( R.id.signUpActivityEditTextFirstname );
		lastNameEditText = (EditText) findViewById( R.id.signUpActivityEditTextLastName );
		passwordEditText = (EditText) findViewById( R.id.signUpActivityEditTextPassword );
		genderTextview = (TextView) findViewById( R.id.signUpActivityEditTextGender );
		mobileEditText = (EditText) findViewById( R.id.signUpActivityEditTextMobilePhone );
		doneButton = (Button) findViewById( R.id.signUpActivityDoneButton );
		genderLayout = (LinearLayout) findViewById( R.id.signUpActivityLayoutGender );
		profileImage = (ImageView) findViewById( R.id.signUpActivityProfileImage );
		
		//Default done button.
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
				if( s.length() == 0 ||
					firstNameEditText.getText().length() == 0 ||
					lastNameEditText.getText().length() == 0 ||
					passwordEditText.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorGrey );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		firstNameEditText.addTextChangedListener( new TextWatcher() {
			
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
					emailEditText.getText().length() == 0 ||
					lastNameEditText.getText().length() == 0 ||
					passwordEditText.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorGrey );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		lastNameEditText.addTextChangedListener( new TextWatcher() {
			
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
					emailEditText.getText().length() == 0 ||
					firstNameEditText.getText().length() == 0 ||
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
					emailEditText.getText().length() == 0 ||
					firstNameEditText.getText().length() == 0 ||
					lastNameEditText.getText().length() == 0){
					doneButton.setTextColor( R.color.NameColorGrey );
					doneButton.setEnabled( false );
				}else{
					doneButton.setTextColor( Color.WHITE );
					doneButton.setEnabled( true );
				}
			}
		});
		
		doneButton.setOnClickListener( this );
		genderLayout.setOnClickListener( this );
		profileImage.setOnClickListener( this );
		
		userPicture = BitmapFactory.decodeResource( this.getResources(), R.drawable.user_image_default );
		
		signUpURL = config.get( MyApp.URL_DEFAULT ).toString()+"guest.json";
	}

	@Override
	public void onClick(View v) {
		if( v.equals( genderLayout ) ){
			final Dialog dialog = new Dialog( this );
			dialog.setContentView( R.layout.customdialoggender );
			
			customDialogMaleButton = (Button) dialog.findViewById( R.id.customDialogGenderMaleButton );
			customDialogFemaleButton = (Button) dialog.findViewById( R.id.customDialogGenderFemaleButton );
			customDialogNotSayButton = (Button) dialog.findViewById( R.id.customDialogGenderNotSayButton );
			
			customDialogMaleButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					genderTextview.setText( MyApp.PROFILE_GENDER_MALE_TEXT );
				}
			});
			
			customDialogFemaleButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					genderTextview.setText( MyApp.PROFILE_GENDER_FEMALE_TEXT );
				}
			});
			
			customDialogNotSayButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					genderTextview.setText( MyApp.PROFILE_GENDER_NOT_SAY_TEXT );
				}
			});
			
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.show();
		}else if( v.equals( profileImage ) ){
			Intent intent = new Intent( this, ProfileChangeImagePopupDialog.class );
			this.startActivityForResult( intent, REQUEST_SIGNUP_PICTURE );
		}else if( v.equals( doneButton ) ){
			final String signUpEmail = emailEditText.getText().toString();
			String signUpFirstName = firstNameEditText.getText().toString();
			String signUpLastName = lastNameEditText.getText().toString();
			String signUpPassword = passwordEditText.getText().toString();
			String signUpPhoneNumber = mobileEditText.getText().toString();
			
			//Gender
			String signUpGender = "";
			if( genderTextview.getText().toString().equals( MyApp.PROFILE_GENDER_MALE_TEXT ) ){
				signUpGender = MyApp.PROFILE_GENDER_MALE_ID;
			}else if( genderTextview.getText().toString().equals( MyApp.PROFILE_GENDER_FEMALE_TEXT ) ){
				signUpGender = MyApp.PROFILE_GENDER_FEMALE_ID;
			}
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			//User Picture
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			userPicture.compress(Bitmap.CompressFormat.PNG, 10, bao);
			byte [] ba = bao.toByteArray();
			ByteArrayBody bab = new ByteArrayBody(ba, "shareImage.png");
			reqEntity.addPart("picture", bab);
			
			try {
				reqEntity.addPart("_a", new StringBody("signUp"));
				reqEntity.addPart("email", new StringBody(signUpEmail));
				reqEntity.addPart("firstName", new StringBody(signUpFirstName));
				reqEntity.addPart("lastName", new StringBody(signUpLastName));
				reqEntity.addPart("password", new StringBody(signUpPassword));
				reqEntity.addPart("confirmedPassword", new StringBody(signUpPassword));
				reqEntity.addPart("gender", new StringBody(signUpGender));
				reqEntity.addPart("phoneNumber", new StringBody(signUpPhoneNumber));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			asyncHttpClient.post( this, signUpURL, reqEntity, null, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						app.setUserId( jsonObject.getString( "id" ) );
						Intent intent = new Intent( SignUpActivity.this, MainActivity.class );
						SignUpActivity.this.startActivity( intent );
					} catch (JSONException e) {
						e.printStackTrace();
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "User with email "+signUpEmail+" already existed." );
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
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_SIGNUP_PICTURE ){
			if( resultCode == RESULT_SINGUP_PICTURE_FINISH ){
				userPicture = (Bitmap) data.getParcelableExtra( ProfileChangeImagePopupDialog.BITMAP_USER_SIGNUP );
				profileImage.setImageBitmap( userPicture );
			}
		}
	}

}
