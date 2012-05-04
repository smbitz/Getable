package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.WishlistData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserWishlistsGrouptItem extends LinearLayout {
	
	private ImageView wishlistsImage;
	private TextView wishlistsName;
	private TextView wishlistsItemNumber;
	private WishlistData wishlistData;
	
	public UserWishlistsGrouptItem(Context context) {
		super(context);
		View.inflate(context, R.layout.userwishlistsgroupitem, this);
		
		wishlistsImage = (ImageView) findViewById( R.id.userWishlistsImage );
		wishlistsName = (TextView) findViewById( R.id.userWishlistsName );
		wishlistsItemNumber = (TextView) findViewById( R.id.userWishlistsItemNumber );
	}
	
	public void setWishlistsImage( Bitmap setBitmap ){
		wishlistsImage.setImageBitmap( setBitmap );
	}
	
	public void setWishlistsName( String setName ){
		wishlistsName.setText( setName );
	}
	
	public void setWishlistsItemNumber( String setItemNumber ){
		wishlistsItemNumber.setText( setItemNumber );
	}
	
	public void setWishlistData( WishlistData setWishlistData ){
		wishlistData = setWishlistData;
	}
	
	public WishlistData getWishlistData(){
		return wishlistData;
	}
	
}
