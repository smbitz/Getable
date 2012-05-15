package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ProductNumLike extends TextView {
	
	private ProductActivityData productActivityData;
	
	public ProductNumLike(Context context) {
		super(context);
	}
	
	public void setProductData( ProductActivityData setData ){
		productActivityData = setData;
	}
	
	public ProductActivityData getProductData(){
		return productActivityData;
	}

}