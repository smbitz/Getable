package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductBrandFeedData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BrandFeedItem extends LinearLayout {
	
	private ImageView userImage;
	private String userNameText;
	private ImageView productImage;
	private TextView productBrandName;
	private TextView time;
	private TextView numberLikes;
	private TextView numberComments;
	private ProductBrandFeedData productData;
	private LinearLayout likeButtonLayout;
	private LinearLayout commentButtonLayout;
	private LinearLayout wishlistButtonLayout;
	private MyFeedLikeButton likeButton;
	private MyFeedCommentButton commentButton;
	private MyfeedWishlistButton wishlistButton;
	private TextView detailText;
	
	public BrandFeedItem(Context context) {
		super(context);
		View.inflate(context, R.layout.brandfeeditem, this);

		userImage = (ImageView) findViewById( R.id.brandFeedItemUserImage );
		productImage = (ImageView) findViewById( R.id.brandFeedItemImage );
		productBrandName = (TextView) findViewById( R.id.brandFeedItemBrandName );
		time = (TextView) findViewById( R.id.brandFeedItemTime );
		numberLikes = (TextView) findViewById( R.id.brandFeedItemNumberLikes );
		numberComments = (TextView) findViewById( R.id.brandFeedItemNumberComments );
		likeButtonLayout = (LinearLayout) findViewById( R.id.brandFeedLikeImageLayout );
		commentButtonLayout = (LinearLayout) findViewById( R.id.brandFeedCommentImageLayout );
		wishlistButtonLayout = (LinearLayout) findViewById( R.id.brandFeedWishlistImageLayout );
		detailText = (TextView) findViewById( R.id.brandFeedItemDetailText );
		
		likeButton = new MyFeedLikeButton( this.getContext() );
		commentButton = new MyFeedCommentButton( this.getContext() );
		wishlistButton = new MyfeedWishlistButton( this.getContext() );
		
		likeButtonLayout.addView( likeButton );
		commentButtonLayout.addView( commentButton );
		wishlistButtonLayout.addView( wishlistButton );
		
		//Set font
		productBrandName.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		time.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		numberLikes.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		numberComments.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		detailText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		productBrandName.setTextColor( Color.parseColor( this.getResources().getString( R.color.NameColorBlue ) ) );
		
		detailText.setText( Html.fromHtml( userNameText+" added a new product." ) );
	}
	
	public void setUserImage( Bitmap setBitmap ){
		userImage.setImageBitmap( setBitmap );
	}
	
	public void setUserName( String setUserName ){
		userNameText = "<font color='#63bfec'>"+setUserName+"</font>";
		detailText.setText( Html.fromHtml( userNameText+" added a new product." ) );
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
	
	public void setPostTime( String setPostTime ){
		time.setText( setPostTime );
	}
	
	public ProductBrandFeedData getProductData(){
		return productData;
	}
	
	public MyFeedLikeButton getLikeButton(){
		return likeButton;
	}
	
	public MyFeedCommentButton getCommentButton(){
		return commentButton;
	}
	
	public MyfeedWishlistButton getWishlistButton(){
		return wishlistButton;
	}
	
	public int getNumbersLikes() {
		return Integer.parseInt( numberLikes.getText().toString() );
	}
	
}
