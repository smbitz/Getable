package com.codegears.getable.ui;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ProductDetailLayout extends LinearLayout implements OnClickListener {

	public static final int LAYOUTCHANGE_USERPROFILE = 2;
	
	private Button viewProfileButton;
	private BodyLayoutStackListener listener;
	
	public ProductDetailLayout( Context context ) {
		super( context );
		View.inflate( context, R.layout.productdetaillayout, this );
		viewProfileButton = (Button)this.findViewById( R.id.ViewProfileButton );
		viewProfileButton.setOnClickListener( this );
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onClick( View view ) {
		if(viewProfileButton.equals( view )){
			if(listener != null){
				listener.onRequestBodyLayoutStack( LAYOUTCHANGE_USERPROFILE );
			}
		}
	}
}
