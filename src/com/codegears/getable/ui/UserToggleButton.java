package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.widget.ToggleButton;

public class UserToggleButton extends ToggleButton {

	public UserToggleButton(Context context) {
		super(context);
	}
	
	public void setNormalText(){
		this.setTextSize( 11 );
		this.setTextColor( this.getResources().getColor( R.color.NameColorGrey ) );
	}
	
	public void setSelectedText(){
		this.setTextSize( 13 );
		this.setTextColor( this.getResources().getColor( R.color.NameColorPink ) );
	}

}
