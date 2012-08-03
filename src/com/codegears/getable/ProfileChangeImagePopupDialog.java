package com.codegears.getable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.activity.ProfileLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.codegears.getable.util.RotateImage;
import com.codegears.getable.util.TwitterApp;
import com.codegears.getable.util.TwitterApp.TwDialogListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileChangeImagePopupDialog extends Activity implements OnClickListener {
	
	private static final int REQUEST_TAKE_PICTURE = 0;
	private static final int REQUEST_CHOOSE_PICTURE = 1;
	
	private static final int SHOW_MENU_MODE_SIGNUP = 0;
	private static final int SHOW_MENU_MODE_PROFILE = 1;
	
	public static final String BITMAP_USER_SIGNUP = "BITMAP_USER_SIGNUP";
	public static final String TEMP_TAKE_IMAGE_FROM_CHANGE_IMAGE_FILE_NAME = "takeImageFromProfileChangeTmp.PNG";
	
	private Button removePictureButton;
	private Button takePictureButton;
	private Button choosePictureButton;
	private Button fromFacebookPictureButton;
	private Button fromTwitterPictureButton;
	private Button closeButton;
	private MyApp app;
	private Config config;
	private String editUserPictureURL;
	private AsyncHttpClient asyncHttpClient;
	private ProgressDialog loadingDialog;
	private LinearLayout removeCurrentPictureLayout;
	private int showMenuMode;
	private Facebook facebook;
	private String importFacebookPhotoURL;
	private String importTwitterPhotoURL;
	private TextView setAProfilePicText;
	private TextView removeCurrentPicText;
	private TextView takePicText;
	private TextView chooseFromLibraryText;
	private TextView importFromFaceBookText;
	private TextView importFromTwitterText;
	private AlertDialog alertDialog;
	private TwitterApp mTwitter;
	private RotateImage rotateImage;
	private String extStorageDirectory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.profilechangeimagepopupdialog );
		
		app = (MyApp) this.getApplication();
		config = new Config( this );
		asyncHttpClient = app.getAsyncHttpClient();
		facebook = app.getFacebook();
		alertDialog = new AlertDialog.Builder( this ).create();
		rotateImage = new RotateImage();
		
		mTwitter = new TwitterApp(this, MyApp.TWITTER_CONSUMER_KEY, MyApp.TWITTER_SECRET_KEY);
		mTwitter.setListener(mTwLoginDialogListener);
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		extStorageDirectory = Environment.getExternalStorageDirectory().toString()+MyApp.TEMP_IMAGE_DIRECTORY_NAME;
		
		File tempDirectory = new File( extStorageDirectory );
		if( !(tempDirectory.exists()) ){
			tempDirectory.mkdir();
		}
		
		removePictureButton = (Button) findViewById( R.id.profileChangeImageDialogRemoveCurrentPictureButton );
		takePictureButton = (Button) findViewById( R.id.profileChangeImageDialogTakePictureButton );
		choosePictureButton = (Button) findViewById( R.id.profileChangeImageDialogChooseImageButton );
		fromFacebookPictureButton = (Button) findViewById( R.id.profileChangeImageDialogImportImageFacebookButton );
		fromTwitterPictureButton = (Button) findViewById( R.id.profileChangeImageDialogImportImageTwitterButton );
		closeButton = (Button) findViewById( R.id.profileChangeImageDialogCloseButton );
		removeCurrentPictureLayout = (LinearLayout) findViewById( R.id.profileChangeImageDialogRemoveCurrentPictureLayout );
		setAProfilePicText = (TextView) findViewById( R.id.profileChangeImageDialogSetAProfilePicText );
		removeCurrentPicText = (TextView) findViewById( R.id.profileChangeImageDialogRemoveCurrentPicText );
		takePicText = (TextView) findViewById( R.id.profileChangeImageDialogTakePicText );
		chooseFromLibraryText = (TextView) findViewById( R.id.profileChangeImageDialogChooseFromLibraryText );
		importFromFaceBookText = (TextView) findViewById( R.id.profileChangeImageDialogImportFromFacebookText );
		importFromTwitterText = (TextView) findViewById( R.id.profileChangeImageDialogImportFromTwitterText );
		
		//Set font
		setAProfilePicText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		removeCurrentPicText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		takePicText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		chooseFromLibraryText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		importFromFaceBookText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		importFromTwitterText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		
		removePictureButton.setOnClickListener( this );
		takePictureButton.setOnClickListener( this );
		choosePictureButton.setOnClickListener( this );
		fromFacebookPictureButton.setOnClickListener( this );
		fromTwitterPictureButton.setOnClickListener( this );
		closeButton.setOnClickListener( this );
		
		/*if( app.getUserId() != null && !(app.getUserId().equals( "" )) ){
			showMenuMode = SHOW_MENU_MODE_PROFILE;
		}else{
			showMenuMode = SHOW_MENU_MODE_SIGNUP;
			removeCurrentPictureLayout.setVisibility( View.GONE );
		}*/
		
		if( app.getCurrentProfileData() != null ){
			showMenuMode = SHOW_MENU_MODE_PROFILE;
		}else{
			showMenuMode = SHOW_MENU_MODE_SIGNUP;
			removeCurrentPictureLayout.setVisibility( View.GONE );
		}
		
		editUserPictureURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/pictures.json";
		importFacebookPhotoURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/pictures/facebook.json";
		importTwitterPhotoURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/pictures/twitter.json";
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if( showMenuMode == SHOW_MENU_MODE_SIGNUP ){
				Intent intent = new Intent();
				this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_CLOSE, intent );
				this.finish();
			}else{
				this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		if( v.equals( removePictureButton ) ){
			loadingDialog.show();
	        HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "_a", "delete" );
			RequestParams params = new RequestParams(paramMap);
	        asyncHttpClient.post( editUserPictureURL, params, new AsyncHttpResponseHandler(){
	        	@Override
	        	public void onSuccess(String arg0) {
	        		super.onSuccess(arg0);
	        		if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
	        		
	        		Intent intent = new Intent();
					ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
					ProfileChangeImagePopupDialog.this.finish();
	        	}
	        });
		}else if( v.equals( takePictureButton ) ){
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			File photo = new File( extStorageDirectory, TEMP_TAKE_IMAGE_FROM_CHANGE_IMAGE_FILE_NAME );
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            startActivityForResult(cameraIntent, REQUEST_TAKE_PICTURE ); 
		}else if( v.equals( choosePictureButton ) ){
			Intent galleryIntent = new Intent();
			galleryIntent.setType("image/*");
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult( Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_CHOOSE_PICTURE );
		}else if( v.equals( closeButton ) ){
			if( showMenuMode == SHOW_MENU_MODE_SIGNUP ){
				Intent intent = new Intent();
				this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_CLOSE, intent );
				this.finish();
			}else{
				this.finish();
			}
		}else if( v.equals( fromFacebookPictureButton ) ){
			loadingDialog.show();
			if( showMenuMode == SHOW_MENU_MODE_SIGNUP ){
				facebook.authorize( this, MyApp.FACEBOOK_PERMISSION, new DialogListener() {
					
					@Override
					public void onFacebookError(FacebookError e) {
						System.out.println("ProductChangeImagePopupError1 : "+e);
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot import from facebook." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
						
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
					}
					
					@Override
					public void onError(DialogError e) {
						System.out.println("ProductChangeImagePopupError2 : "+e);
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot import from facebook." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
						
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
					}
					
					@Override
					public void onComplete(Bundle values) {
						String token = facebook.getAccessToken();  //get access token
						Long expires = facebook.getAccessExpires();  //get access expire
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ProfileChangeImagePopupDialog.this );
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
						
						String jsonUser;
						try {
							jsonUser = facebook.request("me");
							JSONObject obj = Util.parseJson(jsonUser);
							String facebookId = obj.optString("id");
							String getPicture = "http://graph.facebook.com/"+facebookId+"/picture?type=large";
							
							Bitmap bitmap = null;
							HttpClient client = asyncHttpClient.getHttpClient();
							HttpGet httpGet = new HttpGet( getPicture );
							
							HttpResponse res;
							InputStream is;
							try {
								res = client.execute( httpGet );
								is = res.getEntity().getContent();
								bitmap = BitmapFactory.decodeStream( is );
								
								Intent intent = new Intent();
								intent.putExtra( BITMAP_USER_SIGNUP, bitmap);
								ProfileChangeImagePopupDialog.this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_FINISH, intent );
								ProfileChangeImagePopupDialog.this.finish();
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("ProductChangeImagePopupError9 : "+e);
								alertDialog.setTitle( "Error" );
								alertDialog.setMessage( "Cannot import from facebook." );
								alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										alertDialog.dismiss();
									}
								});
								alertDialog.show();
								
								if( loadingDialog.isShowing() ){
									loadingDialog.dismiss();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("ProductChangeImagePopupError10 : "+e);
								alertDialog.setTitle( "Error" );
								alertDialog.setMessage( "Cannot import from facebook." );
								alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										alertDialog.dismiss();
									}
								});
								alertDialog.show();
								
								if( loadingDialog.isShowing() ){
									loadingDialog.dismiss();
								}
							}
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("ProductChangeImagePopupError11 : "+e);
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot import from facebook." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("ProductChangeImagePopupError12 : "+e);
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot import from facebook." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("ProductChangeImagePopupError13 : "+e);
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot import from facebook." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						} catch (FacebookError e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("ProductChangeImagePopupError14 : "+e);
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot import from facebook." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						}

						Intent intent = new Intent();
						ProfileChangeImagePopupDialog.this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_CLOSE, intent );
						ProfileChangeImagePopupDialog.this.finish();
					}
					
					@Override
					public void onCancel() { 
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
					}
					
				});
			}else{
				ActorData currentActorData = app.getCurrentProfileData();
				if( currentActorData.getSocialConnections().getFacebook().getStatus() ){
					getDataFromServer( importFacebookPhotoURL );
				}else{
					facebook.authorize( this, MyApp.FACEBOOK_PERMISSION, new DialogListener(){
						
						@Override
						public void onFacebookError(FacebookError e) {
							System.out.println("ProductChangeImagePopupError3 : "+e);
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot import from facebook." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						}
						
						@Override
						public void onError(DialogError e) {
							System.out.println("ProductChangeImagePopupError4 : "+e);
							alertDialog.setTitle( "Error" );
							alertDialog.setMessage( "Cannot import from facebook." );
							alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertDialog.dismiss();
								}
							});
							alertDialog.show();
							
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						}
						
						@Override
						public void onComplete(Bundle values) {
							String token = facebook.getAccessToken();  //get access token
							Long expires = facebook.getAccessExpires();  //get access expire
							
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ProfileChangeImagePopupDialog.this );
							SharedPreferences.Editor editor = prefs.edit();
							editor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN , token);
							editor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES , expires);
							editor.commit();
							
							//Connect facebook to server.
							HashMap<String, String> paramMap = new HashMap<String, String>();
							paramMap.put( "accessToken", facebook.getAccessToken() );
							paramMap.put( "_a", "connect" );
							RequestParams params = new RequestParams(paramMap);
							/*asyncHttpClient.post( MyApp.CONNECT_FACEBOOK_URL, params, new AsyncHttpResponseHandler(){
								@Override
								public void onSuccess(String arg0) {
									super.onSuccess(arg0);
									getDataFromServer();
								}
							});*/
							asyncHttpClient.post( MyApp.CONNECT_FACEBOOK_URL, params, new JsonHttpResponseHandler(){
								@Override
								public void onSuccess(JSONObject jsonObject) {
									super.onSuccess(jsonObject);
									try {
										String checkValue = jsonObject.getString( "status" );
										getDataFromServer( importFacebookPhotoURL );
									} catch (JSONException e) {
										System.out.println("ProductChangeImagePopupError5 : "+e);
										e.printStackTrace();
										if( loadingDialog.isShowing() ){
											loadingDialog.dismiss();
										}
										
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
											System.out.println("ProductChangeImagePopupError6 : "+e1);
											e1.printStackTrace();
											alertDialog.setTitle( "Error" );
											alertDialog.setMessage( "Cannot import from facebook." );
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
									System.out.println("ProductChangeImagePopupError7 : "+jsonObject);
									super.onFailure(arg0, jsonObject);
									if( loadingDialog.isShowing() ){
										loadingDialog.dismiss();
									}
									
									alertDialog.setTitle( "Error" );
									alertDialog.setMessage( "Import from Facebook fail." );
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
								public void onFailure(Throwable arg0, String arg1) {
									// TODO Auto-generated method stub
									System.out.println("ProductChangeImagePopupError8 : "+arg1);
									super.onFailure(arg0, arg1);
									if( loadingDialog.isShowing() ){
										loadingDialog.dismiss();
									}
									
									alertDialog.setTitle( "Error" );
									alertDialog.setMessage( "Import from Facebook fail." );
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
						public void onCancel() { 
							if( loadingDialog.isShowing() ){
								loadingDialog.dismiss();
							}
						}
						
					});
				}
			}
		}else if( v.equals( fromTwitterPictureButton ) ){
			loadingDialog.show();
			if( showMenuMode == SHOW_MENU_MODE_SIGNUP ){
				mTwitter.resetAccessToken();
				mTwitter.authorize();
			}else{
				ActorData currentActorData = app.getCurrentProfileData();
				if( currentActorData.getSocialConnections().getTwitter().getStatus() ){
					getDataFromServer( importTwitterPhotoURL );
				}else{
					Intent intent = new Intent();
					ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_CONNECT_TWITTER, intent );
					ProfileChangeImagePopupDialog.this.finish();
				}
			}
		}
	}
	
	private void getDataFromServer( final String getPhotoURL ){
		//Get data from server.
		HashMap<String, String> userParamMap = new HashMap<String, String>();
		userParamMap.put( "_a", "put" );
		RequestParams userParams = new RequestParams( userParamMap );
		asyncHttpClient.post( getPhotoURL, userParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				
				try {
					System.out.println("ChangeImageSuccess !! : "+jsonObject);
					//Check data.
					String userId = jsonObject.getString( "id" );
					ActorData actorData = new ActorData( jsonObject );
					app.setCurrentProfileData( actorData );
					
					Intent intent = new Intent();
					ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
					ProfileChangeImagePopupDialog.this.finish();
				} catch (JSONException e) {
					System.out.println("ChangeImageSuccesException !!");
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
					
					//getDataFromServer( getPhotoURL );
					alertDialog.setTitle( "Error" );
					alertDialog.setMessage( "Import image fail." );
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
			public void onFailure(Throwable arg0, JSONObject arg1) {
				super.onFailure(arg0, arg1);
				System.out.println("ChangeImageFail !! : "+arg1);
				//getDataFromServer( getPhotoURL );
				
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				
				alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Import image fail." );
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
		if( showMenuMode == SHOW_MENU_MODE_SIGNUP ){
			System.out.println("ShowMode : SHOW_MENU_MODE_SIGNUP");
			if( requestCode == REQUEST_TAKE_PICTURE ){
				if(resultCode == RESULT_OK){
					Intent intent = new Intent();
					this.setResult( SignUpActivity.RESULT_SINGUP_TAKE_PICTURE_FINISH, intent );
					this.finish();
				}else{
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
			}else if( requestCode == REQUEST_CHOOSE_PICTURE ){
				if(resultCode == RESULT_OK){
		            Uri selectedImage = data.getData();
		            Intent intent = new Intent();
					intent.putExtra( BITMAP_USER_SIGNUP, selectedImage );
					this.setResult( SignUpActivity.RESULT_SINGUP_CHOOSE_PICTURE_FINISH, intent );
					this.finish();
				}else{
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
			}else{
				facebook.authorizeCallback(requestCode, resultCode, data);
			}
		}else if( showMenuMode == SHOW_MENU_MODE_PROFILE ){
			System.out.println("ShowMode : SHOW_MENU_MODE_PROFILE");
			if( requestCode == REQUEST_TAKE_PICTURE ){
				loadingDialog.show();
				if(resultCode == RESULT_OK){
					File photoFormStorage = new File( extStorageDirectory, TEMP_TAKE_IMAGE_FROM_CHANGE_IMAGE_FILE_NAME );
					Uri selectedImage = Uri.fromFile( photoFormStorage );
					
					/*//Check for rotate image
					Matrix matrix = new Matrix();
					int rotation = rotateImage.getCameraPhotoOrientation( this, selectedImage, selectedImage.getPath() );
					if (rotation != 0f) {
					     matrix.preRotate(rotation);
					}*/
					
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
						
						Bitmap bitmap = Bitmap.createBitmap( photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
			            
						//Bitmap bitmap = (Bitmap) data.getExtras().get("data");
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 10, bao);
						byte [] ba = bao.toByteArray();
						ByteArrayBody bab = new ByteArrayBody(ba, "shareImage.png");
						MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
						reqEntity.addPart("picture", bab);
						try {
							reqEntity.addPart("_a", new StringBody( "put" ));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						asyncHttpClient.post( ProfileChangeImagePopupDialog.this, editUserPictureURL, reqEntity, null, new AsyncHttpResponseHandler(){
							@Override
							public void onSuccess(String arg0) {
								System.out.println("TakePictureSuccess : "+arg0);
								super.onSuccess(arg0);
								if( loadingDialog.isShowing() ){
									loadingDialog.dismiss();
								}
								
								Intent intent = new Intent();
								ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
								ProfileChangeImagePopupDialog.this.finish();
							}
						} );
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
			}else if( requestCode == REQUEST_CHOOSE_PICTURE ){
				loadingDialog.show();
				
				Bitmap bitmap = null;
				if(resultCode == RESULT_OK){
					System.out.println("SHOW_MENU_MODE_PROFILE : Choose Image Success !!");
		            Uri selectedImage = data.getData();
		            try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if( bitmap != null ){
						//Check for rotate and scale down image
						Matrix matrix = new Matrix();
						if( bitmap.getHeight() > MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE ){
							float scaleValue = (MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE/bitmap.getHeight());
							
							int newWidth = (int) ((int) bitmap.getWidth()*scaleValue);//(int) (photo.getWidth()*scaleValue);
							int newHeight = (int) MyApp.IMAGE_HEIGHT_UPLOAD_DEFAULT_SIZE;//(int) (photo.getHeight()*scaleValue);
							
							bitmap = Bitmap.createScaledBitmap( bitmap, newWidth, newHeight, false );
						}
						int rotation = rotateImage.getGalleryPhotoOrientation( this, selectedImage );
						if (rotation != 0f) {
						     matrix.preRotate(rotation);
						}
						
						bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 10, bao);
						byte [] ba = bao.toByteArray();
						ByteArrayBody bab = new ByteArrayBody(ba, "shareImage.png");
						MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
						reqEntity.addPart("picture", bab);
						try {
							reqEntity.addPart("_a", new StringBody( "put" ));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						asyncHttpClient.post( ProfileChangeImagePopupDialog.this, editUserPictureURL, reqEntity, null, new AsyncHttpResponseHandler(){
							@Override
							public void onSuccess(String arg0) {
								System.out.println("ChoosePictureSuccess : "+arg0);
								super.onSuccess(arg0);
								if( loadingDialog.isShowing() ){
									loadingDialog.dismiss();
								}
								
								Intent intent = new Intent();
								ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
								ProfileChangeImagePopupDialog.this.finish();
							}
						});
					}
				}else{
					System.out.println("SHOW_MENU_MODE_PROFILE : Choose Image Fail !!");
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
				}
				
			}else{
				facebook.authorizeCallback(requestCode, resultCode, data);
			}
		}
	}
	
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			/*String username = mTwitter.getUsername();
			username		= (username.equals("")) ? "No Name" : username;
			
			//Toast.makeText(WelcomeScene2.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();*/
			String userId = mTwitter.getUserId();
			String getTwitterImageURL = "https://api.twitter.com/1/users/profile_image?user_id="+userId+"&size=bigger";
			
			//System.out.println("getTwitterImageURL : "+getTwitterImageURL);
			Bitmap bitmap = null;
			HttpClient client = asyncHttpClient.getHttpClient();
			HttpGet httpGet = new HttpGet( getTwitterImageURL );
			
			HttpResponse res;
			InputStream is;
			try {
				res = client.execute( httpGet );
				is = res.getEntity().getContent();
				bitmap = BitmapFactory.decodeStream( is );
				
				Intent intent = new Intent();
				intent.putExtra( BITMAP_USER_SIGNUP, bitmap);
				ProfileChangeImagePopupDialog.this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_FINISH, intent );
				ProfileChangeImagePopupDialog.this.finish();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void onError(String value) {
			Toast.makeText( ProfileChangeImagePopupDialog.this, "Twitter connection failed : "+value, Toast.LENGTH_LONG).show();
		}
	};
	
}
