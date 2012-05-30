package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NearbyItem extends LinearLayout {

	private ImageView productImage;
	private TextView userName;
	private TextView productName;
	private TextView productAddress;
	private TextView distanceValue;
	private ProductActivityData activityData;
	
	public NearbyItem(Context context) {
		super(context);
		View.inflate(context, R.layout.nearbyitem, this);
		
		productImage = (ImageView) findViewById( R.id.nearByItemProductImage );
		userName = (TextView) findViewById( R.id.nearByItemUserName );
		productName = (TextView) findViewById( R.id.nearByItemProductName );
		productAddress = (TextView) findViewById( R.id.nearByItemProductAddress );
		distanceValue = (TextView) findViewById( R.id.nearByItemMiDistance );
	}
	
	public ImageView getProductImageView(){
		return productImage;
	}
	
	public ProductActivityData getProductActivityData(){
		return activityData;
	}
	
	public void setUserName( String setValue ){
		userName.setText( setValue );
	}
	
	public void setProductName( String setValue ){
		productName.setText( setValue );
	}
	
	public void setProductAddress( String setValue ){
		productAddress.setText( setValue );
	}
	
	public void setProductDistance( String setValue ){
		distanceValue.setText( setValue );
	}
	
	public void setProductActivityData( ProductActivityData setData ){
		activityData = setData;
	}
	
}
