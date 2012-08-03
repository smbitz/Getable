package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.ProductBrandFeedData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.BrandFeedItem;
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.LikeButton;
import com.codegears.getable.ui.MyFeedCommentButton;
import com.codegears.getable.ui.MyFeedLikeButton;
import com.codegears.getable.ui.MyfeedWishlistButton;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
import com.codegears.getable.util.CalculateTime;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;

public class BrandFeedLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_VALUE_BRAND_ID = "SHARE_PREF_VALUE_BRAND_ID";
	public static final String SHARE_PREF_KEY_BRAND_ID = "SHARE_PREF_KEY_BRAND_ID";
	public static final String URL_DEFAULT = "URL_DEFAULT";

	private BodyLayoutStackListener listener;
	private String brandId;
	private ListView brandListView;
	private BrandAdapter brandAdapter;
	private ArrayList<ProductBrandFeedData> arrayBrandFeedData;
	private ArrayList<Bitmap> arrayUserImage;
	private ArrayList<Bitmap> arrayProductImage;
	private Config config;
	private ProgressDialog loadingDialog;
	private String getProductLikeURL;
	private String getProductUnLikeURL;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private ImageButton backButton;
	private String getBrandFeedDataURL;
	
	public BrandFeedLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.brandfeedlayout, this );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_BRAND_ID, this.getActivity().MODE_PRIVATE );
		brandId = myPrefs.getString( SHARE_PREF_KEY_BRAND_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		
		brandListView = (ListView) findViewById( R.id.brandFeedLayoutListView );
		brandAdapter = new BrandAdapter();
		arrayBrandFeedData = new ArrayList<ProductBrandFeedData>();
		arrayUserImage = new ArrayList<Bitmap>();
		arrayProductImage = new ArrayList<Bitmap>();
		config = new Config( this.getContext() );
		asyncHttpClient = app.getAsyncHttpClient();
		backButton = (ImageButton) findViewById( R.id.brandFeedBackButton );
		
		backButton.setOnClickListener( this );
		
		//Set line at footer
		brandListView.addFooterView( new FooterListView( this.getContext() ) );
		
		getBrandFeedDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"brands/"+brandId+"/activities.json";
		
		loadData();
	}
	
	private void recycleResource() {
		arrayBrandFeedData.clear();
		arrayUserImage.clear();
		arrayProductImage.clear();
	}
	
	public void loadData(){
		loadingDialog.show();
		
		asyncHttpClient.get( getBrandFeedDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				System.out.println("Brand : "+jsonObject);
				try {
					JSONArray newArray = jsonObject.getJSONArray("entities");
					for(int i = 0; i<newArray.length(); i++){
						//Load Product Data
						ProductBrandFeedData newData = new ProductBrandFeedData( (JSONObject) newArray.get(i) );
						
						//Load Product and user Image
						URL productPictureURL = null;
						URL userPictureURL = null;
						Bitmap imageProductBitmap = null;
						Bitmap imageUserBitmap = null;
						try {
							productPictureURL = new URL( newData.getProduct().getProductPicture().getImageUrls().getImageURLT() );
							userPictureURL = new URL( newData.getActor().getPicture().getImageUrls().getImageURLT() );
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
						
						try {
							imageProductBitmap = BitmapFactory.decodeStream( productPictureURL.openConnection().getInputStream() );
							imageUserBitmap = BitmapFactory.decodeStream( userPictureURL.openConnection().getInputStream() );
						} catch (IOException e) {
							e.printStackTrace();
						}

						arrayProductImage.add( imageProductBitmap );
						arrayUserImage.add( imageUserBitmap );
						arrayBrandFeedData.add( newData );
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				brandAdapter.setData( arrayBrandFeedData );
				BrandFeedLayout.this.getActivity().runOnUiThread( new Runnable() {
					@Override
					public void run() {
						brandListView.setAdapter( brandAdapter );
					}
				});
				
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		recycleResource();
		loadData();
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	private class BrandAdapter extends BaseAdapter {
		
		ArrayList<ProductBrandFeedData> data;
		
		public void setData(ArrayList<ProductBrandFeedData> arrayBrandFeedData) {
			data = arrayBrandFeedData;
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
			
			final BrandFeedItem brandFeedItem;
			
			if( convertView == null ){
				brandFeedItem = new BrandFeedItem( BrandFeedLayout.this.getContext() );
			}else{
				brandFeedItem = (BrandFeedItem) convertView;
			}
			
			String setUserName = data.get( position ).getActor().getName();
			String setProductBrandName = data.get( position ).getProduct().getBrand().getName();
			String setNumberLikes = String.valueOf( data.get( position ).getStatisitc().getNumberOfLikes() );
			String setNumberComments = String.valueOf( data.get( position ).getStatisitc().getNumberOfComments() );
			String setPostTime = CalculateTime.getPostTime( data.get( position ).getActivityTime() );
			final ProductBrandFeedData brandFeedData = data.get( position );
			
			brandFeedItem.setUserName( setUserName );
			brandFeedItem.setProductBrandName( setProductBrandName );
			brandFeedItem.setUserImage( arrayUserImage.get( position ) );
			brandFeedItem.setProductImage( arrayProductImage.get( position ) );
			brandFeedItem.setNumberLikes( setNumberLikes );
			brandFeedItem.setNumberComments( setNumberComments );
			brandFeedItem.setProductData( data.get( position ) );
			brandFeedItem.setPostTime( setPostTime );
			brandFeedItem.setOnClickListener( BrandFeedLayout.this );
			
			//Set Like, Comment, Wishlist Button
			MyFeedLikeButton feedLikeButton = brandFeedItem.getLikeButton();
			MyFeedCommentButton feedCommentButton = brandFeedItem.getCommentButton();
			MyfeedWishlistButton feedWishlistButton = brandFeedItem.getWishlistButton();
			
			feedLikeButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					if( v instanceof MyFeedLikeButton ){
						loadingDialog.show();
						
						final MyFeedLikeButton feedLikeButton = (MyFeedLikeButton) v;
						String productActivityId = feedLikeButton.getBrandFeedData().getId();
						getProductLikeURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/likes.json";
						
						if( feedLikeButton.getButtonStatus() == LikeButton.BUTTON_STATUS_LIKE ){
							asyncHttpClient.post( getProductLikeURL, new JsonHttpResponseHandler(){
								@Override
								public void onSuccess(JSONObject jsonObject) {
									super.onSuccess(jsonObject);
									ProductBrandFeedData newData = new ProductBrandFeedData( jsonObject );
									String likeId = newData.getId();
									feedLikeButton.setLikeId( likeId );
									brandFeedData.setMyRelation( newData.getMyRelation() );
									
									//On click like result.
									//Set image liked
									feedLikeButton.setBackgroundResource( R.drawable.myfeed_liked_icon );
									feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKED );
									
									int changeLikeNum = (brandFeedItem.getNumbersLikes())+1;
									brandFeedItem.setNumberLikes( String.valueOf( changeLikeNum ) );
									brandFeedData.getStatisitc().setNumberOfLikes( changeLikeNum );
									
									if( loadingDialog.isShowing() ){
										loadingDialog.dismiss();
									}
								}
							});
						}else if( feedLikeButton.getButtonStatus() == LikeButton.BUTTON_STATUS_LIKED ){
							String likeActivityId = feedLikeButton.getLikeId();
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
									feedLikeButton.setBackgroundResource( R.drawable.myfeed_like_icon );
									feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKE );
									brandFeedData.setMyRelation( null );
									
									int changeLikeNum = (brandFeedItem.getNumbersLikes())-1;
									brandFeedItem.setNumberLikes( String.valueOf( changeLikeNum ) );
									brandFeedData.getStatisitc().setNumberOfLikes( changeLikeNum );
									
									if( loadingDialog.isShowing() ){
										loadingDialog.dismiss();
									}
								}
							});
						}
					}
				}
			});
			feedCommentButton.setOnClickListener( BrandFeedLayout.this );
			feedWishlistButton.setOnClickListener( BrandFeedLayout.this );
			
			//Set Like, Comment, Wishlist Data
			//Set image like button.
			if( brandFeedData.getMyRelation() != null &&
				brandFeedData.getMyRelation().getLike() != "" ){
				//Set image liked.
				//likeButton.setText( "liked" );
				feedLikeButton.setBackgroundResource( R.drawable.myfeed_liked_icon );
				feedLikeButton.setLikeId( brandFeedData.getMyRelation().getLike() );
				feedLikeButton.setBrandFeedData( brandFeedData );
				feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKED );
			}else{
				//Set image like.
				//likeButton.setText( "like" );
				feedLikeButton.setBackgroundResource( R.drawable.myfeed_like_icon );
				feedLikeButton.setBrandFeedData( brandFeedData );
				feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKE );
			}
			
			//Set comment button.
			feedCommentButton.setBrandFeedData( brandFeedData );
			
			//Set wishlist button.
			feedWishlistButton.setBrandFeedData( brandFeedData );
			
			return brandFeedItem;
		}
	
	}

	@Override
	public void onClick(View v) {
		if( v instanceof MyFeedCommentButton ){
			if(listener != null){
				MyFeedCommentButton feedCommentButton = (MyFeedCommentButton) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductCommentLayout.SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_ACT_ID, feedCommentButton.getBrandFeedData().getId() );
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_USER_ID, feedCommentButton.getBrandFeedData().getActor().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST );
			}
		}else if( v instanceof MyfeedWishlistButton ){
			if(listener != null){
				MyfeedWishlistButton feedWishlistButton = (MyfeedWishlistButton) v;
				String wishlistId = null;
				String productActivityId = feedWishlistButton.getBrandFeedData().getId();
				if( feedWishlistButton.getBrandFeedData().getMyRelation() != null &&
					feedWishlistButton.getBrandFeedData().getMyRelation().getArrayWishlistId() != null ){
					wishlistId = feedWishlistButton.getBrandFeedData().getMyRelation().getArrayWishlistId().toString();
				}
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductWishlistLayout.SHARE_PREF_WISHLLIST_VALUE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductWishlistLayout.SHARE_PREF_KEY_PRODUCT_ID, productActivityId );
				prefsEditor.putString( ProductWishlistLayout.SHARE_PREF_KEY_WISHLIST_ID, wishlistId );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_WISHLIST );
			}
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}else if( v instanceof BrandFeedItem ){
			if(listener != null){
				BrandFeedItem brandFeedItem = (BrandFeedItem) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, brandFeedItem.getProductData().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
			}
		}
	}

}
