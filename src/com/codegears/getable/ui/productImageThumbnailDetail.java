package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductImageThumbnailDetail extends LinearLayout {
	
	private ImageView productImage;
	private ProductActivityData productActivityData;
	private TextView productName;
	private TextView userName;

	public ProductImageThumbnailDetail(Context context) {
		super(context);
		View.inflate(context, R.layout.productimagethumbnaildetail, this);
		
		productImage = (ImageView) findViewById( R.id.productImageThumbnailDetailImage );
		productName = (TextView) findViewById( R.id.productImageThumbnailDetailName );
		userName = (TextView) findViewById( R.id.productImageThumbnailDetailUserName );
	}
	
	public void setProductImage( Bitmap setBitmap ){
		productImage.setImageBitmap( setBitmap );
	}
	
	public void setProductData( ProductActivityData setProductActivityData ){
		productActivityData = setProductActivityData;
	}
	
	public void setProductName( String setProductName ){
		productName.setText( setProductName );
	}
	
	public void setUserName( String setUserName ){
		userName.setText( setUserName );
	}
	
	public ProductActivityData getProductData(){
		return productActivityData;
	}
	
	public ImageView getProductImageView(){
		return productImage;
	}
}
