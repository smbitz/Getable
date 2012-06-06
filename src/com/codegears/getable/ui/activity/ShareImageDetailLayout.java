package com.codegears.getable.ui.activity;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetGender;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;

import android.view.View.OnClickListener;

public class ShareImageDetailLayout extends AbstractViewLayout implements OnClickListener, NetworkThreadListener {
	
	public static final String SHARE_PREF_DETAIL_VALUE = "SHARE_PREF_DETAIL_VALUE";
	public static final String SHARE_PREF_KEY_PRODUCT_NAME = "SHARE_PREF_KEY_PRODUCT_NAME";
	public static final String SHARE_PREF_KEY_BRAND_NAME = "SHARE_PREF_KEY_BRAND_NAME";
	public static final String SHARE_PREF_KEY_STORES_NAME = "SHARE_PREF_KEY_STORES_NAME";

	public static final String SHARE_PREF_KEY_GENDER_ID = "SHARE_PREF_KEY_GENDER_ID";	
	public static final String SHARE_PREF_KEY_BRAND_ID = "SHARE_PREF_KEY_BRAND_ID";
	
	public static final String SHARE_PREF_KEY_STORES_EXTERNAL_ID = "SHARE_PREF_KEY_STORES_EXTERNAL_ID";
	public static final String SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID = "SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID";
	
	private Button productButton;
	private Button brandButton;
	private Button locationButton;
	private Button genderButton;
	private BodyLayoutStackListener listener;
	private TextView productName;
	private TextView brandName;
	private TextView locationName;
	private TextView genderName;
	private String setProductName;
	private String setBrandName;
	private String setStoreName;
	private String setGenderId;
	private String setBrandId;
	private SharedPreferences myPrefs;
	private GetGender getGender;
	private Button shareButton;
	private String shareURL;
	private MyApp app;
	private List<String> appCookie;
	private Config config;
	private EditText priceEditText;
	private EditText captionEditText;
	private String setExternalId;
	private String setExternalTypeId;
	private String tempImageLocation;
	
