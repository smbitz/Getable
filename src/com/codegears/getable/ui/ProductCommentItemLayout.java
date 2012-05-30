package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityCommentsData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductCommentItemLayout extends LinearLayout {
	
	public static final int DELETE_LAYOUT_INVISIBLE = 0;
	public static final int DELETE_LAYOUT_VISIBLE = 1;
	
	private ImageView userImage;
	private TextView userName;
	private TextView commentText;
	private TextView commentTime;
	private ProductActivityCommentsData productCommentActivityData;
	private LinearLayout deleteButtonLayout;
	private CommentDeleteButton deleteButton;
	
	public ProductCommentItemLayout(Context context) {
		super(context);
		View.inflate(context, R.layout.productcommentitemlayout, this);
		
		userImage = (ImageView) findViewById( R.id.productCommentItemUserImage );
		userName = (TextView) findViewById( R.id.productCommentItemUserName );
		commentText = (TextView) findViewById( R.id.productCommentItemCommentText );
		commentTime = (TextView) findViewById( R.id.productCommentItemCommentTime );
		deleteButtonLayout = (LinearLayout) findViewById( R.id.productCommentItemDeleteButtonLayout );
		deleteButton = new CommentDeleteButton( context );
		
		deleteButtonLayout.addView( deleteButton );
		
		userName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}
	
	public void setUserName( String setString ){
		userName.setText( setString );
	}
	
	public void setCommentText( String setString ){
		commentText.setText( setString );
	}
	
	public void setCommentTime( String setString ){
		commentTime.setText( setString );
	}
	
	public void setProductCommentActivityData( ProductActivityCommentsData setData ){
		productCommentActivityData = setData;
	}
	
	public ImageView getUserImageView(){
		return userImage;
	}
	
	public ProductActivityCommentsData getProductCommentActivityData(){
		return productCommentActivityData;
	}
	
	public CommentDeleteButton getDeleteButton(){
		return deleteButton;
	}
	
	public void setDeleteButtonLayout(int setValue) {
		if( setValue == DELETE_LAYOUT_INVISIBLE ){
			deleteButtonLayout.setVisibility( View.INVISIBLE );
		}else if( setValue == DELETE_LAYOUT_VISIBLE ){
			deleteButtonLayout.setVisibility( View.VISIBLE );
		}
	}
	
}
