package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFeedLikeRow extends LinearLayout {

	private TextView userName;
	private TextView targetUserName;
	private ImageView userImageView;
	private ImageView productImageView;
	private ProductActivityData activityData;
	
	public MyFeedLikeRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedlikerow, this);
		
		userName = (TextView) findViewById( R.id.myFeedLikeRowUserName );
		targetUserName = (TextView) findViewById( R.id.myFeedLikeRowTargetUserName );
		userImageView = (ImageView) findViewById( R.id.myFeedLikeRowUserImage );
		productImageView = (ImageView) findViewById( R.id.myFeedLikeRowProductImage );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		targetUserName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}

	public void setUserName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setTargetUserName(String setTargetUserName) {
		targetUserName.setText( setTargetUserName );
	}
	
	public ImageView getUserImageView(){
		return userImageView;
	}
	
	public ImageView getProducImageView(){
		return productImageView;
	}
	
	public void setActivityData(ProductActivityData setData){
		activityData = setData;
	}
	
	public ProductActivityData getActivityData(){
		return activityData;
	}
	
}
