package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.ProductBrandFeedData;

import android.content.Context;
import android.widget.ImageButton;

public class MyfeedWishlistButton extends ImageButton {
	
	private ProductActivityData feedData;
	private ProductBrandFeedData brandFeedData;
	
	public MyfeedWishlistButton(Context context) {
		super(context);
		this.setBackgroundResource( R.drawable.myfeed_wishlist_icon );
	}

	public void setActivityData(ProductActivityData setFeedData) {
		feedData = setFeedData;
	}
	
	public ProductActivityData getActivityData() {
		return feedData;
	}

	public void setBrandFeedData(ProductBrandFeedData setBrandFeedData) {
		brandFeedData = setBrandFeedData;
	}
	
	public ProductBrandFeedData getBrandFeedData() {
		return brandFeedData;
	}

}
