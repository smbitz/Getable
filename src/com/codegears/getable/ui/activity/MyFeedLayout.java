package com.codegears.getable.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.LikeButton;
import com.codegears.getable.ui.MyFeedAddNewProductRow;
import com.codegears.getable.ui.MyFeedCommentButton;
import com.codegears.getable.ui.MyFeedCommentRow;
import com.codegears.getable.ui.MyFeedFollowingRow;
import com.codegears.getable.ui.MyFeedLikeButton;
import com.codegears.getable.ui.MyFeedLikeRow;
import com.codegears.getable.ui.MyfeedWishlistButton;
import com.codegears.getable.ui.ProductImageThumbnail;
import com.codegears.getable.ui.ProductNumComment;
import com.codegears.getable.util.CalculateTime;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;

public class MyFeedLayout extends AbstractViewLayout implements OnClickListener {
	
	private static final String FEED_TYPE_ADD_PRODUCT = "1";
	private static final String FEED_TYPE_COMMENT = "2";
	private static final String FEED_TYPE_LIKE = "3";
	private static final String FEED_TYPE_FOLLOW = "4";
	private static final String FEED_TYPE_UPDATE_PRODUCT = "7";
	
	private BodyLayoutStackListener listener;
	private Config config;
	private MyApp app;
	private ArrayList<ProductActivityData> arrayFeedData;
	private ListView feedListView;
	private FeedAdapter feedAdapter;
	private ImageLoader imageLoader;
	private String getProductLikeURL;
	private String getProductUnLikeURL;
	private String getFeedURL;
	private AsyncHttpClient asyncHttpClient;
	private LinearLayout findFriendLayout;
	private Button findFriendButton;
	private ProgressDialog loadingDialog;
	private ActorData currentActorData;
	private MyFeedAddNewProductRow myFeedAddNewProductRow;
	
