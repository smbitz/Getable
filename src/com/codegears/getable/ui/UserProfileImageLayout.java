package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UserProfileImageLayout extends LinearLayout {
	
	private ImageView userImage;
	private String userId;
	
	public UserProfileImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.userprofileimagelayout, this);
		
		userImage = (ImageView) findViewById( R.id.userProfileImageLayoutImage );
	}
	
	public void setUserImage( Bitmap setBitmap ){
		userImage.setImageBitmap( setBitmap );
	}
	
	public void setUserId( String setUserId ){
		userId = setUserId;
	}
	
	public String getUserId(){
		return userId;
	}
	
}
