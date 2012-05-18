package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserProfileHeader extends LinearLayout {
	
	private TextView userName;
	private ActorData userData;
	private UserProfileImageLayout userProfileImageLayout;
	private LinearLayout followButtonLayout;
	private FollowButton followButton;
	
	public UserProfileHeader(Context context) {
		super(context);
		View.inflate(context, R.layout.userprofileheader, this);
		
		userName = (TextView) findViewById( R.id.userProfileName );
		userProfileImageLayout = (UserProfileImageLayout) findViewById( R.id.userProfileImageLayout );
		followButtonLayout = (LinearLayout) findViewById( R.id.userProfileHeaderFollowButtonLayout );
		followButton = new FollowButton( context );
		followButtonLayout.addView( followButton );
	}

	public void setName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setData(ActorData setUserData) {
		userData = setUserData;
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
