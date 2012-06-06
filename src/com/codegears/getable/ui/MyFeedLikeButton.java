package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.widget.ImageButton;

public class MyFeedLikeButton extends ImageButton {
	
	public static final int BUTTON_STATUS_LIKE = 0;
	public static final int BUTTON_STATUS_LIKED = 1;
	
	private int buttonStatus;
	private String likeId;
	private ProductActivityData feedData;
	
	public MyFeedLikeButton(Context context) {
		super(context);
		this.setBackgroundResource( R.drawable.myfeed_like_icon );
	}

	public void setButtonStatus(int buttonStatusLike) {
		buttonStatus = buttonStatusLike;
	}
	
	public void setLikeId(String setId) {
		likeId = setId;
	}
	
	public void setActivityData(ProductActivityData setFeedData) {
		feedData = setFeedData;
	}
	
	public int getButtonStatus(){
		return buttonStatus;
	}
	
	public String getLikeId(){
		return likeId;
	}
	
	public ProductActivityData getActivityData(){
		return feedData;
	}
	
}
