package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFeedLikeRow extends LinearLayout {

	private String userNameText;
	private String targetUserNameText;
	private ImageView userImageView;
	private ImageView productImageView;
	private ProductActivityData activityData;
	private TextView postTime;
	private TextView detailText;
	
	public MyFeedLikeRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedlikerow, this);
		
		userImageView = (ImageView) findViewById( R.id.myFeedLikeRowUserImage );
		productImageView = (ImageView) findViewById( R.id.myFeedLikeRowProductImage );
		postTime = (TextView) findViewById( R.id.myFeedLikeRowTime );
		detailText = (TextView) findViewById( R.id.myFeedLikeRowDetailText );
		
		//Set font
		postTime.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		detailText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		detailText.setText( Html.fromHtml( userNameText+" likes "+targetUserNameText+"'s post" ) );
	}

	public void setUserName(String setUserName) {
		userNameText = "<font color='#63bfec'>"+setUserName+"</font>";
		detailText.setText( Html.fromHtml( userNameText+" likes "+targetUserNameText+"'s post" ) );
	}

	public void setTargetUserName(String setTargetUserName) {
		targetUserNameText = "<font color='#63bfec'>"+setTargetUserName+"</font>";
		detailText.setText( Html.fromHtml( userNameText+" likes "+targetUserNameText+"'s post" ) );
	}
	
	public void setPostTime( String setPostTime ){
		postTime.setText( setPostTime );
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

	public void setTargetUserNameMine() {
		targetUserNameText = "your";
		detailText.setText( Html.fromHtml( userNameText+" likes "+targetUserNameText+" post" ) );
	}
	
}
