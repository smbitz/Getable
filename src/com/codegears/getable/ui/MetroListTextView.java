package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.MetroData;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MetroListTextView extends LinearLayout {
	
	private TextView metroText;
	private MetroData metroData;
	
	public MetroListTextView(Context context) {
		super(context);
		View.inflate(context, R.layout.metrolisttextview, this);
		
		metroText = (TextView) findViewById( R.id.metroListViewText );
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