	public ShareImageDetailLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareimagedetaillayout, this );
		
		myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
		
		productButton = (Button) findViewById( R.id.shareImageDetailLayoutProductButton );
		brandButton = (Button) findViewById( R.id.shareImageDetailLayoutBrandButton );
		locationButton = (Button) findViewById( R.id.shareImageDetailLayoutLocationButton );
		genderButton = (Button) findViewById( R.id.shareImageDetailLayoutGenderButton );
		productName = (TextView) findViewById( R.id.shareImageDetailLayoutProductName );
		brandName = (TextView) findViewById( R.id.shareImageDetailLayoutBrandName );
		locationName = (TextView) findViewById( R.id.shareImageDetailLayoutLocationName );
		genderName = (TextView) findViewById( R.id.shareImageDetailLayoutGenderName );
		getGender = new GetGender( this.getContext() );
		shareButton = (Button) findViewById( R.id.shareImageDetailLayoutShareButton );
		priceEditText = (EditText) findViewById( R.id.shareImageDetailLayoutPriceEditText );
		captionEditText = (EditText) findViewById( R.id.shareImageDetailLayoutCaptionEditText );
		
		productButton.setOnClickListener( this );
		brandButton.setOnClickListener( this );
		locationButton.setOnClickListener( this );
		genderButton.setOnClickListener( this );
		shareButton.setOnClickListener( this );
		
		app = (MyApp) this.getActivity().getApplication();
		appCookie = app.getAppCookie();
		config = new Config( this.getContext() );
		
		String tempDirectory = ShareImageCropLayout.TEMP_IMAGE_DIRECTORY_NAME;
		String tempImageName = ShareImageCropLayout.TEMP_IMAGE_FILE_NAME;
		tempImageLocation = Environment.getExternalStorageDirectory().toString()+tempDirectory+tempImageName;
		
		shareURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/product.json";
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void refreshView() {
		setProductName = myPrefs.getString( SHARE_PREF_KEY_PRODUCT_NAME, null );
		setBrandName = myPrefs.getString( SHARE_PREF_KEY_BRAND_NAME, null );
		setStoreName = myPrefs.getString( SHARE_PREF_KEY_STORES_NAME, null );
		
		setGenderId = myPrefs.getString( SHARE_PREF_KEY_GENDER_ID, null );
		setBrandId = myPrefs.getString( SHARE_PREF_KEY_BRAND_ID, null );
		
		setExternalId = myPrefs.getString( SHARE_PREF_KEY_STORES_EXTERNAL_ID, null );
		setExternalTypeId = myPrefs.getString( SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID, null );

		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				if( setProductName != null ){
					productName.setText( setProductName );
				}
				if( setBrandName != null ){
					brandName.setText( setBrandName );
				}
				if( setStoreName != null ){
					locationName.setText( setStoreName );
				}
				if( setGenderId != null ){
					String setName = getGender.get( setGenderId ).toString();
					genderName.setText( setName );
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if( v.equals( shareButton ) ){
			//Post image.
			Bitmap bitmap = BitmapFactory.decodeFile( tempImageLocation );
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 10, bao);
			byte [] ba = bao.toByteArray();
			ByteArrayBody bab = new ByteArrayBody(ba, "shareImage.png");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("picture", bab);
			
			try {
				reqEntity.addPart("brand", new StringBody(setBrandName));
				reqEntity.addPart("category", new StringBody(setBrandId));
				reqEntity.addPart("gender", new StringBody(setGenderId));
				reqEntity.addPart("price", new StringBody(priceEditText.getText().toString()));
				reqEntity.addPart("description", new StringBody(captionEditText.getText().toString()));
				
				//Set store post data.
				if( setExternalId == null &&
					setExternalTypeId == null){
					reqEntity.addPart("selector", new StringBody("1")); //Store internal.
					reqEntity.addPart("store", new StringBody(setStoreName));
				}else{
					reqEntity.addPart("selector", new StringBody("2")); //Store external.
					reqEntity.addPart("externalStore.id", new StringBody(setExternalId));
					reqEntity.addPart("externalStore.type", new StringBody(setExternalTypeId));
				}
				
				NetworkThreadUtil.getRawDataMultiPartWithCookie( shareURL, null, appCookie, this, reqEntity );
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*//Post data text.
			HashMap< String, String > shareDataMap = new HashMap<String, String>();
			shareDataMap.put( "brand", setBrandName );
			shareDataMap.put( "category", setBrandId );
			shareDataMap.put( "gender", setGenderId );
			shareDataMap.put( "price", priceEditText.getText().toString() );
			shareDataMap.put( "description", captionEditText.getText().toString() );
			
			//Set store post data.
			if( setExternalId == null &&
				setExternalTypeId == null){
				shareDataMap.put( "selector", "1" ); //Store internal.
				shareDataMap.put( "store", setStoreName );
			}else{
				shareDataMap.put( "selector", "2" ); //Store external.
				shareDataMap.put( "externalStore.id", setExternalId );
				shareDataMap.put( "externalStore.type", setExternalTypeId );
			}
			
			shareDataMap.put( "picture", "" );
			
			String sharePostData = NetworkUtil.createPostData( shareDataMap );

			NetworkThreadUtil.getRawDataWithCookie( shareURL, sharePostData, appCookie, this );*/
		}else if( listener != null ){
			if( v.equals( productButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_PRODUCT );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}else if( v.equals( brandButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_BRAND );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}else if( v.equals( locationButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_STORE );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}else if( v.equals( genderButton ) ){
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_TYPE, ShareProductSearchLayout.SHARE_PREF_KEY_SEARCH_GENDER );
		        prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
			}
		}
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( shareURL ) ){
			System.out.println("Result : "+result);
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		if( urlString.equals( shareURL ) ){
			System.out.println("Fail : "+urlString);
		}
	}

}
