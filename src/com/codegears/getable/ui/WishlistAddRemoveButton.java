package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.WishlistData;

import android.content.Context;
import android.widget.Button;

public class WishlistAddRemoveButton extends Button {
	
	public static final int BUTTON_STATE_ADD = 0;
	public static final int BUTTON_STATE_REMOVE = 1;
	
	private int buttonState;
	private WishlistData data;
	
	public WishlistAddRemoveButton(Context context) {
		super(context);
	}
	
	public void setButtonState( int buttonStateValue ){
		buttonState = buttonStateValue;
	}
	
	public void setWishlistData( WishlistData setData ){
		data = setData;
	}
	
	public void setImageButton( int resid ){
		this.setBackgroundResource( resid );
	}
	
	public WishlistData getWishlistData(){
		return data;
	}
	
	public int getButtonState(){
		return buttonState;
	}

}
