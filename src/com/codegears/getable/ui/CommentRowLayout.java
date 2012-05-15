package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentRowLayout extends LinearLayout {
	
	private TextView userName;
	private TextView commentText;

	public CommentRowLayout(Context context) {
		super(context);
		View.inflate(context, R.layout.commentrowlayout, this);
		
		userName = (TextView) findViewById( R.id.commentRowUserName );
		commentText = (TextView) findViewById( R.id.commentRowText );
	}
	
	public void setUserName( String setString ){
		userName.setText( setString );
	}
	
	public void setCommentText( String setString ){
		commentText.setText( setString );
	}

}
