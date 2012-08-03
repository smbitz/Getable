package com.codegears.getable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityCommentsData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.CommentRowLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProductPhotoOptions extends Activity implements OnClickListener {
	
	public static final String PUT_EXTRA_ACTIVITY_ID = "PUT_EXTRA_ACTIVITY_ID"; 
	private static final String URL_GET_PRODUCT_ACTIVITIES_BY_ID = "URL_GET_PRODUCT_ACTIVITIES_BY_ID";
	
	public static String MANAGE_COMMENT_PUT_EXTRA = "PUT_EXTRA_URL_VAR_1";
	
	private Button closeButton;
	private Button closeButtonOwn;
	private String activityId;
	private String getProductDataURL;
	private String flagReviewURL;
	private Config config;
	private List<String> appCookie;
	private MyApp app;
	private LinearLayout photoOptionsOtherLayout;
	private LinearLayout photoOptionsOwnLayout;
	private ProductActivityData currentData;
	private Button copyShareURLButton;
	private Button emailButton;
	private Button textPhotoButton;
	private Button deleteActButton;
	private Button manageCommentButton;
	private String deleteActURL;
	private String currentUserId;
	private Button flagButton;
	private Button shareButton;
	private AsyncHttpClient asyncHttpClient;
	private TextView photoOptionText;
	private TextView flagForReviewText;
	private TextView sharePostText;
	private TextView copyShareUrlText;
	private TextView emailPhotoText;
	private TextView textPhotoText;
	private TextView ownDeleteText;
	private TextView ownSharePostText;
	private TextView ownCopyShareUrlText;
	private TextView ownEmailPhoto;
	private TextView ownTextPhoto;
	private TextView ownManageCommentsText;
	private ActorData currentActorData;
	private Button shareButtonOwn;
	private Button copyShareURLButtonOwn;
	private Button emailButtonOwn;
	private Button textPhotoButtonOwn;
	private ProgressDialog loadingDialog;
	private LinearLayout photoOptionMainLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.productphotooptions );
		
		activityId = this.getIntent().getExtras().getString( PUT_EXTRA_ACTIVITY_ID );
		
		config = new Config( this );
		app = (MyApp) this.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		closeButton = (Button) findViewById( R.id.productDetailPhotoOptionsCloseButton );
		closeButtonOwn = (Button) findViewById( R.id.productDetailPhotoOptionsOwnCloseButton );
		photoOptionsOtherLayout = (LinearLayout) findViewById( R.id.productDetailPhotoOptionsOtherLayout );
		photoOptionsOwnLayout = (LinearLayout) findViewById( R.id.productDetailPhotoOptionsOwnLayout );
		copyShareURLButton = (Button) findViewById( R.id.productDetailPhotoOptionsCopyURLButton );
		emailButton = (Button) findViewById( R.id.productDetailPhotoOptionsSendEmailButton );
		textPhotoButton = (Button) findViewById( R.id.productDetailPhotoOptionsSendTextButton );
		deleteActButton = (Button) findViewById( R.id.productDetailPhotoOptionsDeleteActButton );
		manageCommentButton = (Button) findViewById( R.id.productDetailPhotoOptionsManageCommentButton );
		flagButton = (Button) findViewById( R.id.productDetailPhotoOptionsFlagReviewButton );
		shareButton = (Button) findViewById( R.id.productDetailPhotoOptionsShareButton );
		photoOptionText = (TextView) findViewById( R.id.productDetailPhotoOptionsText );
		flagForReviewText = (TextView) findViewById( R.id.productDetailPhotoOptionsFlagForReviewText );
		sharePostText = (TextView) findViewById( R.id.productDetailPhotoOptionsSharePostText );
		copyShareUrlText = (TextView) findViewById( R.id.productDetailPhotoOptionsCopyShareUrlText );
		emailPhotoText = (TextView) findViewById( R.id.productDetailPhotoOptionsEmailPhotoText );
		textPhotoText = (TextView) findViewById( R.id.productDetailPhotoOptionsTextPhotoText );
		ownDeleteText = (TextView) findViewById( R.id.productDetailPhotoOptionsOwnDeleteText );
		ownSharePostText = (TextView) findViewById( R.id.productDetailPhotoOptionsOwnSharePostText );
		ownCopyShareUrlText = (TextView) findViewById( R.id.productDetailPhotoOptionsOwnCopyShareUrlText );
		ownEmailPhoto = (TextView) findViewById( R.id.productDetailPhotoOptionsOwnEmailPhotoText );
		ownTextPhoto = (TextView) findViewById( R.id.productDetailPhotoOptionsOwnTextPhotoText );
		ownManageCommentsText = (TextView) findViewById( R.id.productDetailPhotoOptionsOwnManageCommentText );
		currentActorData = app.getCurrentProfileData();
		shareButtonOwn = (Button) findViewById( R.id.productDetailPhotoOptionsShareButtonOwn );
		copyShareURLButtonOwn = (Button) findViewById( R.id.productDetailPhotoOptionsCopyURLButtonOwn );
		emailButtonOwn = (Button) findViewById( R.id.productDetailPhotoOptionsSendEmailButtonOwn );
		textPhotoButtonOwn = (Button) findViewById( R.id.productDetailPhotoOptionsSendTextButtonOwn );
		photoOptionMainLayout = (LinearLayout) findViewById( R.id.productDetailPhotoOptionMainLayout );
		
		//Set font
		photoOptionText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		flagForReviewText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		sharePostText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		copyShareUrlText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		emailPhotoText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		textPhotoText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		ownDeleteText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		ownSharePostText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		ownCopyShareUrlText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		ownEmailPhoto.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		ownTextPhoto.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		ownManageCommentsText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		shareButtonOwn.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		copyShareURLButtonOwn.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		emailButtonOwn.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		textPhotoButtonOwn.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		closeButton.setOnClickListener( this );
		closeButtonOwn.setOnClickListener( this );
		copyShareURLButton.setOnClickListener( this );
		emailButton.setOnClickListener( this );
		textPhotoButton.setOnClickListener( this );
		deleteActButton.setOnClickListener( this );
		manageCommentButton.setOnClickListener( this );
		flagButton.setOnClickListener( this );
		shareButton.setOnClickListener( this );
		shareButtonOwn.setOnClickListener( this );
		copyShareURLButtonOwn.setOnClickListener( this );
		emailButtonOwn.setOnClickListener( this );
		textPhotoButtonOwn.setOnClickListener( this );
		
		String urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		
		getProductDataURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+activityId+".json"+urlVar1;
		
		asyncHttpClient.get( 
			getProductDataURL,
			new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					//Load Product Data
					currentData = new ProductActivityData( jsonObject );
					
					currentUserId = currentData.getActor().getId();
					
					if( app.getUserId().equals( currentUserId ) ){
						photoOptionsOwnLayout.setVisibility( View.VISIBLE );
						photoOptionMainLayout.setVisibility( View.VISIBLE );
					}else{
						photoOptionsOtherLayout.setVisibility( View.VISIBLE );
						photoOptionMainLayout.setVisibility( View.VISIBLE );
					}
				}
			});
	}

	@Override
	public void onClick(View v) {
		if( v.equals( closeButton ) || v.equals( closeButtonOwn ) ){
			this.finish();
		}else if( v.equals( copyShareURLButton ) || v.equals( copyShareURLButtonOwn ) ){
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText( currentData.getURL() );
			
			this.finish();
			
			Toast toast = Toast.makeText(this, "You have copied the link.", Toast.LENGTH_LONG);
			toast.show();
		}else if( v.equals( emailButton ) || v.equals( emailButtonOwn ) ){
			//loadingDialog.show();
			
			final String userName = currentActorData.getName();
			String productImageURL = currentData.getProduct().getProductPicture().getImageUrls().getImageURLL();
			
			if( productImageURL != null ){
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						Html.fromHtml(
							userName+" thinks you should check this out on Getable:<br/><br/>"+
							"<a href="+currentData.getURL()+">"+currentData.getURL()+"</a><br/><br/>"+
							"<font color='#0082a3'>"+currentData.getProduct().getBrand().getName()+"</font><br/>"+
							currentData.getProduct().getDescription()+"<br/><br/>"+
							currentData.getProduct().getStore().getName() 
						) 
				);
				//emailIntent.setType("text/plain");
				emailIntent.setType("text/html");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				
				/*final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
						userName+" thinks you should check this out on Getable:"+currentData.getURL()+"\n\n");
				shareIntent.putExtra(
					Intent.EXTRA_TEXT,
					Html.fromHtml(new StringBuilder()
					    .append("<b>Good</b>")
					    .toString()+"\n\n")
				);
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
						currentData.getProduct().getBrand().getName()+"\n"+
						currentData.getProduct().getDescription()+"\n\n"+
						currentData.getProduct().getStore().getName());
				startActivity(Intent.createChooser(shareIntent, "Send mail..."));*/
			}else{
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
						userName+" thinks you should check this out on Getable:\n\n"+currentData.getURL()+"\n\n"+
						currentData.getProduct().getBrand().getName()+"\n"+
						currentData.getProduct().getDescription()+"\n"+
						currentData.getProduct().getStore().getName());
				emailIntent.setType("text/plain");
				
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				
				/*final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
						userName+" thinks you should check this out on Getable:"+currentData.getURL()+"\n\n");
				shareIntent.putExtra(
					Intent.EXTRA_TEXT,
					Html.fromHtml(new StringBuilder()
					    .append("<img src='"+productImageURL+"' />")
					    .toString()+"\n\n")
				);
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
						currentData.getProduct().getBrand().getName()+"\n"+
						currentData.getProduct().getDescription()+"\n\n"+
						currentData.getProduct().getStore().getName());
				startActivity(Intent.createChooser(shareIntent, "Send mail..."));*/
			}
			
			/*NetworkThreadUtil.getImageInputStream( productImageURL, asyncHttpClient, new NetworkThreadListener() {
				
				@Override
				public void onNetworkRawSuccess(String urlString, String result) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onNetworkLoadImageSuccess(Bitmap bitmap) {
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
					
					if( bitmap != null ){
						String path = Images.Media.insertImage(getContentResolver(), bitmap, "email_image", null);
					    Uri screenshotUri = Uri.parse(path);
					    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
								userName+" thinks you should check this out on Getable:"+currentData.getURL()+"\n\n"+
								currentData.getProduct().getBrand().getName()+"\n"+
								currentData.getProduct().getDescription()+"\n\n"+
								currentData.getProduct().getStore().getName());
						emailIntent.setType("text/plain");
						emailIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
					    emailIntent.setType("image/png");
					    
						startActivity(Intent.createChooser(emailIntent, "Send mail..."));
					}else{
						Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
								userName+" thinks you should check this out on Getable:"+currentData.getURL()+"\n\n"+
								currentData.getProduct().getBrand().getName()+"\n"+
								currentData.getProduct().getDescription()+"\n"+
								currentData.getProduct().getStore().getName());
						emailIntent.setType("text/plain");
						
						startActivity(Intent.createChooser(emailIntent, "Send mail..."));
					}
				}
				
				@Override
				public void onNetworkFail(String urlString) {
					if( loadingDialog.isShowing() ){
						loadingDialog.dismiss();
					}
					
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, userName+" wants to share an awesome find with you.");
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
							userName+" thinks you should check this out on Getable:"+currentData.getURL()+"\n\n"+
							currentData.getProduct().getBrand().getName()+"\n"+
							currentData.getProduct().getDescription()+"\n"+
							currentData.getProduct().getStore().getName());
					emailIntent.setType("text/plain");
					
					startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				}
				
				@Override
				public void onNetworkDocSuccess(String urlString, Document document) {
					// TODO Auto-generated method stub
					
				}
			});*/
		}else if( v.equals( textPhotoButton ) || v.equals( textPhotoButtonOwn ) ){
			String userName = currentActorData.getName();
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("sms_body", userName+" thinks you should check this out on Getable:"+currentData.getURL());
			intent.setType("vnd.android-dir/mms-sms"); 
			startActivity(intent);
		}else if( v.equals( deleteActButton ) ){
			deleteActURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+activityId+".json";

			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "_a", "delete" );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( deleteActURL, params, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					/*Intent intent = new Intent( ProductPhotoOptions.this, MainActivity.class );
					ProductPhotoOptions.this.startActivity( intent );*/
					
					System.out.println("DeleteSuccess!!");
					
					Intent intent = new Intent();
					ProductPhotoOptions.this.setResult( MainActivity.RESULT_PHOTO_OPTION_DELETE_ACTIVITY, intent );
					ProductPhotoOptions.this.finish();
				}
			});
		}else if( v.equals( manageCommentButton ) ){
			String extra[] = { activityId, currentUserId };
			
			Intent intent = new Intent();
			intent.putExtra( MANAGE_COMMENT_PUT_EXTRA, extra );
			this.setResult( MainActivity.RESULT_PHOTO_OPTION_TO_MANAGE_COMMENT, intent );
			this.finish();
		}else if( v.equals( flagButton ) ){
			flagReviewURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+activityId+"/flags.json";
			
			asyncHttpClient.post( flagReviewURL, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					Toast toast = Toast.makeText( ProductPhotoOptions.this, "Flag Review.", Toast.LENGTH_LONG );
					toast.show();
				}
			});
			this.finish();
		}else if( v.equals( shareButton ) || v.equals ( shareButtonOwn ) ){
			Intent intent = new Intent();
			intent.putExtra( PUT_EXTRA_ACTIVITY_ID, activityId );
			this.setResult( MainActivity.RESULT_PHOTO_OPTION_TO_SHARE_LIST_PRODUCT_LAYOUT, intent );
			this.finish();
		}
	}
	
}
