package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class UserProfileHeader extends LinearLayout {
	
	private TextView userName;
	private ActorData userData;
	private UserProfileImageLayout userProfileImageLayout;
	private LinearLayout followButtonLayout;
	private FollowButton followButton;
	private TextView postTime;
	private TextView moreTextName;
	
	public UserProfileHeader(Context context) {
		super(context);
		View.inflate(context, R.layout.userprofileheader, this);
		
		userName = (TextView) findViewById( R.id.userProfileName );
		userProfileImageLayout = (UserProfileImageLayout) findViewById( R.id.userProfileImageLayout );
		postTime = (TextView) findViewById( R.id.userProfilePostTime );
		followButtonLayout = (LinearLayout) findViewById( R.id.userProfileHeaderFollowButtonLayout );
		moreTextName = (TextView) findViewById( R.id.userProfileMoreTexName );
		
		//Set font
		userName.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		postTime.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		followButton = new FollowButton( context );
		followButton.setScaleType( ScaleType.FIT_START );
		followButtonLayout.addView( followButton );
	}

	public void setName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setData(ActorData setUserData) {
		userData = setUserData;
	}
	
	public void setPostTime(String setTimeText){
		postTime.setText( setTimeText );
	}
	
	public void setMoreTextNameVisible(){
		moreTextName.setVisibility( View.VISIBLE );
	}
	
	public ActorData getUserData(){
		return userData;
	}
	
	public UserProfileImageLayout getUserProfileImageLayout(){
		return userProfileImageLayout;
	}
	
	public FollowButton getFollowButton(){
		return followButton;
	}
	
	public LinearLayout getFollowButtonLayout(){
		return followButtonLayout;
	}
}
