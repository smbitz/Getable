package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFeedFollowingRow extends LinearLayout {
	
	private TextView userName;
	private TextView targetUserName;
	private ImageView userImageView;
	private ActorData targetActorData;
	
	public MyFeedFollowingRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedfollowingrow, this);
		
		userName = (TextView) findViewById( R.id.myFeedFollowingUserName );
		targetUserName = (TextView) findViewById( R.id.myFeedFollowingTargetUserName );
		userImageView = (ImageView) findViewById( R.id.myFeedFollowingUserImage );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		targetUserName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}

	public void setUserName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setTargetUserName(String setTargetUserName) {
		targetUserName.setText( setTargetUserName );
	}

	public void setTargetActorData(ActorData setFollowedUser) {
		targetActorData = setFollowedUser;
	}
	
	public ActorData getTargetActorData() {
		return targetActorData;
	}
	
	public ImageView getUserImageView(){
		return userImageView;
	}

}