	public MyFeedLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.myfeedlayout, this );
		
		config = new Config( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		arrayFeedData = new ArrayList<ProductActivityData>();
		feedListView = (ListView) findViewById( R.id.myFeedLayoutListView );
		feedAdapter = new FeedAdapter();
		imageLoader = new ImageLoader( this.getContext() );
		findFriendLayout = (LinearLayout) findViewById( R.id.myFeedLayoutFindFriendLayout );
		findFriendButton = (Button) findViewById( R.id.myFeedLayoutFindFriendButton );
		currentActorData = app.getCurrentProfileData();
		
		findFriendButton.setOnClickListener( this );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		//Set line at footer
		feedListView.addFooterView( new FooterListView( this.getContext() ) );
		
		getFeedURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/feed.json"+MyApp.DEFAULT_URL_VAR_1;
		
		//NetworkThreadUtil.getRawDataWithCookie(getFeedURL, null, appCookie, this);
		loadData();
	}
	
	private void recycleResource() {
		arrayFeedData.clear();
	}
	
	public void loadData(){
		loadingDialog.show();
		
		//Clear Data
		arrayFeedData.clear();
		
		asyncHttpClient.get( getFeedURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject resultObject) {
				super.onSuccess(resultObject);
				
				try {
					//Check type of feed.
					if( resultObject.optJSONArray( "entities" ) != null ){
						JSONArray newArray = resultObject.optJSONArray( "entities" );
						
						for(int i = 0; i<newArray.length(); i++){
							JSONObject entitiesObject = (JSONObject) newArray.get(i);
							
							ProductActivityData activityData = new ProductActivityData( entitiesObject );
							arrayFeedData.add( activityData );
						}
					}
					
					if( arrayFeedData.size() > 0 ){
						feedAdapter.setData( arrayFeedData );
						feedListView.setAdapter( feedAdapter );
					}else{
						feedListView.setVisibility( View.GONE );
						findFriendLayout.setVisibility( View.VISIBLE );
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}
	
	private class FeedAdapter extends BaseAdapter {
		
		ArrayList<ProductActivityData> data;
		
		public void setData(ArrayList<ProductActivityData> arrayFeedData) {
			data = arrayFeedData;
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
			
			final ProductActivityData feedData = data.get( position );
			String feedType = feedData.getType().getId();
			String postTimeText = CalculateTime.getPostTime( feedData.getActivityTime() );
			
			if( feedType.equals( FEED_TYPE_ADD_PRODUCT ) ){
				//final MyFeedAddNewProductRow newProductRow = new MyFeedAddNewProductRow( MyFeedLayout.this.getContext() );
				if( convertView instanceof MyFeedAddNewProductRow ){
					myFeedAddNewProductRow = (MyFeedAddNewProductRow) convertView;
				}else{
					myFeedAddNewProductRow = new MyFeedAddNewProductRow( MyFeedLayout.this.getContext() );
				}
				final MyFeedAddNewProductRow newProductRow = myFeedAddNewProductRow;
				
				String userName = feedData.getActor().getName();
				String productName = feedData.getProduct().getBrand().getName();
				String numLike = String.valueOf( feedData.getStatisitc().getNumberOfLikes() );
				String numComment = String.valueOf( feedData.getStatisitc().getNumberOfComments() );
				String userImageURL = feedData.getActor().getPicture().getImageUrls().getImageURLT();
				String productImageURL = feedData.getProduct().getProductPicture().getImageUrls().getImageURLT();
				
				//Set Text
				newProductRow.setUserName( userName );
				newProductRow.setProductName( productName );
				newProductRow.setNumLike( numLike );
				newProductRow.setNumComment( numComment );
				newProductRow.setPostTime( postTimeText );
				
				//Set Like, Comment, Wishlist Button
				MyFeedLikeButton feedLikeButton = newProductRow.getLikeButton();
				MyFeedCommentButton feedCommentButton = newProductRow.getCommentButton();
				MyfeedWishlistButton feedWishlistButton = newProductRow.getWishlistButton();
				
				feedLikeButton.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View v) {
						if( v instanceof MyFeedLikeButton ){
							loadingDialog.show();
							
							final MyFeedLikeButton feedLikeButton = (MyFeedLikeButton) v;
							String productActivityId = feedLikeButton.getActivityData().getId();
							getProductLikeURL = config.get( MyApp.URL_GET_PRODUCT_ACTIVITIES_BY_ID ).toString()+productActivityId+"/likes.json";
							
							if( feedLikeButton.getButtonStatus() == LikeButton.BUTTON_STATUS_LIKE ){
								asyncHttpClient.post( getProductLikeURL, new JsonHttpResponseHandler(){
									@Override
									public void onSuccess(JSONObject jsonObject) {
										super.onSuccess(jsonObject);
										ProductActivityData newData = new ProductActivityData( jsonObject );
										String likeId = newData.getLike().getActivityData().getId();
										feedLikeButton.setLikeId( likeId );
										//feedData.getMyRelation().setLike( newData.getMyRelation().getLike() );
										feedData.setMyRelation( newData.getLike().getActivityData().getMyRelation() );
										
										//On click like result.
										//Set image liked
										feedLikeButton.setBackgroundResource( R.drawable.myfeed_liked_icon );
										feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKED );
										
										int changeLikeNum = (newProductRow.getNumLike())+1;
										newProductRow.setNumLike( String.valueOf( changeLikeNum ) );
										feedData.getStatisitc().setNumberOfLikes( changeLikeNum );
										
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
										feedData.setMyRelation( null );
										
										int changeLikeNum = (newProductRow.getNumLike())-1;
										newProductRow.setNumLike( String.valueOf( changeLikeNum ) );
										feedData.getStatisitc().setNumberOfLikes( changeLikeNum );
										
										if( loadingDialog.isShowing() ){
											loadingDialog.dismiss();
										}
									}
								});
							}
						}
					}
				});
				feedCommentButton.setOnClickListener( MyFeedLayout.this );
				feedWishlistButton.setOnClickListener( MyFeedLayout.this );
				
				//Set Like, Comment, Wishlist Data
				//Set image like button.
				if( feedData.getMyRelation() != null &&
					feedData.getMyRelation().getLike() != "" ){
					//Set image liked.
					//likeButton.setText( "liked" );
					feedLikeButton.setBackgroundResource( R.drawable.myfeed_liked_icon );
					feedLikeButton.setLikeId( feedData.getMyRelation().getLike() );
					feedLikeButton.setActivityData( feedData );
					feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKED );
				}else{
					//Set image like.
					//likeButton.setText( "like" );
					feedLikeButton.setBackgroundResource( R.drawable.myfeed_like_icon );
					feedLikeButton.setActivityData( feedData );
					feedLikeButton.setButtonStatus( LikeButton.BUTTON_STATUS_LIKE );
				}
				
				//Set comment button.
				feedCommentButton.setProductData( feedData );
				
				//Set wishlist button.
				feedWishlistButton.setActivityData( feedData );
				
				//Set Data
				newProductRow.setActivityData( feedData );
				
				//Set image
				ImageView userImageView = newProductRow.getUserImageView();
				ImageView productImageView = newProductRow.getProducImageView();
				
				imageLoader.DisplayImage(userImageURL, MyFeedLayout.this.getActivity(), userImageView, true, asyncHttpClient);
				imageLoader.DisplayImage(productImageURL, MyFeedLayout.this.getActivity(), productImageView, true, asyncHttpClient);
				
				newProductRow.setOnClickListener( MyFeedLayout.this );
				
				return newProductRow;
			}else if( feedType.equals( FEED_TYPE_COMMENT ) ){
				//MyFeedCommentRow commentRow = new MyFeedCommentRow( MyFeedLayout.this.getContext() );
				MyFeedCommentRow commentRow;
				if( convertView instanceof MyFeedCommentRow ){
					commentRow = (MyFeedCommentRow) convertView;
				}else{
					commentRow = new MyFeedCommentRow( MyFeedLayout.this.getContext() );
				}
				
				String userName = feedData.getActor().getName();
				String targetUserName = feedData.getComment().getProductActivityData().getActor().getName();
				String commentText = feedData.getComment().getCommentText();
				String userImageURL = feedData.getActor().getPicture().getImageUrls().getImageURLT();
				String productImageURL = feedData.getComment().getProductActivityData().getProduct().getProductPicture().getImageUrls().getImageURLT();
				
				//Set Text
				commentRow.setUserName( userName );
				if( feedData.getComment().getProductActivityData().getActor().getId().equals( currentActorData.getId() ) ){
					commentRow.setTargetUserNameMine();
				}else{
					commentRow.setTargetUserName( targetUserName );
				}
				commentRow.setCommentText( commentText );
				commentRow.setPostTime( postTimeText );
				
				//Set Data
				commentRow.setActivityData( feedData.getComment().getProductActivityData() );
				
				//Set image
				ImageView userImageView = commentRow.getUserImageView();
				ImageView productImageView = commentRow.getProducImageView();
				
				imageLoader.DisplayImage(userImageURL, MyFeedLayout.this.getActivity(), userImageView, true, asyncHttpClient);
				imageLoader.DisplayImage(productImageURL, MyFeedLayout.this.getActivity(), productImageView, true, asyncHttpClient);
				
				commentRow.setOnClickListener( MyFeedLayout.this );
				
				return commentRow;
			}else if( feedType.equals( FEED_TYPE_LIKE ) ){
				//MyFeedLikeRow likeRow = new MyFeedLikeRow( MyFeedLayout.this.getContext() );
				MyFeedLikeRow likeRow;
				if( convertView instanceof MyFeedLikeRow ){
					likeRow = (MyFeedLikeRow) convertView;
				}else{
					likeRow = new MyFeedLikeRow( MyFeedLayout.this.getContext() );
				}
				
				String userName = feedData.getActor().getName();
				String targetUserName = feedData.getLike().getActivityData().getActor().getName();
				String userImageURL = feedData.getActor().getPicture().getImageUrls().getImageURLT();
				String productImageURL = feedData.getLike().getActivityData().getProduct().getProductPicture().getImageUrls().getImageURLT();
				
				//Set Text
				likeRow.setUserName( userName );
				if( feedData.getLike().getActivityData().getActor().getId().equals( currentActorData.getId() ) ){
					likeRow.setTargetUserNameMine();
				}else{
					likeRow.setTargetUserName( targetUserName );
				}
				likeRow.setPostTime( postTimeText );
				
				//Set Data
				likeRow.setActivityData( feedData.getLike().getActivityData() );
				
				//Set image
				ImageView userImageView = likeRow.getUserImageView();
				ImageView productImageView = likeRow.getProducImageView();
				
				imageLoader.DisplayImage(userImageURL, MyFeedLayout.this.getActivity(), userImageView, true, asyncHttpClient);
				imageLoader.DisplayImage(productImageURL, MyFeedLayout.this.getActivity(), productImageView, true, asyncHttpClient);
				
				likeRow.setOnClickListener( MyFeedLayout.this );
				
				return likeRow;
			}else if( feedType.equals( FEED_TYPE_FOLLOW ) ){
				//MyFeedFollowingRow followingRow = new MyFeedFollowingRow( MyFeedLayout.this.getContext() );
				MyFeedFollowingRow followingRow;
				if( convertView instanceof MyFeedFollowingRow ){
					followingRow = (MyFeedFollowingRow) convertView;
				}else{
					followingRow = new MyFeedFollowingRow( MyFeedLayout.this.getContext() );
				}
				
				String userName = feedData.getActor().getName();
				String targetUserName = feedData.getFollowedUser().getName();
				String userImageURL = feedData.getActor().getPicture().getImageUrls().getImageURLT();
				
				//Set Text
				followingRow.setUserName( userName );
				if( feedData.getFollowedUser().getId().equals( currentActorData.getId() ) ){
					followingRow.setTargetUserNameMine();
				}else{
					followingRow.setTargetUserName( targetUserName );
				}
				followingRow.setPostTime( postTimeText );
				
				//Set Data
				followingRow.setTargetActorData( feedData.getFollowedUser() );
				
				//Set image
				ImageView userImageView = followingRow.getUserImageView();
				
				imageLoader.DisplayImage(userImageURL, MyFeedLayout.this.getActivity(), userImageView, true, asyncHttpClient);
				
				followingRow.setOnClickListener( MyFeedLayout.this );
				
				return followingRow;
			}else{
				LinearLayout emptyLayout = new LinearLayout( MyFeedLayout.this.getContext() );
				return emptyLayout;
			}
			
			//return null;
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v instanceof MyFeedCommentButton ){
			if(listener != null){
				MyFeedCommentButton feedCommentButton = (MyFeedCommentButton) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductCommentLayout.SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_ACT_ID, feedCommentButton.getProductData().getId() );
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_USER_ID, feedCommentButton.getProductData().getActor().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST );
			}
		}else if( v instanceof MyfeedWishlistButton ){
			if(listener != null){
				MyfeedWishlistButton feedWishlistButton = (MyfeedWishlistButton) v;
				String wishlistId = null;
				String productActivityId = feedWishlistButton.getActivityData().getId();
				if( feedWishlistButton.getActivityData().getMyRelation() != null &&
					feedWishlistButton.getActivityData().getMyRelation().getArrayWishlistId() != null ){
					wishlistId = feedWishlistButton.getActivityData().getMyRelation().getArrayWishlistId().toString();
				}
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductWishlistLayout.SHARE_PREF_WISHLLIST_VALUE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductWishlistLayout.SHARE_PREF_KEY_PRODUCT_ID, productActivityId );
				prefsEditor.putString( ProductWishlistLayout.SHARE_PREF_KEY_WISHLIST_ID, wishlistId );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_WISHLIST );
			}
		}else if( v.equals( findFriendButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS );
			}
		}else if(listener != null){
			if( v instanceof  MyFeedAddNewProductRow ){
				MyFeedAddNewProductRow newProductRow = (MyFeedAddNewProductRow) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, newProductRow.getActivityData().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
			}else if( v instanceof  MyFeedCommentRow ){
				MyFeedCommentRow commentRow = (MyFeedCommentRow) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, commentRow.getActivityData().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
			}else if( v instanceof  MyFeedLikeRow ){
				MyFeedLikeRow likeRow = (MyFeedLikeRow) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, likeRow.getActivityData().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
			}else if( v instanceof  MyFeedFollowingRow ){
				MyFeedFollowingRow followingRow = (MyFeedFollowingRow) v;
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, followingRow.getTargetActorData().getId() );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
		}
	}

}
