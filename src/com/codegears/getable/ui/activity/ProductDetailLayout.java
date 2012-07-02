package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
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
import com.codegears.getable.ui.ProductImageThumbnail;
import com.codegears.getable.ui.ProductNumComment;
import com.codegears.getable.ui.ProductNumLike;
import com.codegears.getable.ui.ProductStoreAddress;
import com.codegears.getable.ui.ProductBrandName;
import com.codegears.getable.ui.UserName;
import com.codegears.getable.ui.UserProfileHeader;
import com.codegears.getable.ui.UserProfileImageLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.codegears.getable.util.RoundScaleNumber;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductDetailLayout extends AbstractViewLayout implements OnClickListener, OnItemClickListener {

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
	private String getRelatedProductURL;
	private String getProductUnLikeURL;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private LinearLayout commentLayout;
	private Button photoOptionsButton;
	private Button wishlistButton;
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
	//private List<String> appCookie;
	private FollowButton userHeaderFollowButton;
	private String followUserURL;
	private LinearLayout userHeaderFollowButtonLayout;
	private String wishlistId;
	private ArrayList<ProductActivityData> relateActivityData;
	private ImageLoader imageLoader;
	private Gallery relatedGallery;
	private RelatedAdapter relatedAdapter;
	private RoundScaleNumber roundScaleNumber;
	private AsyncHttpClient asyncHttpClient;
	
	public ProductDetailLayout( Activity activity ) {
		super( activity );
		View.inflate( this.getContext(), R.layout.productdetaillayout, this );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
		productActivityId = myPrefs.getString( SHARE_PREF_KEY_ACTIVITY_ID, null );
		
		loadingDialog = ProgressDialog.show(this.getActivity(), "", 
	               "Loading. Please wait...", true, true);
		
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		userHeader = new UserProfileHeader( this.getContext() );
		config = new Config( this.getContext() );
		arrayCommentLayout = new ArrayList<CommentRowLayout>();
		//appCookie = app.getAppCookie();
		relateActivityData = new ArrayList<ProductActivityData>();
		imageLoader = new ImageLoader( this.getContext() );
		relatedAdapter = new RelatedAdapter();
		roundScaleNumber = new RoundScaleNumber();
		
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
		wishlistButton = (Button) findViewById( R.id.productDetailWishlistButton );
		relatedGallery = (Gallery) findViewById( R.id.productDetailRelatedGallery );
		
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
		wishlistButton.setOnClickListener( this );
		relatedGallery.setOnItemClickListener( this );
		
		//Set gallery align left
		MarginLayoutParams mlp = (MarginLayoutParams) relatedGallery.getLayoutParams();
		mlp.setMargins(-(this.getWidth()/2+310), 
		               mlp.topMargin, 
		               mlp.rightMargin, 
		               mlp.bottomMargin
		);
		
		commentEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
		          submitCommentText();
		          return true;
		        }
		        return false;
			}
		});
		
		String urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		
		getProductDataURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+".json"+urlVar1;
		getProductCommentURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/comments.json"+urlVar1;
		getProductLikeURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/likes.json";
		getRelatedProductURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/recommended.json"+urlVar1;
		
		//NetworkThreadUtil.getRawDataWithCookie( getProductDataURL, null, appCookie, this );
		//NetworkThreadUtil.getRawData( getProductCommentURL, null, this);
		//NetworkThreadUtil.getRawDataWithCookie( getRelatedProductURL, null, appCookie, this );
		
		asyncHttpClient.get( getProductDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject getJsonObject) {
				super.onSuccess(getJsonObject);
				onGetProductDataURLSuccess(getJsonObject);
			}
		});
		
		asyncHttpClient.get( getProductCommentURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject getJsonObject) {
				super.onSuccess(getJsonObject);
				onGetProduceCommentURLSuccess(getJsonObject);
			}
		});
		
		asyncHttpClient.get( getRelatedProductURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject getJsonObject) {
				// TODO Auto-generated method stub
				super.onSuccess(getJsonObject);
				onGetRelatedProductURL(getJsonObject);
			}
		});
	}
	
	private void onGetProductDataURLSuccess(JSONObject jsonObject){
		//Load Product Data
		ProductActivityData newData = new ProductActivityData( jsonObject );
		
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
				
				//Price text
				if( !(setPrice.equals("")) && (setPrice != null) ){
					String setPriceText = String.valueOf( roundScaleNumber.round( Double.valueOf( setPrice ) , 2, BigDecimal.ROUND_HALF_UP ) );
					productPrice.setText( setPriceText );
				}else{
					productPrice.setText( "-" );
				}
				
				productNumLike.setText( setNumLike );
				productNumLike.setProductData( setProductData );
				productNumComment.setText( setNumComment );
				productNumComment.setProductData( setProductData );
				
				//Set User Header
				userHeader.setName( setUserName );
				userHeader.setData( setUserData );
				if( setUserImage != null ){
					userProfileImageLayout.setUserImage( setUserImage );
				}
				userProfileImageLayout.setUserId( setUserData.getId() );
				userHeaderFollowButton.setActorData( setUserData );
				
				//Set image like button.
				if( setProductData.getMyRelation() != null &&
					setProductData.getMyRelation().getLike() != "" ){
					//Set image liked.
					//likeButton.setText( "liked" );
					likeButton.setBackgroundResource( R.drawable.button_liked );
					likeButton.setLikeId( setProductData.getMyRelation().getLike() );
					likeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKED );
				}else{
					//Set image like.
					//likeButton.setText( "like" );
					likeButton.setBackgroundResource( R.drawable.button_like );
					likeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKE );
				}
				
				//Set image wishlist
				if( setProductData.getMyRelation() != null &&
					setProductData.getMyRelation().getArrayWishlistId() != null ){
					//Set image wishlist
					//wishlistButton.setText( "wishlistAdd" );
					wishlistButton.setBackgroundResource( R.drawable.button_wishlish_added );
					wishlistId = setProductData.getMyRelation().getArrayWishlistId().toString();
				}
			}
		});
		
		loadingDialog.dismiss();
	}

	private void onGetProduceCommentURLSuccess(JSONObject jsonObject){
		JSONArray newArray = null;
		try {
			newArray = jsonObject.optJSONArray("entities");
			
			ProductActivityCommentsData newData;
			if( newArray == null ){
				//Load Product Data
				newData = new ProductActivityCommentsData( jsonObject );
				CommentRowLayout newCommentRowLayout = new CommentRowLayout( this.getContext() );
				newCommentRowLayout.setUserName( newData.getActor().getName() );
				newCommentRowLayout.setCommentText( newData.getComment().getCommentText() );
				newCommentRowLayout.setActivityData( newData );
				
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
					newCommentRowLayout.setActivityData( newData );
					
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
					//Set Image
					String imageURL = fetchLayout.getActivityData().getActor().getPicture().getImageUrls().getImageURLT();
					imageLoader.DisplayImage( imageURL, ProductDetailLayout.this.getActivity(), fetchLayout.getUserImageView(), true, asyncHttpClient );
					
					commentLayout.addView( fetchLayout );
				}
				
				//Set new comment num text.
				productNumComment.setText( String.valueOf( arrayCommentLayout.size() )+" Comments" );
			}
		});
	}
	
	private void onGetRelatedProductURL(JSONObject jsonObject){
		try {
			JSONArray newArray = jsonObject.getJSONArray("entities");
			for(int i = 0; i<newArray.length(); i++){
				//Load Product Data
				ProductActivityData newData = new ProductActivityData( (JSONObject) newArray.get(i) );
				relateActivityData.add(newData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		relatedAdapter.setData( relateActivityData );
		relatedGallery.setSpacing( 5 );
		relatedGallery.setAdapter( relatedAdapter );
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
			this.getActivity().startActivityForResult(newIntent, MainActivity.REQUEST_PHOTO_OPTION);
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
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductCommentLayout.SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_ACT_ID, productNumComment.getProductData().getId() );
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_USER_ID, productNumComment.getProductData().getActor().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST );
			}
		}else if( v.equals( commentButton ) ){
			if( commentTextBoxLayout.getVisibility() != View.VISIBLE ){
				commentTextBoxLayout.setVisibility( View.VISIBLE );
				//Show Keybord
			    ((InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(commentEditText, 0);
			}else{
				commentTextBoxLayout.setVisibility( View.GONE );
			}
		}else if( v.equals( commentSubmitButton ) ){
			submitCommentText(); 
		}else if( v.equals( likeButton ) ){
			if( likeButton.getButtonStatus() == LikeButton.BUTTON_STATUS_LIKE ){
				
				asyncHttpClient.post( getProductLikeURL, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						ProductActivityData newData = new ProductActivityData( jsonObject );
						String likeId = newData.getLike().getActivityData().getId();
						likeButton.setLikeId( likeId );
						
						//On click like result.
						//Set image liked
						likeButton.setBackgroundResource( R.drawable.button_liked );
						likeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKED );
					}
				});
			}else if( likeButton.getButtonStatus() == LikeButton.BUTTON_STATUS_LIKED ){
				String likeActivityId = likeButton.getLikeId();
				getProductUnLikeURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+"/"+likeActivityId+".json";
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "delete" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( getProductUnLikeURL, params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						//On click like result.
						//Set image liked
						likeButton.setBackgroundResource( R.drawable.button_like );
						likeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKE );
					}
				});
			}
		}else if( v.equals( userHeaderFollowButton ) ){
			String followUserId = userHeaderFollowButton.getActorData().getId();
			
			followUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+followUserId+"/followers.json";
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "_a", "follow" );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( followUserURL, params, null );
		}else if( v instanceof UserName ){
			if(listener != null){
				UserName name = (UserName) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, name.getActor().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
		}else if( v.equals( wishlistButton ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductWishlistLayout.SHARE_PREF_WISHLLIST_VALUE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductWishlistLayout.SHARE_PREF_KEY_PRODUCT_ID, productActivityId );
				prefsEditor.putString( ProductWishlistLayout.SHARE_PREF_KEY_WISHLIST_ID, wishlistId );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_WISHLIST );
			}
		}
	}

	private void submitCommentText() {
		//Hide Keybord
	    ((InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(commentEditText.getWindowToken(), 0); 
		
	    //Hide Comment Layout
	    commentTextBoxLayout.setVisibility( View.GONE );
	    
		String text = commentEditText.getText().toString();
		if( !(text.equals("")) && !(text.equals(null)) ){
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "text", text );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( getProductCommentURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					onGetProduceCommentURLSuccess(getJsonObject);
				}
			});
		}
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	private class RelatedAdapter extends BaseAdapter {

		private ArrayList<ProductActivityData> data;
		
		public void setData(ArrayList<ProductActivityData> relateActivityData) {
			data = relateActivityData;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get( position );
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			
			ProductImageThumbnail returnView;
			
			if( convertView == null ){
				returnView = new ProductImageThumbnail( ProductDetailLayout.this.getContext() );
			}else{
				returnView = (ProductImageThumbnail) convertView;
			}
			
			String imageURL = data.get( position ).getProduct().getProductPicture().getImageUrls().getImageURLT();
			
			returnView.setProductData( data.get( position ) );
			imageLoader.DisplayImage( imageURL, ProductDetailLayout.this.getActivity(), returnView.getProductImageView(), true, asyncHttpClient );
			
			return returnView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		if(listener != null){
			if( v instanceof  ProductImageThumbnail ){
				ProductImageThumbnail productSelect = (ProductImageThumbnail) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, productSelect.getProductData().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
			}
		}
	}
	
}
