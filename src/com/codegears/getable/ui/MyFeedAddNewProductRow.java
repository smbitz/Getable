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
	private LinearLayout likeButtonLayout;
	private LinearLayout commentButtonLayout;
	private LinearLayout wishlistButtonLayout;
	private MyFeedLikeButton likeButton;
	private MyFeedCommentButton commentButton;
	private MyfeedWishlistButton wishlistButton;
	
	public MyFeedAddNewProductRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedaddnewproductrow, this);
		
		userImageView = (ImageView) findViewById( R.id.myFeedAddNewProductRowUserImage );
		productImageView = (ImageView) findViewById( R.id.myFeedAddNewProductRowProductImage );
		userName = (TextView) findViewById( R.id.myFeedAddNewProductRowUserName );
		productName = (TextView) findViewById( R.id.myFeedAddNewProductRowProductName );
		numLike = (TextView) findViewById( R.id.myFeedAddNewProductRowLikeNum );
		numComment = (TextView) findViewById( R.id.myFeedAddNewProductRowCommentNum );
		likeButtonLayout = (LinearLayout) findViewById( R.id.myFeedAddNewProductRowLikeImageLayout );
		commentButtonLayout = (LinearLayout) findViewById( R.id.myFeedAddNewProductRowCommentImageLayout );
		wishlistButtonLayout = (LinearLayout) findViewById( R.id.myFeedAddNewProductRowWishlistImageLayout );
		
		likeButton = new MyFeedLikeButton( this.getContext() );
		commentButton = new MyFeedCommentButton( this.getContext() );
		wishlistButton = new MyfeedWishlistButton( this.getContext() );
		
		likeButtonLayout.addView( likeButton );
		commentButtonLayout.addView( commentButton );
		wishlistButtonLayout.addView( wishlistButton );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		productName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
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
	
	public ImageView getUserImageView(){
		return userImageView;
	}

	public ImageView getProducImageView(){
		return productImageView;
	}
	
	public MyFeedLikeButton getLikeButton(){
		return likeButton;
	}
	
	public MyFeedCommentButton getCommentButton(){
		return commentButton;
	}
	
	public MyfeedWishlistButton getWishlistButton(){
		return wishlistButton;
	}
	
}
