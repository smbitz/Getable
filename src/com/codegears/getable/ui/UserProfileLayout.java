package com.codegears.getable.ui;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.R;
import com.codegears.getable.ui.tabbar.TabBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class UserProfileLayout extends AbstractViewLayout implements OnClickListener, BodyLayoutStackListener {

	public static final int LAYOUTCHANGE_BADGE = 3;
	
	private BodyLayoutStackListener listener;
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private UserGalleryLayout userGalleryLayout;
	private LinearLayout headerLayout;
	private UserProfileHeader userHeader;
	private String userId;
	
	public UserProfileLayout( Activity activity ) {
		super( activity );
		View.inflate( activity, R.layout.userprofilelayout, this );
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_USER_ID, this.getActivity().MODE_PRIVATE );
		userId = myPreferences.getString( ProductDetailLayout.SHARE_PREF_KEY_USER_ID, null );
		
		userHeader = new UserProfileHeader( this.getContext() );
		
		tabBar = (TabBar) findViewById( R.id.userProfileTabBar );
		bodyLayout = (ViewGroup) findViewById( R.id.userProfileBodyLayout );
		headerLayout = (LinearLayout) findViewById( R.id.userProfileHeaderLayout );
		headerLayout.addView( userHeader );
		
		tabBar.setBodyLayout( bodyLayout );
		
		//---- First Layout ----//
        ToggleButton b1 = new ToggleButton(this.getContext());
        b1.setTextOff( "Photo" );
        b1.setTextOn( "Photo" );
        userGalleryLayout = new UserGalleryLayout( this.getActivity() );
        userGalleryLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userGalleryLayout.setBodyLayoutStackListener( this );
        userGalleryLayout.setBackgroundColor( 0xFFFF0000 );
        userGalleryLayout.setUserId( userId );
        tabBar.addTab( b1, userGalleryLayout );
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

}
