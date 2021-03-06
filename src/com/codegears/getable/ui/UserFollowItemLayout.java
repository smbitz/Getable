package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActorFollowData;
import com.codegears.getable.data.ProductActorLikeData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserFollowItemLayout extends LinearLayout {
	
	private ImageView userImage;
	private TextView mainText;
	private TextView secondText;
	private ProductActorFollowData followUserData;
	private ProductActorLikeData likeUserData;
	private LinearLayout followButtonLayout;
	private FollowButton followButton;
	private ActorData actorData;

	public UserFollowItemLayout(Context context) {
		super(context);
		View.inflate(context, R.layout.userfollowitemlayout, this);
		
		userImage = (ImageView) findViewById( R.id.userFollowUserImage );
		mainText = (TextView) findViewById( R.id.userFollowMainText );
		secondText = (TextView) findViewById( R.id.userFollowSecondText );
		followButtonLayout = (LinearLayout) findViewById( R.id.userFollowItemFollowbuttonLayout );
		followButton = new FollowButton( context );
		LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
		params.gravity = Gravity.CENTER;
		followButton.setLayoutParams( params );
		followButtonLayout.addView( followButton );
		
		//Set font
		mainText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		secondText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
	}
	
	public void setUserImage( Bitmap setBitmap ){
		userImage.setImageBitmap( setBitmap );
	}
	
	public void setUserImageDefault(){
		userImage.setImageResource( R.drawable.user_image_default );
	}
	
	public void setMainText( String setString ){
		mainText.setText( setString );
	}
	
	public void setSecondText( String setString ){
		secondText.setText( setString );
	}

	public void setFollowUserData(ProductActorFollowData productActorFollowersData) {
		followUserData = productActorFollowersData;
	}
	
	public void setActorData(ActorData setActorData) {
		actorData = setActorData;
	}
	
	public ProductActorFollowData getFollowUserData(){
		return followUserData;
	}
	
	public void setLikeUserData(ProductActorLikeData producActorLikeData) {
		likeUserData = producActorLikeData;
	}
	
	public ProductActorLikeData getLikeUserData(){
		return likeUserData;
	}
	
	public ImageView getUserImageView(){
		return userImage;
	}
	
	public FollowButton getFollowButton(){
		return followButton;
	}
	
	public ActorData getActorData(){
		return actorData;
	}

}
