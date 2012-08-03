package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.ProductBrandFeedData;

import android.content.Context;
import android.widget.ImageButton;

public class MyFeedCommentButton extends ImageButton {
	
	private ProductActivityData productActivityData;
	private ProductBrandFeedData brandFeedData;
	
	public MyFeedCommentButton(Context context) {
		super(context);
		this.setBackgroundResource( R.drawable.myfeed_comment_icon );
	}

	public void setProductData( ProductActivityData setData ){
		productActivityData = setData;
	}
	
	public ProductActivityData getProductData(){
		return productActivityData;
	}

	public void setBrandFeedData(ProductBrandFeedData setBrandFeedData) {
		brandFeedData = setBrandFeedData;
	}
	
	public ProductBrandFeedData getBrandFeedData() {
		return brandFeedData;
	}

}
