package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityCommentsData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentRowLayout extends LinearLayout {
	
	private LinearLayout userNameLayout;
	private TextView commentText;
	private UserName userName;
	private ImageView userImageView;
	private ProductActivityCommentsData commentData;

	public CommentRowLayout(Context context) {
		super(context);
		View.inflate(context, R.layout.commentrowlayout, this);
		
		userNameLayout = (LinearLayout) findViewById( R.id.commentRowUserNameLayout );
		commentText = (TextView) findViewById( R.id.commentRowText );
		userImageView = (ImageView) findViewById( R.id.commentRowLayoutUserImage );
		userName = new UserName(context);
		userNameLayout.addView( userName );
		
		//Set font
		userName.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		commentText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}
	
	public void setUserName( String setString ){
		userName.setText( setString );
	}
	
	public void setCommentText( String setString ){
		commentText.setText( setString );
	}
	
	public UserName getUserNameTextView(){
		return userName;
	}

	public void setActivityData(ProductActivityCommentsData setCommentData) {
		commentData = setCommentData;
	}
	
	public ProductActivityCommentsData getActivityData(){
		return commentData;
	}

	public ImageView getUserImageView() {
		return userImageView;
	}

}
