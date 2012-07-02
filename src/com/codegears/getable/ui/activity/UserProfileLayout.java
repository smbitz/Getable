package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.FollowButton;
import com.codegears.getable.ui.UserProfileHeader;
import com.codegears.getable.ui.UserProfileImageLayout;
import com.codegears.getable.ui.UserToggleButton;
import com.codegears.getable.ui.tabbar.TabBar;
import com.codegears.getable.ui.tabbar.TabBarListener;
import com.codegears.getable.util.Config;
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
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class UserProfileLayout extends AbstractViewLayout implements OnClickListener, BodyLayoutStackListener, TabBarListener {

	public static final String GET_NUMBER_OF_PRODUCT = "numberOfProducts";
	public static final String SHARE_PREF_VALUE_USER_ID = "SHARE_PREF_PRODUCT_USER_ID";
	public static final String SHARE_PREF_KEY_USER_ID = "SHARE_PREF_KEY_USER_ID";
	
	private BodyLayoutStackListener listener;
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private UserGalleryLayout userGalleryLayoutPhotos;
	private UserGalleryLayout userGalleryLayoutLikes;
	private UserWishlistsLayout userWishlistsLayout;
	private UserFollowLayout userFollowersLayout;
	private UserFollowLayout userFollowingsLayout;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private String userId;
	private String getUserDataURL;
	private Config config;
	private UserProfileImageLayout userProfileImageLayout;
	private UserToggleButton photoColumnButton;
	private UserToggleButton likesColumnButton;
	private UserToggleButton wishlistsColumnButton;
	private UserToggleButton followersColumnButton;
	private UserToggleButton followingsColumnButton;
	private String photoTextButton;
	private String likesTextButton;
	private String wishlistsTextButton;
	private String followersTextButton;
	private String followingsTextButton;
	private ProgressDialog loadingDialog;
	private FollowButton userHeaderFollowButton;
	private String followUserURL;
	private String unFollowUserURL;
	//private List<String> appCookie;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	
	public UserProfileLayout( Activity activity ) {
		super( activity );
		View.inflate( activity, R.layout.userprofilelayout, this );
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
		userId = myPreferences.getString( SHARE_PREF_KEY_USER_ID, null );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		loadingDialog.show();
		
		userHeader = new UserProfileHeader( this.getContext() );
		config = new Config( this.getActivity() );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		tabBar = (TabBar) findViewById( R.id.userProfileTabBar );
		bodyLayout = (ViewGroup) findViewById( R.id.userProfileBodyLayout );
		userProfileImageLayout = (UserProfileImageLayout) userHeader.getUserProfileImageLayout();
		userHeaderFollowButton = (FollowButton) userHeader.getFollowButton();
		headerLayout = (LinearLayout) findViewById( R.id.userProfileHeaderLayout );
		userHeader.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ) );
		headerLayout.addView( userHeader );
		
		userHeaderFollowButton.setOnClickListener( this );
		
		tabBar.setBodyLayout( bodyLayout );
		
		//Set URL data.
		String getPhotosDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+"/activities.json?page.number=1&page.size=32";
		String getLikesDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+"/activities.json?page.number=1&page.size=32&type=3";
		String getWishlistsDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+"/wishlists.json?page.number=1&page.size=32";
		String getFollowersDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+"/followers.json?page.number=1&page.size=32";
		String getFollowingsDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+"/followings.json?page.number=1&page.size=32";
		
		//---- First Layout ----//
		photoColumnButton = new UserToggleButton( this.getContext() );
		photoColumnButton.setText("");
		photoColumnButton.setTextOn( photoTextButton );
		photoColumnButton.setTextOff( photoTextButton );
		photoColumnButton.setBackgroundResource( R.drawable.user_layout_button_1 );
		photoColumnButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
		userGalleryLayoutPhotos = new UserGalleryLayout( this.getActivity(), getPhotosDataURL, UserGalleryLayout.USER_GALLERY_VIEW_TYPE_PHOTOS );
		userGalleryLayoutPhotos.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		userGalleryLayoutPhotos.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( photoColumnButton, userGalleryLayoutPhotos );
        
        //---- Second Layout ----//
        likesColumnButton = new UserToggleButton( this.getContext() );
        likesColumnButton.setText("");
        likesColumnButton.setTextOn( likesTextButton );
        likesColumnButton.setTextOff( likesTextButton );
        likesColumnButton.setBackgroundResource( R.drawable.user_layout_button_2 );
        likesColumnButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        userGalleryLayoutLikes = new UserGalleryLayout( this.getActivity(), getLikesDataURL, UserGalleryLayout.USER_GALLERY_VIEW_TYPE_LIKES );
        userGalleryLayoutLikes.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userGalleryLayoutLikes.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( likesColumnButton, userGalleryLayoutLikes );
        
        //---- Third Layout ----//
        wishlistsColumnButton = new UserToggleButton( this.getContext() );
        wishlistsColumnButton.setText("");
        wishlistsColumnButton.setTextOn( wishlistsTextButton );
        wishlistsColumnButton.setTextOff( wishlistsTextButton );
        wishlistsColumnButton.setBackgroundResource( R.drawable.user_layout_button_3 );
        wishlistsColumnButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        userWishlistsLayout = new UserWishlistsLayout( this.getActivity(), getWishlistsDataURL );
        userWishlistsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userWishlistsLayout.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( wishlistsColumnButton, userWishlistsLayout );
        
        //---- Fourth Layout ----//
        followersColumnButton = new UserToggleButton( this.getContext() );
        followersColumnButton.setText("");
        followersColumnButton.setTextOn( followersTextButton );
        followersColumnButton.setTextOff( followersTextButton );
        followersColumnButton.setBackgroundResource( R.drawable.user_layout_button_4 );
        followersColumnButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        userFollowersLayout = new UserFollowLayout( this.getActivity(), getFollowersDataURL, UserFollowLayout.GET_FOLLOWERS );
        userFollowersLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userFollowersLayout.setBackgroundColor( 0xFFFFFFFF );
        tabBar.addTab( followersColumnButton, userFollowersLayout );
        
        //---- Fifth Layout ----//
        followingsColumnButton = new UserToggleButton( this.getContext() );
        followingsColumnButton.setText("");
        followingsColumnButton.setTextOn( followingsTextButton );
        followingsColumnButton.setTextOff( followingsTextButton );
        followingsColumnButton.setBackgroundResource( R.drawable.user_layout_button_5 );
        followingsColumnButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        userFollowingsLayout = new UserFollowLayout( this.getActivity(), getFollowingsDataURL, UserFollowLayout.GET_FOLLOWINGS );
        userFollowingsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userFollowingsLayout.setBackgroundColor( 0xFFFFFFFF );
        tabBar.addTab( followingsColumnButton, userFollowingsLayout );
        
        photoColumnButton.setSelectedText();
        likesColumnButton.setNormalText();
        wishlistsColumnButton.setNormalText();
        followersColumnButton.setNormalText();
        followingsColumnButton.setNormalText();
        
        tabBar.setTabBarListener( this );
        
        getUserDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+userId+".json";
        
        //NetworkThreadUtil.getRawDataWithCookie(getUserDataURL, null, appCookie, this);
        asyncHttpClient.get( getUserDataURL, new JsonHttpResponseHandler(){
        	@Override
        	public void onSuccess(JSONObject getJsonObject) {
        		super.onSuccess(getJsonObject);
        		onGetUserDataURL(getJsonObject);
        	}
        });
	}
	
	private void onGetUserDataURL(JSONObject jsonObject){
		ActorData newData = null;
		//Load Product Data
		newData = new ActorData( jsonObject );
		
		//Load Product Image
		URL userPictureURL = null;
		Bitmap userImageBitmap = null;
		try {
			userPictureURL = new URL( newData.getPicture().getImageUrls().getImageURLT() );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			userImageBitmap = BitmapFactory.decodeStream( userPictureURL.openConnection().getInputStream() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Set User Header
		final String setUserName = newData.getName();
		final Bitmap setUserImage = userImageBitmap;
		final ActorData setActorData = newData;
		
		//Set number button
		JSONObject statisticJson = null;
		try {
			statisticJson = jsonObject.getJSONObject("statistic");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		photoTextButton = statisticJson.optString("numberOfProducts");
		likesTextButton = statisticJson.optString("numberOfLikes");
		wishlistsTextButton = statisticJson.optString("numberOfWishlists");
		followersTextButton = statisticJson.optString("numberOfFollowers");
		followingsTextButton = statisticJson.optString("numberOfFollowings");
		
		final String setNumberPhotos = photoTextButton;
		final String setNumberLikes = likesTextButton;
		final String setNumberWishlists = wishlistsTextButton;
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				//Set User Header
				userHeader.setName( setUserName );
				userHeader.setData( setActorData );
				if( setUserImage != null ){
					userProfileImageLayout.setUserImage( setUserImage );
				}
				
				if( app.getUserId().equals( setActorData.getId() ) ){
					userHeaderFollowButton.setVisibility( View.INVISIBLE );
				}else{
					userHeaderFollowButton.setActorData( setActorData );
					
					//Set text/image follow/following
					if( setActorData.getMyRelation().getFollowActivity() != null ){
						//userHeaderFollowButton.setText( "Following" );
						userHeaderFollowButton.setBackgroundResource( R.drawable.button_following );
						userHeaderFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
					}else{
						//userHeaderFollowButton.setText( "Follow" );
						userHeaderFollowButton.setBackgroundResource( R.drawable.button_follow );
						userHeaderFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
					}
				}
				
				//Set number of likes.
				photoColumnButton.setText( setNumberPhotos );
				likesColumnButton.setText( setNumberLikes );
				wishlistsColumnButton.setText( setNumberWishlists );
				followersColumnButton.setText( followersTextButton );
				followingsColumnButton.setText( followingsTextButton );
			}
		});
		
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
		}
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
		userGalleryLayoutPhotos.setBodyLayoutStackListener( listener );
		userGalleryLayoutLikes.setBodyLayoutStackListener( listener );
		userWishlistsLayout.setBodyLayoutStackListener( listener );
		userFollowersLayout.setBodyLayoutStackListener( listener );
		userFollowingsLayout.setBodyLayoutStackListener( listener );
	}

	@Override
	public void onClick( View v ) {
		if( v.equals( userHeaderFollowButton ) ){
			final FollowButton followButton = (FollowButton) v;
			followButton.setEnabled( false );
			if( followButton.getFollowButtonStatus() == FollowButton.BUTTON_STATUS_UNFOLLOW ){
				String followUserId = followButton.getActorData().getId();
				
				followUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+followUserId+"/followers.json";
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "_a", "follow" );
				String postData = NetworkUtil.createPostData( newMapData );
				
				NetworkThreadUtil.getRawDataWithCookie(followUserURL, postData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							ActorData actorData = null;
							if( jsonObject.optJSONObject("followedUser") != null ){
								actorData = new ActorData( jsonObject.optJSONObject("followedUser") );
							}
							followButton.setActorData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						UserProfileLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								followButton.setEnabled( true );
							}
						});
					}
					
					@Override
					public void onNetworkFail(String urlString) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onNetworkDocSuccess(String urlString, Document document) {
						// TODO Auto-generated method stub
						
					}
				});*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "follow" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( followUserURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						ActorData actorData = null;
						if( jsonObject.optJSONObject("followedUser") != null ){
							actorData = new ActorData( jsonObject.optJSONObject("followedUser") );
						}
						followButton.setActorData( actorData );
						
						followButton.setEnabled( true );
					}
				});
				
				//Set text/image follow/following
				//followButton.setText( "Following" );
				followButton.setBackgroundResource( R.drawable.button_following );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
			}else if( followButton.getFollowButtonStatus() == FollowButton.BUTTON_STATUS_FOLLOWING ){
				String followActivityId = followButton.getActorData().getMyRelation().getFollowActivity().getId();
				
				unFollowUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+followActivityId+".json";
				/*Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "_a", "delete" );
				String postData = NetworkUtil.createPostData( newMapData );
				
				NetworkThreadUtil.getRawDataWithCookie(unFollowUserURL, postData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						UserProfileLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								followButton.setEnabled( true );
							}
						});
					}
					
					@Override
					public void onNetworkFail(String urlString) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onNetworkDocSuccess(String urlString, Document document) {
						// TODO Auto-generated method stub
						
					}
				});*/
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "delete" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( unFollowUserURL, params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						followButton.setEnabled( true );
					}
				});
				
				//Set text/image follow/following
				//followButton.setText( "Follow" );
				followButton.setBackgroundResource( R.drawable.button_follow );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
			}
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

	@Override
	public void onRequestBodyLayoutStack(int requestId) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( getUserDataURL ) ){
			ActorData newData = null;
			JSONObject jsonObject = null;
			try {
				//Load Product Data
				jsonObject = new JSONObject( result );
				newData = new ActorData( jsonObject );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Load Product Image
			URL userPictureURL = null;
			Bitmap userImageBitmap = null;
			try {
				userPictureURL = new URL( newData.getPicture().getImageUrls().getImageURLT() );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			try {
				userImageBitmap = BitmapFactory.decodeStream( userPictureURL.openConnection().getInputStream() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Set User Header
			final String setUserName = newData.getName();
			final Bitmap setUserImage = userImageBitmap;
			final ActorData setActorData = newData;
			
			//Set number button
			JSONObject statisticJson = null;
			try {
				statisticJson = jsonObject.getJSONObject("statistic");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			photoTextButton = statisticJson.optString("numberOfProducts");
			likesTextButton = statisticJson.optString("numberOfLikes");
			wishlistsTextButton = statisticJson.optString("numberOfWishlists");
			followersTextButton = statisticJson.optString("numberOfFollowers");
			followingsTextButton = statisticJson.optString("numberOfFollowings");
			
			final String setNumberPhotos = photoTextButton;
			final String setNumberLikes = likesTextButton;
			final String setNumberWishlists = wishlistsTextButton;
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					//Set User Header
					userHeader.setName( setUserName );
					userHeader.setData( setActorData );
					if( setUserImage != null ){
						userProfileImageLayout.setUserImage( setUserImage );
					}
					
					if( app.getUserId().equals( setActorData.getId() ) ){
						userHeaderFollowButton.setVisibility( View.INVISIBLE );
					}else{
						userHeaderFollowButton.setActorData( setActorData );
						
						//Set text/image follow/following
						if( setActorData.getMyRelation().getFollowActivity() != null ){
							//userHeaderFollowButton.setText( "Following" );
							userHeaderFollowButton.setBackgroundResource( R.drawable.button_following );
							userHeaderFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
						}else{
							//userHeaderFollowButton.setText( "Follow" );
							userHeaderFollowButton.setBackgroundResource( R.drawable.button_follow );
							userHeaderFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
						}
					}
					
					//Set number of likes.
					photoColumnButton.setText( setNumberPhotos );
					likesColumnButton.setText( setNumberLikes );
					wishlistsColumnButton.setText( setNumberWishlists );
					followersColumnButton.setText( followersTextButton );
					followingsColumnButton.setText( followingsTextButton );
				}
			});
			
			if( loadingDialog.isShowing() ){
				loadingDialog.dismiss();
			}
		}else if( urlString.equals( followUserURL ) ){
			//On click follow result.
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public void onTabBarPerform(CompoundButton button) {
		if( button.equals( photoColumnButton ) ){
			photoColumnButton.setSelectedText();
	        likesColumnButton.setNormalText();
	        wishlistsColumnButton.setNormalText();
	        followersColumnButton.setNormalText();
	        followingsColumnButton.setNormalText();
		}else if( button.equals( likesColumnButton ) ){
			photoColumnButton.setNormalText();
	        likesColumnButton.setSelectedText();
	        wishlistsColumnButton.setNormalText();
	        followersColumnButton.setNormalText();
	        followingsColumnButton.setNormalText();
		}else if( button.equals( wishlistsColumnButton ) ){
			photoColumnButton.setNormalText();
	        likesColumnButton.setNormalText();
	        wishlistsColumnButton.setSelectedText();
	        followersColumnButton.setNormalText();
	        followingsColumnButton.setNormalText();
		}else if( button.equals( followersColumnButton ) ){
			photoColumnButton.setNormalText();
	        likesColumnButton.setNormalText();
	        wishlistsColumnButton.setNormalText();
	        followersColumnButton.setSelectedText();
	        followingsColumnButton.setNormalText();
		}else if( button.equals( followingsColumnButton ) ){
			photoColumnButton.setNormalText();
	        likesColumnButton.setNormalText();
	        wishlistsColumnButton.setNormalText();
	        followersColumnButton.setNormalText();
	        followingsColumnButton.setSelectedText();
		}
	}

}
