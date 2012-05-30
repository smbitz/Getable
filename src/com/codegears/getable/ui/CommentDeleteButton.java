package com.codegears.getable.ui;

import com.codegears.getable.data.ProductActivityCommentsData;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.widget.Button;

public class CommentDeleteButton extends Button {
	
	private ProductActivityCommentsData commentActivityData;
	
	public CommentDeleteButton(Context context) {
		super(context);
		this.setText( "Delete" );
	}
	
	public void setActivityCommentsData( ProductActivityCommentsData setCommentActivityData ){
		commentActivityData = setCommentActivityData;
	}
	
	public ProductActivityCommentsData getActivityCommentsData(){
		return commentActivityData;
	}
	
}
