package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

public class FollowButton extends ImageButton {
	
	public static final int BUTTON_STATUS_UNFOLLOW = 0;
	public static final int BUTTON_STATUS_FOLLOWING = 1;
	
	private ActorData actorData;
	private int status;
	
	public FollowButton(Context context) {
		super(context);
		this.setScaleType( ScaleType.FIT_CENTER );
		this.setBackgroundResource( 0 );
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
