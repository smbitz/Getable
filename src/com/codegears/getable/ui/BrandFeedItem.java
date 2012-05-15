package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductBrandFeedData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BrandFeedItem extends LinearLayout {
	
	private ImageView userImage;
	private TextView userName;
	private ImageView productImage;
	private TextView productBrandName;
	private TextView time;
	private TextView numberLikes;
	private TextView numberComments;
	private TextView numberWishlist;
	private ProductBrandFeedData productData;
	
	public BrandFeedItem(Context context) {
		super(context);
		View.inflate(context, R.layout.brandfeeditem, this);

		userImage = (ImageView) findViewById( R.id.brandFeedItemUserImage );
		userName = (TextView) findViewById( R.id.brandFeedItemUserName );
		productImage = (ImageView) findViewById( R.id.brandFeedItemImage );
		productBrandName = (TextView) findViewById( R.id.brandFeedItemBrandName );
		time = (TextView) findViewById( R.id.brandFeedItemTime );
		numberLikes = (TextView) findViewById( R.id.brandFeedItemNumberLikes );
		numberComments = (TextView) findViewById( R.id.brandFeedItemNumberComments );
		numberWishlist = (TextView) findViewById( R.id.brandFeedItemNumberWishlists );
	}
	
	public void setUserImage( Bitmap setBitmap ){
		userImage.setImageBitmap( setBitmap );
	}
	
	public void setUserName( String setString ){
		userName.setText( setString );
	}
	
	public void setProductImage( Bitmap setBitmap ){
		productImage.setImageBitmap( setBitmap );
	}
	
	public void setProductBrandName( String setString ){
		productBrandName.setText( setString );
	}
	
	public void setNumberLikes( String setString ){
		numberLikes.setText( setString );
	}
	
	public void setNumberComments( String setString ){
		numberComments.setText( setString );
	}
	
	public void setProductData( ProductBrandFeedData setData ){
		productData = setData;
	}
	
	public ProductBrandFeedData getProductData(){
		return productData;
	}
	
}
