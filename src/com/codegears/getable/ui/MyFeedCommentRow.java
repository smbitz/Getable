package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFeedCommentRow extends LinearLayout {

	private TextView userName;
	private TextView targetUserName;
	private TextView commentText;
	private ImageView userImageView;
	private ImageView productImageView;
	private ProductActivityData activityData;
	
	public MyFeedCommentRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedcommentrow, this);
		
		userName = (TextView) findViewById( R.id.myFeedCommentRowUserName );
		targetUserName = (TextView) findViewById( R.id.myFeedCommentRowTargetUserName );
		commentText = (TextView) findViewById( R.id.myFeedCommentRowCommentText );
		userImageView = (ImageView) findViewById( R.id.myFeedCommentRowUserImage );
		productImageView = (ImageView) findViewById( R.id.myFeedCommentRowProductImage );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		targetUserName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}

	public void setUserName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setTargetUserName(String setTargetUserName) {
		targetUserName.setText( setTargetUserName );
	}

	public void setCommentText(String setCommentText) {
		commentText.setText( setCommentText );
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
