package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProductImageThumbnail extends LinearLayout {
	
	private ImageView productImage;
	private ProductActivityData productActivityData;

	public ProductImageThumbnail(Context context) {
		super(context);
		View.inflate(context, R.layout.productimagethumbnail, this);
		
		productImage = (ImageView) findViewById( R.id.productImageView );
	}
	
	public void setProductImage( Bitmap setBitmap ){
		productImage.setImageBitmap( setBitmap );
	}
	
	public void setProductImageDefault(){
		productImage.setImageResource( R.drawable.empty_image );
	}
	
	public void setProductData( ProductActivityData setProductActivityData ){
		productActivityData = setProductActivityData;
	}
	
	public ProductActivityData getProductData(){
		return productActivityData;
	}
	
	public ImageView getProductImageView(){
		return productImage;
	}
}
