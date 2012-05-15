package com.codegears.getable.ui;

import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.widget.Button;

public class FollowButton extends Button {
	
	private ActorData actorData;
	
	public FollowButton(Context context) {
		super(context);
		this.setText( "Follow" );
	}
	
	public void setActorData( ActorData setActorData ){
		actorData = setActorData;
	}
	
	public ActorData getActorData(){
		return actorData;
	}

}
