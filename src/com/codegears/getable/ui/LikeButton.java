package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.widget.Button;

public class LikeButton extends Button {
	
	public static final int BUTTON_STATUS_LIKE = 0;
	public static final int BUTTON_STATUS_LIKED = 1;
	
	private int buttonStatus;
	private String likeId;
	
	public LikeButton(Context context) {
		super(context);
		//this.setText( "Like" );
		this.setBackgroundResource( R.drawable.button_like );
	}

	public void setButtonStatus(int buttonStatusLike) {
		buttonStatus = buttonStatusLike;
	}
	
	public void setLikeId(String setId) {
		likeId = setId;
	}
	
	public int getButtonStatus(){
		return buttonStatus;
	}
	
	public String getLikeId(){
		return likeId;
	}
	
}
