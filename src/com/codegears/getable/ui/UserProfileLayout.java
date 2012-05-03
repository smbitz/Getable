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
	
	private BodyLayoutStackListener listener;
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private UserGalleryLayout userGalleryLayout;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private String userId;
	private String getUserDataURL;
	private Config config;
	private UserProfileImageLayout userProfileImageLayout;
	
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
		
		//---- First Layout ----//
        ToggleButton b1 = new ToggleButton(this.getContext());
        b1.setTextOff( "Photo" );
        b1.setTextOn( "Photo" );
        userGalleryLayout = new UserGalleryLayout( this.getActivity(), userId );
        userGalleryLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userGalleryLayout.setBodyLayoutStackListener( listener );
        userGalleryLayout.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( b1, userGalleryLayout );
        
        getUserDataURL = config.get( URL_DEFAULT ).toString()+"users/"+userId+".json";
        
        NetworkThreadUtil.getRawData( getUserDataURL, null, this);
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
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
			try {
				//Load Product Data
				JSONObject jsonObject = new JSONObject(result);
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
			
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					//Set User Header
					userHeader.setName( setUserName );
					userHeader.setData( setUserData );
					userProfileImageLayout.setUserImage( setUserImage );
				}
			});
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}

}
