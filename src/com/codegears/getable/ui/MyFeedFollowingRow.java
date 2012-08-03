package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFeedFollowingRow extends LinearLayout {
	
	private String userNameText;
	private String targetUserNameText;
	private ImageView userImageView;
	private ActorData targetActorData;
	private TextView postTime;
	private TextView detailText;
	
	public MyFeedFollowingRow(Context context) {
		super(context);
		View.inflate(context, R.layout.myfeedfollowingrow, this);
		
		userImageView = (ImageView) findViewById( R.id.myFeedFollowingUserImage );
		postTime = (TextView) findViewById( R.id.myFeedFollowingTime );
		detailText = (TextView) findViewById( R.id.myFeedFollowingDetailText );
		
		//Set font
		postTime.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		detailText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		detailText.setText( Html.fromHtml( userNameText+" is now following "+targetUserNameText+"." ) );
	}

	public void setUserName(String setUserName) {
		userNameText = "<font color='#63bfec'>"+setUserName+"</font>";
		detailText.setText( Html.fromHtml( userNameText+" is now following "+targetUserNameText+"." ) );
	}

	public void setTargetUserName(String setTargetUserName) {
		targetUserNameText = "<font color='#63bfec'>"+setTargetUserName+"</font>";
		detailText.setText( Html.fromHtml( userNameText+" is now following "+targetUserNameText+"." ) );
	}

	public void setTargetActorData(ActorData setFollowedUser) {
		targetActorData = setFollowedUser;
	}
	
	public void setPostTime( String setPostTime ){
		postTime.setText( setPostTime );
	}
	
	public ActorData getTargetActorData() {
		return targetActorData;
	}
	
	public ImageView getUserImageView(){
		return userImageView;
	}

	public void setTargetUserNameMine() {
		targetUserNameText = "your";
		detailText.setText( Html.fromHtml( userNameText+" is now following "+targetUserNameText+"." ) );
	}

}
