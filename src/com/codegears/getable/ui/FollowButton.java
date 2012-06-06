package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.widget.Button;

public class FollowButton extends Button {
	
	public static final int BUTTON_STATUS_UNFOLLOW = 0;
	public static final int BUTTON_STATUS_FOLLOWING = 1;
	
	private ActorData actorData;
	private int status;
	
	public FollowButton(Context context) {
		super(context);
		this.setBackgroundResource( R.drawable.button_follow );
	}
	
	public void setActorData( ActorData setActorData ){
		actorData = setActorData;
	}
	
	public ActorData getActorData(){
		return actorData;
	}
	
	public void setFollowButtonStatus( int setStatus ){
		status = setStatus;
	}
	
	public int getFollowButtonStatus(){
		return status;
	}

}
