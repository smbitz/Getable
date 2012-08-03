package com.codegears.getable.ui;

import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.widget.TextView;

public class UserName extends TextView {
	
	private ActorData actorData;

	public UserName(Context context) {
		super(context);
		this.setMaxLines( 1 );
	}

	public void setActorData(ActorData actor) {
		actorData = actor;
	}

	public ActorData getActor() {
		return actorData;
	}

}
