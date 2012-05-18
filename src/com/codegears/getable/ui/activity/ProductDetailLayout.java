package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.ProductPhotoOptions;
import com.codegears.getable.R;
import com.codegears.getable.WishlistsFilterActivity;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.ProductActivityCommentsData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.StoreData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.CommentRowLayout;
import com.codegears.getable.ui.FollowButton;
import com.codegears.getable.ui.LikeButton;
import com.codegears.getable.ui.ProductNumComment;
import com.codegears.getable.ui.ProductNumLike;
import com.codegears.getable.ui.ProductStoreAddress;
import com.codegears.getable.ui.ProductBrandName;
import com.codegears.getable.ui.UserName;
import com.codegears.getable.ui.UserProfileHeader;
import com.codegears.getable.ui.UserProfileImageLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductDetailLayout extends AbstractViewLayout implements OnClickListener, NetworkThreadListener {

	private static final String URL_GET_PRODUCT_ACTIVITIES_BY_ID = "URL_GET_PRODUCT_ACTIVITIES_BY_ID";
	public static final String SHARE_PREF_PRODUCT_ACT_ID = "SHARE_PREF_PRODUCT_ACT_ID";
	public static final String SHARE_PREF_KEY_ACTIVITY_ID = "SHARE_PREF_KEY_ACTIVITY_ID";
	
	private BodyLayoutStackListener listener;
	private String productActivityId;
	private Config config;
	private ImageView productImage;
	private ProductBrandName productBrandName;
	private TextView productDescription;
	private TextView productPrice;
	private String getProductDataURL;
	private String getProductCommentURL;
	private String getProductLikeURL;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private LinearLayout commentLayout;
	private Button photoOptionsButton;
	private UserProfileImageLayout userProfileImageLayout;
	private ProgressDialog loadingDialog;
	private LinearLayout productBrandNameLayout;
	private ArrayList<CommentRowLayout> arrayCommentLayout;
	private LinearLayout productAddressLayout;
	private ProductStoreAddress productStoreAddress;
	private LinearLayout productNumberLikeLayout;
	private ProductNumLike productNumLike;
	private LinearLayout productCommentNumLayout;
	private ProductNumComment productNumComment;
	private LinearLayout commentTextBoxLayout;
	private EditText commentEditText;
	private Button commentSubmitButton;
	private Button commentButton;
	private MyApp app;
	private LinearLayout likeButtonLayout;
	private LikeButton likeButton;
	private List<String> appCookie;
	private FollowButton userHeaderFollowButton;
	private String followUserURL;
	private LinearLayout userHeaderFollowButtonLayout;
	
	public ProductDetailLayout( Activity activity ) {
		super( activity );
		View.inflate( this.getContext(), R.layout.productdetaillayout, this );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
		productActivityId = myPrefs.getString( SHARE_PREF_KEY_ACTIVITY_ID, null );
		
		loadingDialog = ProgressDialog.show(this.getActivity(), "", 
	               "Loading. Please wait...", true, true);
		
		app = (MyApp) this.getActivity().getApplication();
		userHeader = new UserProfileHeader( this.getContext() );
		config = new Config( this.getContext() );
		arrayCommentLayout = new ArrayList<CommentRowLayout>();
		appCookie = app.getAppCookie();
		
		userProfileImageLayout = (UserProfileImageLayout) userHeader.getUserProfileImageLayout();
		userHeaderFollowButtonLayout = (LinearLayout) userHeader.getFollowButtonLayout();
		userHeaderFollowButtonLayout.setVisibility( View.GONE );
		userHeaderFollowButton = (FollowButton) userHeader.getFollowButton();
		headerLayout = (LinearLayout) findViewById( R.id.productDetailHeadLayout );
		userHeader.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ) );
		headerLayout.addView( userHeader );
		productImage = (ImageView) findViewById( R.id.productDetailImage );
		productDescription = (TextView) findViewById( R.id.productDetailDescription );
		productPrice = (TextView) findViewById( R.id.productDetailPrice );
		commentLayout = (LinearLayout) findViewById( R.id.productDetailCommentLayout );
		photoOptionsButton = (Button) findViewById( R.id.productDetailPhotoOptionsButton );
		commentTextBoxLayout = (LinearLayout) findViewById( R.id.productDetailCommentTextBoxLayout );
		commentEditText = (EditText) findViewById( R.id.productDetailCommentTextBoxEditText );
		commentSubmitButton = (Button) findViewById( R.id.productDetailCommentTextBokSubmit );
		commentButton = (Button) findViewById( R.id.productDetailCommentButton );
		likeButtonLayout = (LinearLayout) findViewById( R.id.productDetailLikeButtonLayout );
		likeButton = new LikeButton( this.getContext() );
		likeButtonLayout.addView( likeButton );
		
		productBrandNameLayout = (LinearLayout) findViewById( R.id.productDetailNameLayout );
		productBrandName = new ProductBrandName( this.getContext() );
		productBrandName.setTextSize( 20 );
		productBrandNameLayout.addView( productBrandName );
		
		productAddressLayout = (LinearLayout) findViewById( R.id.productDetailAddressLayout );
		productStoreAddress = new ProductStoreAddress( this.getContext() );
		productAddressLayout.addView( productStoreAddress );
		
		productNumberLikeLayout = (LinearLayout) findViewById( R.id.productDetailLikeNumLayout );
		productNumLike = new ProductNumLike( this.getContext() );
		productNumberLikeLayout.addView( productNumLike );
		
		productCommentNumLayout = (LinearLayout) findViewById( R.id.productDetailCommentNumLayout );
		productNumComment = new ProductNumComment( this.getContext() );
		productCommentNumLayout.addView( productNumComment );
		
		photoOptionsButton.setOnClickListener( this );
		userProfileImageLayout.setOnClickListener( this );
		productBrandName.setOnClickListener( this );
		productStoreAddress.setOnClickListener( this );
		productNumLike.setOnClickListener( this );
		productNumComment.setOnClickListener( this );
		commentSubmitButton.setOnClickListener( this );
		commentButton.setOnClickListener( this );
		likeButton.setOnClickListener( this );
		userHeaderFollowButton.setOnClickListener( this );
		
		String urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		
		getProductDataURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+".json"+urlVar1;
		getProductCommentURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/comments.json"+urlVar1;
		getProductLikeURL = config.get( URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/likes.json";
		
		NetworkThreadUtil.getRawDataWithCookie( getProductDataURL, null, appCookie, this );
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
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, profileHeader.getUserId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
		}else if( v.equals( photoOptionsButton ) ){
			Intent newIntent = new Intent( this.getContext(), ProductPhotoOptions.class );
			newIntent.putExtra( ProductPhotoOptions.PUT_EXTRA_ACTIVITY_ID, productActivityId );
			this.getActivity().startActivityForResult(newIntent, MainActivity.REQUEST_PHOTO_OPTION_TO_MANAGE_COMMENT);
		}else if( v.equals( productBrandName ) ){
			if(listener != null){
				ProductBrandName productBrandName = (ProductBrandName) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( BrandFeedLayout.SHARE_PREF_VALUE_BRAND_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( BrandFeedLayout.SHARE_PREF_KEY_BRAND_ID, productBrandName.getBrandData().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BRAND_FEED );
			}
		}else if( v.equals( productStoreAddress ) ){
			if(listener != null){
				ProductStoreAddress productStoreAddress = (ProductStoreAddress) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( StoreMainLayout.SHARE_PREF_VALUE_STORE_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( StoreMainLayout.SHARE_PREF_KEY_STORE_ID, productBrandName.getBrandData().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_STORE_MAIN);
			}
		}else if( v.equals( productNumLike ) ){
			if(listener != null){
				ProductNumLike productNumLike = (ProductNumLike) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductLikeLayout.SHARE_PREF_VALUE_PRODUCT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductLikeLayout.SHARE_PREF_KEY_PRODUCT_ID, productNumLike.getProductData().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_LIKE_USER_LIST );
			}
		}else if( v.equals( productNumComment ) ){
			if(listener != null){
				ProductNumComment productNumComment = (ProductNumComment) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductCommentLayout.SHARE_PREF_VALUE_PRODUCT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_PRODUCT_ID, productNumComment.getProductData().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST );
			}
		}else if( v.equals( commentButton ) ){
			if( commentTextBoxLayout.getVisibility() != View.VISIBLE ){
				commentTextBoxLayout.setVisibility( View.VISIBLE );
			}else{
				commentTextBoxLayout.setVisibility( View.GONE );
			}
		}else if( v.equals( commentSubmitButton ) ){
			String text = commentEditText.getText().toString();
			if( !(text.equals("")) && !(text.equals(null)) ){
				HashMap< String, String > dataMap = new HashMap<String, String>();
				dataMap.put( "text", text );
				String postData = NetworkUtil.createPostData( dataMap );
				
				NetworkThreadUtil.getRawDataWithCookie(getProductCommentURL, postData, appCookie, this);
			}
		}else if( v.equals( likeButton ) ){
			NetworkThreadUtil.getRawDataWithCookie(getProductLikeURL, null, appCookie, this);
		}else if( v.equals( userHeaderFollowButton ) ){
			String followUserId = userHeaderFollowButton.getActorData().getId();
			
			followUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+followUserId+"/followers.json";
			Map< String, String > newMapData = new HashMap<String, String>();
			newMapData.put( "_a", "follow" );
			String postData = NetworkUtil.createPostData( newMapData );
			
			NetworkThreadUtil.getRawDataWithCookie(followUserURL, postData, appCookie, this);
		}else if( v instanceof UserName ){
			if(listener != null){
				UserName name = (UserName) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, name.getActor().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
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
			final BrandData setBrandData = newData.getProduct().getBrand();
			final String setDescription = newData.getProduct().getDescription();
			final String setPrice = newData.getProduct().getPrice();
			final String setAddress = newData.getProduct().getStore().getName()+" - "+newData.getProduct().getStore().getStreetAddress();
			final String setNumLike = String.valueOf( newData.getStatisitc().getNumberOfLikes() )+" Likes";
			final String setNumComment = String.valueOf( newData.getStatisitc().getNumberOfComments() )+" Comments";
			final StoreData setStoreData = newData.getProduct().getStore();
			final ProductActivityData setProductData = newData;
			
			//Set User Header
			final String setUserName = newData.getActor().getName();
			final Bitmap setUserImage = userImageBitmap;
			final ActorData setUserData = newData.getActor();
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					productImage.setImageBitmap( setProductImage );
					productBrandName.setText( setName );
					productBrandName.setBrandData( setBrandData );
					productStoreAddress.setText( setAddress );
					productStoreAddress.setStoreData( setStoreData );
					productDescription.setText( setDescription );
					productPrice.setText( setPrice );
					productNumLike.setText( setNumLike );
					productNumLike.setProductData( setProductData );
					productNumComment.setText( setNumComment );
					productNumComment.setProductData( setProductData );
					
					//Set User Header
					userHeader.setName( setUserName );
					userHeader.setData( setUserData );
					userProfileImageLayout.setUserImage( setUserImage );
					userProfileImageLayout.setUserId( setUserData.getId() );
					userHeaderFollowButton.setActorData( setUserData );
					
					//Set image like button.
					if( setProductData.getMyRelation().getLike() != "" ){
						//Set image liked
						likeButton.setText( "liked" );
					}
				}
			});
			
			loadingDialog.dismiss();
		}else if( urlString.equals( getProductCommentURL ) ){
			JSONArray newArray = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				newArray = jsonObject.optJSONArray("entities");
				
				ProductActivityCommentsData newData;
				if( newArray == null ){
					//Load Product Data
					newData = new ProductActivityCommentsData( jsonObject );
					CommentRowLayout newCommentRowLayout = new CommentRowLayout( this.getContext() );
					newCommentRowLayout.setUserName( newData.getActor().getName() );
					newCommentRowLayout.setCommentText( newData.getComment().getCommentText() );
					
					//Set onClick name send to user profile.
					UserName userName = newCommentRowLayout.getUserNameTextView();
					userName.setActorData( newData.getActor() );
					userName.setOnClickListener( this );
					
					arrayCommentLayout.add( newCommentRowLayout );
				}else{
					for(int i = 0; i<newArray.length(); i++){
						//Load Product Data
						newData = new ProductActivityCommentsData( (JSONObject) newArray.get(i) );
						CommentRowLayout newCommentRowLayout = new CommentRowLayout( this.getContext() );
						newCommentRowLayout.setUserName( newData.getActor().getName() );
						newCommentRowLayout.setCommentText( newData.getComment().getCommentText() );
						
						//Set onClick name send to user profile.
						UserName userName = newCommentRowLayout.getUserNameTextView();
						userName.setActorData( newData.getActor() );
						userName.setOnClickListener( this );
						
						arrayCommentLayout.add( newCommentRowLayout );
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					//Clear comment text in text box.
					commentEditText.setText("");
					commentTextBoxLayout.setVisibility( View.GONE );
					
					//Remove and add new view.
					commentLayout.removeAllViews();
					for( CommentRowLayout fetchLayout:arrayCommentLayout ){
						commentLayout.addView( fetchLayout );
					}
					
					//Set new comment num text.
					productNumComment.setText( String.valueOf( arrayCommentLayout.size() )+" Comments" );
				}
			});
		}else if( urlString.equals( getProductLikeURL ) ){
			//On click like result.
		}else if( urlString.equals( followUserURL ) ){
			//On click follow result.
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}

}
