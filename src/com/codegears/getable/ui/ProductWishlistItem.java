package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.WishlistData;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductWishlistItem extends LinearLayout {
	
	private TextView name;
	private TextView itemQty;
	private LinearLayout addRemoveButtonLayout;
	private WishlistAddRemoveButton addRemoveButton;
	
	public ProductWishlistItem(Context context) {
		super(context);
		View.inflate(context, R.layout.productwishlistitem, this);
		
		name = (TextView) findViewById( R.id.productWishlistItemWishlistName );
		itemQty = (TextView) findViewById( R.id.productWishlistItemWishlistQuantity );
		addRemoveButtonLayout = (LinearLayout) findViewById( R.id.productWishlistItemAddRemoveButtonLayout );
		
		//Set font
		name.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		itemQty.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		addRemoveButton = new WishlistAddRemoveButton( context );
		addRemoveButton.setGravity( Gravity.CENTER );
		
		addRemoveButtonLayout.addView( addRemoveButton );
	}
	
	public void setName( String setName ){
		name.setText( setName );
	}
	
	public void setItemQty( String setQty ){
		itemQty.setText( setQty );
	}
	
	public WishlistAddRemoveButton getAddRemoveButton(){
		return addRemoveButton;
	}
	
}
