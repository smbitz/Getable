package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFeedAddNewProductRow extends LinearLayout {
	
	private ImageView userImageView;
	private ImageView productImageView;
	private TextView userName;
	private TextView productName;
	private TextView numLike;
	private TextView numComment;
	private ProductActivityData activityData;
	
	public MyFeedAddNewProductRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedaddnewproductrow, this);
		
		userImageView = (ImageView) findViewById( R.id.myFeedAddNewProductRowUserImage );
		productImageView = (ImageView) findViewById( R.id.myFeedAddNewProductRowProductImage );
		userName = (TextView) findViewById( R.id.myFeedAddNewProductRowUserName );
		productName = (TextView) findViewById( R.id.myFeedAddNewProductRowProductName );
		numLike = (TextView) findViewById( R.id.myFeedAddNewProductRowLikeNum );
		numComment = (TextView) findViewById( R.id.myFeedAddNewProductRowCommentNum );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		productName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}
	
	public ImageView getUserImageView(){
		return userImageView;
	}

	public ImageView getProducImageView(){
		return productImageView;
	}

	public void setUserName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setProductName(String setProductName) {
		productName.setText( setProductName );
	}

	public void setNumLike(String setNumLike) {
		numLike.setText( setNumLike );
	}

	public void setNumComment(String setNumComment) {
		numComment.setText( setNumComment );
	}
	
	public void setActivityData(ProductActivityData setData){
		activityData = setData;
	}
	
	public ProductActivityData getActivityData(){
		return activityData;
	}
	
}
