package com.codegears.getable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProductPhotoOptions extends Activity implements OnClickListener {
	
	private Button closeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.productphotooptions );
		
		closeButton = (Button) findViewById( R.id.productDetailPhotoOptionsCloseButton );
		
		closeButton.setOnClickListener( this );
	}

	@Override
	public void onClick(View v) {
		if( v.equals( closeButton ) ){
			this.finish();
		}
	}
	
}
