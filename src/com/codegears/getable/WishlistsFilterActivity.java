package com.codegears.getable;

import com.codegears.getable.util.GetCurrentLocation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WishlistsFilterActivity extends Activity implements OnClickListener {
	
	public static String PUT_EXTRA_URL_VAR_1 = "PUT_EXTRA_URL_VAR_1";
	public static String PUT_EXTRA_FILTER_TEXT = "PUT_EXTRA_FILTER_TEXT";
	
	private Button filterTitle;
	private Button filterPriceLowToHigh;
	private Button filterPriceHighToLow;
	private Button filterDate;
	private Button filterDistance;
	private String currentLat;
	private String currentLng;
	private GetCurrentLocation getCurrentLocation;
	private Button cancelButton;
	private TextView wishlistFilterText;
	private TextView filterTitleText;
	private TextView filterPriceLowToHighText;
	private TextView filterPriceHighToLowText;
	private TextView filterDateText;
	private TextView filterDistanceText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.wishlistsfilterlayout );
		
		filterTitle = (Button) findViewById( R.id.wishlistsFilterTitleAToZButton );
		filterPriceLowToHigh = (Button) findViewById( R.id.wishlistsFilterPriceLowToHighButton );
		filterPriceHighToLow = (Button) findViewById( R.id.wishlistsFilterPriceHighToLowButton );
		filterDate = (Button) findViewById( R.id.wishlistsFilterDataAddedButton );
		filterDistance = (Button) findViewById( R.id.wishlistsFilterDistanceButton );
		cancelButton = (Button) findViewById( R.id.wishlistsFilterCancelButton );
		wishlistFilterText = (TextView) findViewById( R.id.wishlistsFilterText );
		filterTitleText = (TextView) findViewById( R.id.wishlistsFilterTitleAToZButtonText );
		filterPriceLowToHighText = (TextView) findViewById( R.id.wishlistsFilterPriceLowToHighButtonText );
		filterPriceHighToLowText = (TextView) findViewById( R.id.wishlistsFilterPriceHighToLowButtonText );
		filterDateText = (TextView) findViewById( R.id.wishlistsFilterDataAddedButtonText );
		filterDistanceText = (TextView) findViewById( R.id.wishlistsFilterDistanceButtonText );
		
		//Set font
		wishlistFilterText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		filterTitleText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		filterPriceLowToHighText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		filterPriceHighToLowText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		filterDateText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		filterDistanceText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH ) );
		
		filterTitle.setOnClickListener( this );
		filterPriceLowToHigh.setOnClickListener( this );
		filterPriceHighToLow.setOnClickListener( this );
		filterDate.setOnClickListener( this );
		filterDistance.setOnClickListener( this );
		cancelButton.setOnClickListener( this );
		
		getCurrentLocation = new GetCurrentLocation( this );
		
		currentLat = getCurrentLocation.getCurrentLat();
	    currentLng = getCurrentLocation.getCurrentLng();
	}

	@Override
	public void onClick(View v) {
		if( v.equals( cancelButton ) ){
			Intent intent = new Intent();
			this.setResult( MainActivity.RESULT_WISHLISTS_FILTER_BACK, intent );
			this.finish();
		}else{
			Intent intent = new Intent();
			String urlVar = "";
			String filterText = "";
			
			if( v.equals( filterTitle ) ){
				urlVar = "&sort.properties[0].name=product.brand.name&sort.properties[0].reverse=false";
				filterText = "Title(A to Z)";
			}else if( v.equals( filterPriceLowToHigh ) ){
				urlVar = "&sort.properties[0].name=product.price&sort.properties[0].reverse=false";
				filterText = "Price(low to high)";
			}else if( v.equals( filterPriceHighToLow ) ){
				urlVar = "&sort.properties[0].name=product.price&sort.properties[0].reverse=true";
				filterText = "Price(high to low)";
			}else if( v.equals( filterDate ) ){
				urlVar = "&sort.properties[0].name=addedTime&sort.properties[0].reverse=false";
				filterText = "Date added";
			}else if( v.equals( filterDistance ) ){
				urlVar = "&sort.properties[0].name=product.brand.name&sort.properties[0].reverse=false&currentCoordinate.latitude="+currentLat+"&currentCoordinate.longitude="+currentLng;
				filterText = "Distance";
			}
			
			intent.putExtra( PUT_EXTRA_URL_VAR_1, urlVar );
			intent.putExtra( PUT_EXTRA_FILTER_TEXT, filterText );
			this.setResult( MainActivity.RESULT_WISHLISTS_FILTER_FINISH, intent );
			this.finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			this.setResult( MainActivity.RESULT_WISHLISTS_FILTER_BACK, intent );
			this.finish();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
