package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class ProductNumLike extends TextView {
	
	private ProductActivityData productActivityData;
	
	public ProductNumLike(Context context) {
		super(context);
		this.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		this.setTextSize( 12 );
	}
	
	public void setProductData( ProductActivityData setData ){
		productActivityData = setData;
	}
	
	public ProductActivityData getProductData(){
		return productActivityData;
	}

}
