package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.ToggleButton;

public class UserToggleButton extends ToggleButton {

	public UserToggleButton(Context context) {
		super(context);
		this.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
	}
	
	public void setNormalText(){
		this.setTextSize( 12 );
		this.setTextColor( this.getResources().getColor( R.color.NameColorGrey ) );
	}
	
	public void setSelectedText(){
		this.setTextSize( 14 );
		this.setTextColor( this.getResources().getColor( R.color.NameColorPink ) );
	}

}
