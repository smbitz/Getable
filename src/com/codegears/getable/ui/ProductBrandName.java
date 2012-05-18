package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.BrandData;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class ProductBrandName extends TextView {
	
	private BrandData brandData;
	
	public ProductBrandName(Context context) {
		super(context);
		this.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
	}
	
	public void setBrandData( BrandData setData ){
		brandData = setData;
	}
	
	public BrandData getBrandData(){
		return brandData;
	}

}
