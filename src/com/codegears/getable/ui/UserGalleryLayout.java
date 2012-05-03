package com.codegears.getable.ui;

import com.codegears.getable.BodyLayoutStackListener;

import android.app.Activity;
import android.content.Intent;

public class UserGalleryLayout extends AbstractViewLayout {

	private BodyLayoutStackListener listener;
	private String userId;
	
	public UserGalleryLayout(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutStackListener( BodyLayoutStackListener setListener ){
		this.listener = setListener;
	}
	
	public void setUserId( String setUserId ){
		this.userId = setUserId;
	}

}
