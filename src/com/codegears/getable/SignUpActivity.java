package com.codegears.getable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.RotateImage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint.Join;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignUpActivity extends Activity implements OnClickListener {

	private static final int REQUEST_SIGNUP_PICTURE = 0;

	public static final int RESULT_SINGUP_PICTURE_FINISH = 0;
	public static final int RESULT_SINGUP_CHOOSE_PICTURE_FINISH = 1;
	public static final int RESULT_SINGUP_PICTURE_CLOSE = 2;
	public static final int RESULT_SINGUP_TAKE_PICTURE_FINISH = 3;
	
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
	private ImageButton backButton;
	private TextView signUpText;
	private TextView emailText;
	private TextView firstNameText;
	private TextView lastNameText;
	private TextView passwordText;
	private TextView genderText;
	private TextView mobilePhoneText;
	private TextView pictureText;
	private TextView detailText;
	private ProgressDialog loadingDialog;
	private RotateImage rotateImage;
	private String extStorageDirectory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.signupactivity );
		
		app = (MyApp) this.getApplication();
		config = new Config( this );
		asyncHttpClient = app.getAsyncHttpClient();
		alertDialog = new AlertDialog.Builder( this ).create();
		rotateImage = new RotateImage();
		extStorageDirectory = Environment.getExternalStorageDirectory().toString()+MyApp.TEMP_IMAGE_DIRECTORY_NAME;
		
		File tempDirectory = new File( extStorageDirectory );
		if( !(tempDirectory.exists()) ){
			tempDirectory.mkdir();
		}
		
		emailEditText = (EditText) findViewById( R.id.signUpActivityEditTextEmail );
		firstNameEditText = (EditText) findViewById( R.id.signUpActivityEditTextFirstname );
		lastNameEditText = (EditText) findViewById( R.id.signUpActivityEditTextLastName );
		passwordEditText = (EditText) findViewById( R.id.signUpActivityEditTextPassword );
		genderTextview = (TextView) findViewById( R.id.signUpActivityEditTextGender );
		mobileEditText = (EditText) findViewById( R.id.signUpActivityEditTextMobilePhone );
		doneButton = (Button) findViewById( R.id.signUpActivityDoneButton );
		genderLayout = (LinearLayout) findViewById( R.id.signUpActivityLayoutGender );
		profileImage = (ImageView) findViewById( R.id.signUpActivityProfileImage );
		backButton = (ImageButton) findViewById( R.id.signUpActivityBackButton );
		signUpText = (TextView) findViewById( R.id.signUpActivitySignUpText );
		emailText = (TextView) findViewById( R.id.signUpActivityEmailText );
		firstNameText = (TextView) findViewById( R.id.signUpActivityFirstNameText );
		lastNameText = (TextView) findViewById( R.id.signUpActivityLastNameText );
		passwordText = (TextView) findViewById( R.id.signUpActivityPasswordText );
		genderText = (TextView) findViewById( R.id.signUpActivityGenderText );
		mobilePhoneText = (TextView) findViewById( R.id.signUpActivityMobilePhoneText );
		pictureText = (TextView) findViewById( R.id.signUpActivityPictureText );
		detailText = (TextView) findViewById( R.id.signUpActivityDetailText );
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		//Set font
		signUpText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		emailText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		firstNameText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		lastNameText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		passwordText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		genderText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		mobilePhoneText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		pictureText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		detailText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		emailEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		firstNameEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		lastNameEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		passwordEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		genderTextview.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		mobileEditText.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH) );
		doneButton.setTypeface( Typeface.createFromAsset(this.getAssets(), MyApp.APP_FONT_PATH_2) );
		
		//Default done button.
		doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
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
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
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
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
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
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
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
					doneButton.setTextColor( R.color.NameColorBlueSubmitEnableFalse );
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
		backButton.setOnClickListener( this );
		
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
			
			//Set font
			customDialogMaleButton.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
			customDialogFemaleButton.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
			customDialogNotSayButton.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
			
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
			loadingDialog.show();
			
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
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
					try {
						String checkValue = jsonObject.getString( "id" );
						ActorData actorData = new ActorData( jsonObject );
						app.setUserId( actorData.getId() );
						app.setCurrentProfileData( actorData );
						Intent intent = new Intent( SignUpActivity.this, FindFriendActivity.class );
						SignUpActivity.this.startActivity( intent );
					} catch (JSONException e) {
						e.printStackTrace();
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
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Sign up fail." );
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
			});
		}else if( v.equals( backButton ) ){
			this.finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_SIGNUP_PICTURE ){
			if( resultCode == RESULT_SINGUP_PICTURE_FINISH ){
				userPicture = (Bitmap) data.getParcelableExtra( ProfileChangeImagePopupDialog.BITMAP_USER_SIGNUP );
				profileImage.setImageBitmap( userPicture );
			}else if( resultCode == RESULT_SINGUP_CHOOSE_PICTURE_FINISH ){
				Uri imageURI = data.getParcelableExtra( ProfileChangeImagePopupDialog.BITMAP_USER_SIGNUP );
				
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
					
					//Check for rotate and scale down image
					Matrix matrix = new Matrix();
					if( bitmap.getHeight() > MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE ){
						float scaleValue = (MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE/bitmap.getHeight());
						
						int newWidth = (int) ((int) bitmap.getWidth()*scaleValue);//(int) (photo.getWidth()*scaleValue);
						int newHeight = (int) MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE;//(int) (photo.getHeight()*scaleValue);
						
						bitmap = Bitmap.createScaledBitmap( bitmap, newWidth, newHeight, false );
					}
					int rotation = rotateImage.getGalleryPhotoOrientation( this, imageURI );
					if (rotation != 0f) {
					     matrix.preRotate(rotation);
					}
					
					userPicture = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
					profileImage.setImageBitmap( userPicture );
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if( resultCode == RESULT_SINGUP_TAKE_PICTURE_FINISH ){
				loadingDialog.show();
				
				File photoFormStorage = new File( extStorageDirectory, ProfileChangeImagePopupDialog.TEMP_TAKE_IMAGE_FROM_CHANGE_IMAGE_FILE_NAME );
				Uri selectedImage = Uri.fromFile( photoFormStorage );
				
	            getContentResolver().notifyChange(selectedImage, null);
	            ContentResolver cr = getContentResolver();
				
	            Bitmap photo;
				try {
					photo = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
					
					//Check for rotate and scale down image
					Matrix matrix = new Matrix();
					if( photo.getHeight() > MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE ){
						float scaleValue = (MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE/photo.getHeight());
						
						int newWidth = (int) ((int) photo.getWidth()*scaleValue);//(int) (photo.getWidth()*scaleValue);
						int newHeight = (int) MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE;//(int) (photo.getHeight()*scaleValue);
						
						photo = Bitmap.createScaledBitmap( photo, newWidth, newHeight, false );
					}
					int rotation = rotateImage.getCameraPhotoOrientation( this, selectedImage, selectedImage.getPath() );
					if (rotation != 0f) {
					     matrix.preRotate(rotation);
					}
					
					userPicture = Bitmap.createBitmap( photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
					profileImage.setImageBitmap( userPicture );
					
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
			}
		}
	}

}
