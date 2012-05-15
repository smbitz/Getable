package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ProductNumComment extends TextView {
	
	private ProductActivityData productActivityData;
	
	public ProductNumComment(Context context) {
		super(context);
	}
	
	public void setProductData( ProductActivityData setData ){
		productActivityData = setData;
	}
	
	public ProductActivityData getProductData(){
		return productActivityData;
	}

}
