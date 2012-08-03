package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NearbyFilterSelectedButton extends LinearLayout {
	
	private TextView filterText;
	
	public NearbyFilterSelectedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.nearbyfilterselectedbutton, this);
		
		filterText = (TextView) findViewById( R.id.nearbyFilterSelectedButtonText );
		
		//Set font
		filterText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
	}
	
	public void setFilterButtonText( String setText ){
		filterText.setText( setText );
	}
	
	public String getFilterButtonText(){
		return filterText.getText().toString();
	}

}
