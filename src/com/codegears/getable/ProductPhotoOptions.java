package com.codegears.getable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.data.ProductActivityCommentsData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.CommentRowLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ProductPhotoOptions extends Activity implements OnClickListener, NetworkThreadListener {
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.productphotooptions );
		
		activityId = this.getIntent().getExtras().getString( PUT_EXTRA_ACTIVITY_ID );
		
		config = new Config( this );
		app = (MyApp) this.getApplication();
		appCookie = app.getAppCookie();
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
		
		closeButton.setOnClickListener( this );
		closeButtonOwn.setOnClickListener( this );
		copyShareURLButton.setOnClickListener( this );
		emailButton.setOnClickListener( this );
		textPhotoButton.setOnClickListener( this );
		deleteActButton.setOnClickListener( this );
		manageCommentButton.setOnClickListener( this );
		flagButton.setOnClickListener( this );
		
		String urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		
		getProductDataURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+activityId+".json"+urlVar1;
		
		NetworkThreadUtil.getRawDataWithCookie(getProductDataURL, null, appCookie, this);
	}

	@Override
	public void onClick(View v) {
		if( v.equals( closeButton ) || v.equals( closeButtonOwn ) ){
			this.finish();
		}else if( v.equals( copyShareURLButton ) ){
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText( currentData.getURL() );
			
			this.finish();
			
			Toast toast = Toast.makeText(this, "You have copied the link.", Toast.LENGTH_LONG);
			toast.show();
		}else if( v.equals( emailButton ) ){
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this image");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "link : "+currentData.getURL());
			emailIntent.setType("text/plain");

			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}else if( v.equals( textPhotoButton ) ){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("sms_body", "link : "+currentData.getURL());
			intent.setType("vnd.android-dir/mms-sms"); 
			startActivity(intent);
		}else if( v.equals( deleteActButton ) ){
			deleteActURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+activityId+".json";
			Map< String, String > newMapData = new HashMap<String, String>();
			newMapData.put( "_a", "delete" );
			String postData = NetworkUtil.createPostData( newMapData );
			
			NetworkThreadUtil.getRawDataWithCookie(deleteActURL, postData, appCookie, this);
		}else if( v.equals( manageCommentButton ) ){
			String extra[] = { activityId, currentUserId };
			
			Intent intent = new Intent();
			intent.putExtra( MANAGE_COMMENT_PUT_EXTRA, extra );
			this.setResult( MainActivity.RESULT_PHOTO_OPTION_TO_MANAGE_COMMENT, intent );
			this.finish();
		}else if( v.equals( flagButton ) ){
			flagReviewURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+activityId+"/flags.json";
			
			Map< String, String > newMapData = new HashMap<String, String>();
			newMapData.put( "emptly", "emptly" );
			String postData = NetworkUtil.createPostData( newMapData );
			
			NetworkThreadUtil.getRawDataWithCookie( flagReviewURL, postData, appCookie, this );
			this.finish();
		}
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( getProductDataURL ) ){
			try {
				//Load Product Data
				JSONObject jsonObject = new JSONObject(result);
				currentData = new ProductActivityData( jsonObject );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			currentUserId = currentData.getActor().getId();
			
			if( app.getUserId().equals( currentUserId ) ){
				this.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						photoOptionsOwnLayout.setVisibility( View.VISIBLE );
					}
				});
			}else{
				this.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						photoOptionsOtherLayout.setVisibility( View.VISIBLE );
					}
				});
			}
		}else if( urlString.equals( deleteActURL ) ){
			Intent intent = new Intent( this, MainActivity.class );
			this.startActivity( intent );
		}else if( urlString.equals( flagReviewURL ) ){
			this.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText( ProductPhotoOptions.this, "Flag Review.", Toast.LENGTH_LONG );
					toast.show();
				}
			});
		}
		
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		if( urlString.equals( flagReviewURL ) ){
			System.out.println("Fail URL : "+urlString);
			//System.out.println("Result : "+result);
			
			/*Toast toast = Toast.makeText(this, "Flag Review.", Toast.LENGTH_LONG);
			toast.show();*/
		}
	}
	
}
