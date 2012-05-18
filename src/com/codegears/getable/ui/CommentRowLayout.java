package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentRowLayout extends LinearLayout {
	
	private LinearLayout userNameLayout;
	private TextView commentText;
	private UserName userName;

	public CommentRowLayout(Context context) {
		super(context);
		View.inflate(context, R.layout.commentrowlayout, this);
		
		userNameLayout = (LinearLayout) findViewById( R.id.commentRowUserNameLayout );
		commentText = (TextView) findViewById( R.id.commentRowText );
		userName = new UserName(context);
		userNameLayout.addView( userName );
		
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

}
