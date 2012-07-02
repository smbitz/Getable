package com.codegears.getable;

import java.io.ByteArrayOutputStream;
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
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ProfileChangeImagePopupDialog extends Activity implements OnClickListener {
	
	private static final int REQUEST_TAKE_PICTURE = 0;
	private static final int REQUEST_CHOOSE_PICTURE = 1;
	
	private static final int SHOW_MENU_MODE_SIGNUP = 0;
	private static final int SHOW_MENU_MODE_PROFILE = 1;
	
	public static final String BITMAP_USER_SIGNUP = "BITMAP_USER_SIGNUP";
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.profilechangeimagepopupdialog );
		
		app = (MyApp) this.getApplication();
		config = new Config( this );
		asyncHttpClient = app.getAsyncHttpClient();
		facebook = app.getFacebook();
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		removePictureButton = (Button) findViewById( R.id.profileChangeImageDialogRemoveCurrentPictureButton );
		takePictureButton = (Button) findViewById( R.id.profileChangeImageDialogTakePictureButton );
		choosePictureButton = (Button) findViewById( R.id.profileChangeImageDialogChooseImageButton );
		fromFacebookPictureButton = (Button) findViewById( R.id.profileChangeImageDialogImportImageFacebookButton );
		fromTwitterPictureButton = (Button) findViewById( R.id.profileChangeImageDialogImportImageTwitterButton );
		closeButton = (Button) findViewById( R.id.profileChangeImageDialogCloseButton );
		removeCurrentPictureLayout = (LinearLayout) findViewById( R.id.profileChangeImageDialogRemoveCurrentPictureLayout );
		
		removePictureButton.setOnClickListener( this );
		takePictureButton.setOnClickListener( this );
		choosePictureButton.setOnClickListener( this );
		fromFacebookPictureButton.setOnClickListener( this );
		fromTwitterPictureButton.setOnClickListener( this );
		closeButton.setOnClickListener( this );
		
		if( app.getUserId() == null || app.getUserId().equals( "" ) ){
			showMenuMode = SHOW_MENU_MODE_SIGNUP;
			removeCurrentPictureLayout.setVisibility( View.GONE );
		}else{
			showMenuMode = SHOW_MENU_MODE_PROFILE;
		}
		
		editUserPictureURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/pictures.json";
		importFacebookPhotoURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/pictures/facebook.json";
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
	        		ProfileChangeImagePopupDialog.this.finish();
	        	}
	        });
		}else if( v.equals( takePictureButton ) ){
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
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
							this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_FINISH, intent );
							this.finish();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FacebookError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent intent = new Intent();
					this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_CLOSE, intent );
					this.finish();
				}else{
					facebook.authorize( this, new DialogListener() {
						
						@Override
						public void onFacebookError(FacebookError e) { }
						
						@Override
						public void onError(DialogError e) { }
						
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
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FacebookError e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Intent intent = new Intent();
							ProfileChangeImagePopupDialog.this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_CLOSE, intent );
							ProfileChangeImagePopupDialog.this.finish();
						}
						
						@Override
						public void onCancel() { }
						
					});
				}
			}else{
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
					paramMap.put( "_a", "put" );
					RequestParams params = new RequestParams(paramMap);
					/*asyncHttpClient.post( importFacebookPhotoURL, params, new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(String arg0) {
							super.onSuccess(arg0);
							Intent intent = new Intent();
							ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
							ProfileChangeImagePopupDialog.this.finish();
						}
					});*/
					asyncHttpClient.post( importFacebookPhotoURL, params, new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject jsonObject) {
							super.onSuccess(jsonObject);
							try {
								//Check data.
								String userId = jsonObject.getString( "id" );
								ActorData actorData = new ActorData( jsonObject );
								app.setCurrentProfileData( actorData );
								
								Intent intent = new Intent();
								ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
								ProfileChangeImagePopupDialog.this.finish();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}else{
					facebook.authorize( this, new DialogListener(){
						
						@Override
						public void onFacebookError(FacebookError e) { }
						
						@Override
						public void onError(DialogError e) { }
						
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
							
							//Get data from server.
							HashMap<String, String> userParamMap = new HashMap<String, String>();
							userParamMap.put( "_a", "put" );
							RequestParams userParams = new RequestParams( userParamMap );
							/*asyncHttpClient.post( importFacebookPhotoURL, userParams, new AsyncHttpResponseHandler(){
								@Override
								public void onSuccess(String arg0) {
									super.onSuccess(arg0);
									Intent intent = new Intent();
									ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
									ProfileChangeImagePopupDialog.this.finish();
								}
							});*/
							asyncHttpClient.post( importFacebookPhotoURL, params, new JsonHttpResponseHandler(){
								@Override
								public void onSuccess(JSONObject jsonObject) {
									super.onSuccess(jsonObject);
									try {
										//Check data.
										String userId = jsonObject.getString( "id" );
										ActorData actorData = new ActorData( jsonObject );
										app.setCurrentProfileData( actorData );
										
										Intent intent = new Intent();
										ProfileChangeImagePopupDialog.this.setResult( MainActivity.RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH, intent );
										ProfileChangeImagePopupDialog.this.finish();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
						}
						
						@Override
						public void onCancel() { }
						
					});
				}
			}
		}else if( v.equals( fromTwitterPictureButton ) ){
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( showMenuMode == SHOW_MENU_MODE_SIGNUP ){
			if( requestCode == REQUEST_TAKE_PICTURE ){
				if(resultCode == RESULT_OK){
					loadingDialog.show();
					
					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
					if( bitmap != null ){
						Intent intent = new Intent();
						intent.putExtra( BITMAP_USER_SIGNUP, bitmap);
						this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_FINISH, intent );
						this.finish();
					}
				}
			}else if( requestCode == REQUEST_CHOOSE_PICTURE ){
				if(resultCode == RESULT_OK){
					loadingDialog.show();
					
					Bitmap bitmap = null;
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
							Intent intent = new Intent();
							intent.putExtra( BITMAP_USER_SIGNUP, bitmap);
							this.setResult( SignUpActivity.RESULT_SINGUP_PICTURE_FINISH, intent );
							this.finish();
						}
				}
			}
		}else if( showMenuMode == SHOW_MENU_MODE_PROFILE ){
			if( requestCode == REQUEST_TAKE_PICTURE ){
				loadingDialog.show();
				
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
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
				
				//NetworkThreadUtil.getRawDataMultiPartWithCookie( editUserPictureURL, reqEntity, app.getAppBasicHttpContext(), this );
				asyncHttpClient.post( ProfileChangeImagePopupDialog.this, editUserPictureURL, reqEntity, null, new AsyncHttpResponseHandler(){
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
				} );
			}else if( requestCode == REQUEST_CHOOSE_PICTURE ){
				loadingDialog.show();
				
				Bitmap bitmap = null;
				if(resultCode == RESULT_OK){
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
						
						//NetworkThreadUtil.getRawDataMultiPartWithCookie( editUserPictureURL, reqEntity, app.getAppBasicHttpContext(), this );
						asyncHttpClient.post( ProfileChangeImagePopupDialog.this, editUserPictureURL, reqEntity, null, new AsyncHttpResponseHandler(){
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
					}
				}
				
			}
		}
		
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
}
