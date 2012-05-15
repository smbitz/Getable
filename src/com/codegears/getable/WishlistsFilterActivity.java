package com.codegears.getable;

import com.codegears.getable.util.GetCurrentLocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WishlistsFilterActivity extends Activity implements OnClickListener {
	
	public static String PUT_EXTRA_URL_VAR_1 = "PUT_EXTRA_URL_VAR_1";
	
	private Button filterTitle;
	private Button filterPriceLowToHigh;
	private Button filterPriceHighToLow;
	private Button filterDate;
	private Button filterDistance;
	private String currentLat;
	private String currentLng;
	private GetCurrentLocation getCurrentLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.wishlistsfilterlayout );
		
		filterTitle = (Button) findViewById( R.id.wishlistsFilterTitleAToZButton );
		filterPriceLowToHigh = (Button) findViewById( R.id.wishlistsFilterPriceLowToHighButton );
		filterPriceHighToLow = (Button) findViewById( R.id.wishlistsFilterPriceHighToLowButton );
		filterDate = (Button) findViewById( R.id.wishlistsFilterDataAddedButton );
		filterDistance = (Button) findViewById( R.id.wishlistsFilterDistanceButton );
		
		filterTitle.setOnClickListener( this );
		filterPriceLowToHigh.setOnClickListener( this );
		filterPriceHighToLow.setOnClickListener( this );
		filterDate.setOnClickListener( this );
		filterDistance.setOnClickListener( this );
		
		getCurrentLocation = new GetCurrentLocation( this );
		
		currentLat = getCurrentLocation.getCurrentLat();
	    currentLng = getCurrentLocation.getCurrentLng();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		String urlVar = "";
		
		if( v.equals( filterTitle ) ){
			urlVar = "&sort.properties[0].name=product.brand.name&sort.properties[0].reverse=false"; 
		}else if( v.equals( filterPriceLowToHigh ) ){
			urlVar = "&sort.properties[0].name=product.price&sort.properties[0].reverse=false"; 
		}else if( v.equals( filterPriceHighToLow ) ){
			urlVar = "&sort.properties[0].name=product.price&sort.properties[0].reverse=true"; 
		}else if( v.equals( filterDate ) ){
			urlVar = "&sort.properties[0].name=addedTime&sort.properties[0].reverse=false"; 
		}else if( v.equals( filterDistance ) ){
			urlVar = "&sort.properties[0].name=product.brand.name&sort.properties[0].reverse=false&currentCoordinate.latitude="+currentLat+"&currentCoordinate.longitude="+currentLng; 
		}
		
		intent.putExtra( PUT_EXTRA_URL_VAR_1, urlVar );
		this.setResult( MainActivity.RESULT_WISHLISTS_FILTER_FINISH, intent );
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			String urlVar = "&sort.properties[0].name=product.brand.name&sort.properties[0].reverse=false";
			intent.putExtra( PUT_EXTRA_URL_VAR_1, urlVar );
			this.setResult( MainActivity.RESULT_WISHLISTS_FILTER_FINISH, intent );
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
