package com.codegears.getable.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.ProductPhotoOptions;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductDetailLayout extends AbstractViewLayout implements OnClickListener, NetworkThreadListener {

	private static final String URL_GET_PRODUCT_ACTIVITIES_BY_ID = "URL_GET_PRODUCT_ACTIVITIES_BY_ID";
	public static final String SHARE_PREF_PRODUCT_USER_ID = "SHARE_PREF_PRODUCT_USER_ID";
	public static final String SHARE_PREF_KEY_USER_ID = "SHARE_PREF_KEY_USER_ID";
	public static final String SHARE_PREF_PRODUCT_ACT_ID = "SHARE_PREF_PRODUCT_ACT_ID";
	public static final String SHARE_PREF_KEY_ACTIVITY_ID = "SHARE_PREF_KEY_ACTIVITY_ID";
	
	private BodyLayoutStackListener listener;
	private String productActivityId;
	private Config config;
	private ImageView productImage;
	private TextView productName;
	private TextView productDescription;
	private TextView productPrice;
	private TextView productAddress;
	private TextView productNumLike;
	private TextView productNumComments;
	private String getProductDataURL;
	private String getProductCommentURL;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private LinearLayout commentLayout;
	private Button photoOptionsButton;
	private UserProfileImageLayout userProfileImageLayout;
	
	public ProductDetailLayout( Activity activity ) {
		super( activity );
		View.inflate( this.getContext(), R.layout.productdetaillayout, this );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
		productActivityId = myPrefs.getString( SHARE_PREF_KEY_ACTIVITY_ID, null );
		
		userHeader = new UserProfileHeader( activity );
		config = new Config( this.getActivity() );
		
		productImage = (ImageView) findViewById( R.id.productDetailImage );
		productName = (TextView) findViewById( R.id.productDetailName );
		productDescription = (TextView) findViewById( R.id.productDetailDescription );
		productPrice = (TextView) findViewById( R.id.productDetailPrice );
		productAddress = (TextView) findViewById( R.id.productDetailAddress );
		productNumLike = (TextView) findViewById( R.id.productDetailNumLike );
		productNumComments = (TextView) findViewById( R.id.productDetailNumComments );
		headerLayout = (LinearLayout) findViewById( R.id.productDetailHeadLayout );
		headerLayout.addView( userHeader );
		commentLayout = (LinearLayout) findViewById( R.id.productDetailCommentLayout );
		photoOptionsButton = (Button) findViewById( R.id.productDetailPhotoOptionsButton );
		userProfileImageLayout = (UserProfileImageLayout) userHeader.getUserProfileImageLayout();
		
		photoOptionsButton.setOnClickListener( this );
		userProfileImageLayout.setOnClickListener( this );
		
		getProductDataURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+".json";
		getProductCommentURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/comments.json";

		NetworkThreadUtil.getRawData( getProductDataURL, null, this);
		NetworkThreadUtil.getRawData( getProductCommentURL, null, this);
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onClick( View v ) {
		if( v.equals( userProfileImageLayout ) ){
			if(listener != null){
				UserProfileImageLayout profileHeader = (UserProfileImageLayout) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_PRODUCT_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( SHARE_PREF_KEY_USER_ID, profileHeader.getUserId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
		}else if( v.equals( photoOptionsButton ) ){
			Intent newIntent = new Intent( this.getContext(), ProductPhotoOptions.class );
			this.getActivity().startActivity( newIntent );
		}
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( getProductDataURL ) ){
			ProductActivityData newData = null;
			try {
				//Load Product Data
				JSONObject jsonObject = new JSONObject(result);
				newData = new ProductActivityData( jsonObject );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Load Product Image
			URL productPictureURL = null;
			URL userPictureURL = null;
			Bitmap productImageBitmap = null;
			Bitmap userImageBitmap = null;
			try {
				productPictureURL = new URL( newData.getProduct().getProductPicture().getImageUrls().getImageURLS() );
				userPictureURL = new URL( newData.getActor().getPicture().getImageUrls().getImageURLT() );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			try {
				productImageBitmap = BitmapFactory.decodeStream( productPictureURL.openConnection().getInputStream() );
				userImageBitmap = BitmapFactory.decodeStream( userPictureURL.openConnection().getInputStream() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Set Product Image
			final Bitmap setProductImage = productImageBitmap;
			final String setName = newData.getProduct().getBrand().getName();
			final String setDescription = newData.getProduct().getDescription();
			final String setPrice = newData.getProduct().getPrice();
			final String setAddress = newData.getProduct().getStore().getName()+" - "+newData.getProduct().getStore().getStreetAddress();
			final String setNumLike = String.valueOf( newData.getStatisitc().getNumberOfLikes() )+" Likes";
			final String setNumComments = String.valueOf( newData.getStatisitc().getNumberOfComments() )+" Comments";
			
			//Set User Header
			final String setUserName = newData.getActor().getName();
			final Bitmap setUserImage = userImageBitmap;
			final ActorData setUserData = newData.getActor();
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					productImage.setImageBitmap( setProductImage );
					productName.setText( setName );
					productDescription.setText( setDescription );
					productPrice.setText( setPrice );
					productAddress.setText( setAddress );
					productNumLike.setText( setNumLike );
					productNumComments.setText( setNumComments );
					
					//Set User Header
					userHeader.setName( setUserName );
					userHeader.setData( setUserData );
					userProfileImageLayout.setUserImage( setUserImage );
					userProfileImageLayout.setUserId( setUserData.getId() );
				}
			});
			
		}else if( urlString.equals( getProductCommentURL ) ){
			try {
				//System.out.println("URL : "+getProductCommentURL);
				//System.out.println("REUSLT : "+result);
				JSONObject jsonObject = new JSONObject(result);
				JSONArray newArray = jsonObject.getJSONArray("entities");
				ProductActivityData newData;
				for(int i = 0; i<newArray.length(); i++){
					//Load Product Data
					newData = new ProductActivityData( (JSONObject) newArray.get(i) );
					//System.out.println("Result : "+newArray.get(i));
					//System.out.println("This is : "+newData.getProduct());
					//System.out.println("Get Comment !! : "+newData.getComment());
					//System.out.println("This is : "+newData.getComment().getProductActivityData().getActor().getName()+" - "+newData.getComment().getCommentText());
				}
				this.getActivity().runOnUiThread( new Runnable() {
					@Override
					public void run() {
						commentLayout.addView( new CommentRowLayout( getContext() ) );
						commentLayout.addView( new CommentRowLayout( getContext() ) );
						commentLayout.addView( new CommentRowLayout( getContext() ) );
						commentLayout.addView( new CommentRowLayout( getContext() ) );
						commentLayout.addView( new CommentRowLayout( getContext() ) );
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
}
