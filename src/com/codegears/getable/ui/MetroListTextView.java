package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.MetroData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MetroListTextView extends LinearLayout {
	
	private TextView metroText;
	private MetroData metroData;
	private LinearLayout viewLayout;
	
	public MetroListTextView(Context context) {
		super(context);
		View.inflate(context, R.layout.metrolisttextview, this);

		viewLayout = (LinearLayout) findViewById( R.id.metroListLayout );
		
		metroText = (TextView) findViewById( R.id.metroListViewText );
		
		metroText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
	}
	
	public void setItemNormal(){
		viewLayout.setBackgroundColor( this.getResources().getColor( R.color.BGColorGrey ) );
		metroText.setTextColor( R.color.NameColorGrey );
	}
	
	public void setItemSelected(){
		viewLayout.setBackgroundColor( this.getResources().getColor( R.color.NameColorPink ) );
		metroText.setTextColor( Color.WHITE );
	}
	
	public void setText(String name) {
		// TODO Auto-generated method stub
		metroText.setText( name );
	}
	
	public void setData(MetroData setData){
		metroData = setData;
	}
	
	public MetroData getData(){
		return metroData;
	}

}
