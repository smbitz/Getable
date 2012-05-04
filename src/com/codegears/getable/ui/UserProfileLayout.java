package com.codegears.getable.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.tabbar.TabBar;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class UserProfileLayout extends AbstractViewLayout implements OnClickListener, BodyLayoutStackListener, NetworkThreadListener {

	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String GET_NUMBER_OF_PRODUCT = "numberOfProducts";
	
	private BodyLayoutStackListener listener;
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private UserGalleryLayout userGalleryLayoutPhotos;
	private UserGalleryLayout userGalleryLayoutLikes;
	private UserWishlistsLayout userWishlistsLayout;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private String userId;
	private String getUserDataURL;
	private Config config;
	private UserProfileImageLayout userProfileImageLayout;
	private ToggleButton photoColumnButton;
	private ToggleButton likesColumnButton;
	private ToggleButton wishlistsColumnButton;
	private ToggleButton followersColumnButton;
	private ToggleButton followingColumnButton;
	private String photoTextButton;
	private String likesTextButton;
	private String wishlistsTextButton;
	private String followersTextButton;
	private String followingTextButton;
	
	public UserProfileLayout( Activity activity ) {
		super( activity );
		View.inflate( activity, R.layout.userprofilelayout, this );
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_USER_ID, this.getActivity().MODE_PRIVATE );
		userId = myPreferences.getString( ProductDetailLayout.SHARE_PREF_KEY_USER_ID, null );
		
		userHeader = new UserProfileHeader( this.getContext() );
		config = new Config( this.getActivity() );
		
		tabBar = (TabBar) findViewById( R.id.userProfileTabBar );
		bodyLayout = (ViewGroup) findViewById( R.id.userProfileBodyLayout );
		headerLayout = (LinearLayout) findViewById( R.id.userProfileHeaderLayout );
		headerLayout.addView( userHeader );
		userProfileImageLayout = (UserProfileImageLayout) userHeader.getUserProfileImageLayout();
		
		tabBar.setBodyLayout( bodyLayout );
		
		//Set URL data.
		String getPhotosDataURL = config.get( URL_DEFAULT ).toString()+"users/"+userId+"/activities.json";
		String getLikesDataURL = config.get( URL_DEFAULT ).toString()+"users/"+userId+"/activities.json";
		String getWishlistsDataURL = config.get( URL_DEFAULT ).toString()+"users/"+userId+"/wishlists.json";
		
		//---- First Layout ----//
		photoColumnButton = new ToggleButton( this.getContext() );
		photoColumnButton.setText( "PHOTOS" );
		photoColumnButton.setTextOn( photoTextButton );
		photoColumnButton.setTextOff( photoTextButton );
		userGalleryLayoutPhotos = new UserGalleryLayout( this.getActivity(), getPhotosDataURL );
		userGalleryLayoutPhotos.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		userGalleryLayoutPhotos.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( photoColumnButton, userGalleryLayoutPhotos );
        
        //---- Second Layout ----//
        likesColumnButton = new ToggleButton( this.getContext() );
        likesColumnButton.setText( "LIKES" );
        likesColumnButton.setTextOn( likesTextButton );
        likesColumnButton.setTextOff( likesTextButton );
        userGalleryLayoutLikes = new UserGalleryLayout( this.getActivity(), getLikesDataURL );
        userGalleryLayoutLikes.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userGalleryLayoutLikes.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( likesColumnButton, userGalleryLayoutLikes );
        
        //---- Third Layout ----//
        wishlistsColumnButton = new ToggleButton( this.getContext() );
        wishlistsColumnButton.setText( "WISHLISTS" );
        wishlistsColumnButton.setTextOn( wishlistsTextButton );
        wishlistsColumnButton.setTextOff( wishlistsTextButton );
        userWishlistsLayout = new UserWishlistsLayout( this.getActivity(), getWishlistsDataURL );
        userWishlistsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userWishlistsLayout.setBackgroundColor( 0xFFFFFFFF );
        tabBar.addTab( wishlistsColumnButton, userWishlistsLayout );
        
        //---- Fourth Layout ----//
        //---- Fifth Layout ----//
        
        getUserDataURL = config.get( URL_DEFAULT ).toString()+"users/"+userId+".json";
        
        NetworkThreadUtil.getRawData( getUserDataURL, null, this);
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
		userGalleryLayoutPhotos.setBodyLayoutStackListener( listener );
		userGalleryLayoutLikes.setBodyLayoutStackListener( listener );
		userWishlistsLayout.setBodyLayoutStackListener( listener );
	}

	@Override
	public void onClick( View view ) {
		
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestBodyLayoutStack(int requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
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
			final ActorData setUserData = newData;
			
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
			
			final String setNumberPhotos = photoTextButton;
			final String setNumberLikes = likesTextButton;
			final String setNumberWishlists = wishlistsTextButton;
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					//Set User Header
					userHeader.setName( setUserName );
					userHeader.setData( setUserData );
					userProfileImageLayout.setUserImage( setUserImage );
					
					//Set number of likes.
					photoColumnButton.setText( setNumberPhotos+"\nPHOTOS" );
					likesColumnButton.setText( setNumberLikes+"\nLIKES" );
					wishlistsColumnButton.setText( setNumberWishlists+"\nWISHLISTS" );
				}
			});
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}

}
