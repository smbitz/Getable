package com.codegears.getable.ui.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.GalleryFilterActivity;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.NearByFilterActivity;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.NearbyFilterSelectedButton;
import com.codegears.getable.ui.NearbyItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetCurrentLocation;
import com.codegears.getable.util.ImageLoader;
import com.codegears.getable.util.RoundScaleNumber;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.view.View.OnClickListener;

public class NearbyLayout extends AbstractViewLayout implements OnClickListener {
	
	private static double METER_TO_MILE_VALUE = 0.000621371192;
	
	private BodyLayoutStackListener listener;
	private ListView nearByListView;
	private NearByAdapter nearByAdapter;
	private Config config;
	private GetCurrentLocation getCurrentLocation;
	private MyApp app;
	//private List<String> appCookie;
	private ArrayList<ProductActivityData> arrayProductData;
	private ImageLoader imageLoader;
	private Button filterButton;
	private String currentLat;
	private String currentLng;
	private RoundScaleNumber roundScaleNumber;
	private String nearByURL;
	private String url_var_1;
	private String url_var_2;
	private String url_var_3;
	private LinearLayout filterSelectedLayout;
	private NearbyFilterSelectedButton filterSelectedButton1;
	private NearbyFilterSelectedButton filterSelectedButton2;
	private NearbyFilterSelectedButton filterSelectedButton3;
	private AsyncHttpClient asyncHttpClient;
	
	public NearbyLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.nearbylayout, this );
		
		nearByListView = ( ListView ) findViewById( R.id.nearByLayoutListView );
		nearByAdapter = new NearByAdapter();
		config = new Config( this.getContext() );
		getCurrentLocation = new GetCurrentLocation( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		arrayProductData = new ArrayList<ProductActivityData>();
		imageLoader = new ImageLoader( this.getContext() );
		filterButton = (Button) findViewById( R.id.nearbyLayoutFilterButton );
		roundScaleNumber = new RoundScaleNumber();
		filterSelectedLayout = (LinearLayout) findViewById( R.id.nearbyLayoutFilterSelectedLayout );
		filterSelectedButton1 = (NearbyFilterSelectedButton) findViewById( R.id.nearbyLayoutFilterSelectedButton1 );
		filterSelectedButton2 = (NearbyFilterSelectedButton) findViewById( R.id.nearbyLayoutFilterSelectedButton2 );
		filterSelectedButton3 = (NearbyFilterSelectedButton) findViewById( R.id.nearbyLayoutFilterSelectedButton3 );
		
		filterButton.setOnClickListener( this );
		filterSelectedButton1.setOnClickListener( this );
		filterSelectedButton2.setOnClickListener( this );
		filterSelectedButton3.setOnClickListener( this );
		
		currentLat = getCurrentLocation.getCurrentLat();
		currentLng = getCurrentLocation.getCurrentLng();
		
		nearByURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities.json?page.number=1&page.size=20&sort.properties[0].name=distance&sort.properties[0].reverse=false&sort.properties[1].name=statistic.score.active&sort.properties[1].reverse=false&sort.properties[2].name=statistic.score.allTime&sort.properties[2].reverse=false&";
		String url_default_var_1 = "currentCoordinate.latitude="+currentLat+
			"&currentCoordinate.longitude="+currentLng;
		//String url_default_var_2 = "&sort.properties[0].name=distance&sort.properties[0].reverse=false";
		String url_default_var_2 = "";
		
		nearByURL = nearByURL+url_default_var_1+url_default_var_2;
		
		recycleResource();
		loadData();
	}
	
	private void recycleResource() {
		arrayProductData.clear();
	}
	
	public void loadData(){
		recycleResource();
		
		if( url_var_1 == null ){
			url_var_1 = "";
		}
		if( url_var_2 == null ){
			url_var_2 = "";
		}
		if( url_var_3 == null ){
			url_var_3 = "";
		}
		
		String url = nearByURL+url_var_1+url_var_2+url_var_3;
		url = url.replace( " ", "%20" );
		
		//NetworkThreadUtil.getRawData( url, null, this);
		asyncHttpClient.get( url, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				try {
					JSONArray newArray = jsonObject.getJSONArray("entities");
					for(int i = 0; i<newArray.length(); i++){
						//Load Product Data
						ProductActivityData newData = new ProductActivityData( (JSONObject) newArray.get(i) );
						arrayProductData.add( newData );
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				nearByAdapter.setData( arrayProductData );
				nearByListView.setAdapter( nearByAdapter );
			}
		});
	}
	
	@Override
	public void refreshView(Intent getData) {
		final String var1 = getData.getExtras().getString( NearByFilterActivity.PUT_EXTRA_URL_VAR_1 );
		final String var2 = getData.getExtras().getString( NearByFilterActivity.PUT_EXTRA_URL_VAR_2 );
		final String var3 = getData.getExtras().getString( NearByFilterActivity.PUT_EXTRA_URL_VAR_3 );
		final String var4 = getData.getExtras().getString( NearByFilterActivity.PUT_EXTRA_URL_VAR_4 );
		
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				//Set filter selected button text.
				if( !(var1.equals("")) && var1 != null ){
					filterSelectedButton1.setFilterButtonText( var1 );
					filterSelectedButton1.setVisibility( View.VISIBLE );
					url_var_1 = "&q="+var1;
				}else{
					url_var_1 = "";
					filterSelectedButton1.setVisibility( View.GONE );
				}
				
				if( !(var2.equals("")) && var2 != null ){
					filterSelectedButton2.setFilterButtonText( var2+" mi" );
					filterSelectedButton2.setVisibility( View.VISIBLE );
					url_var_2 = "&radius="+var2;
				}else{
					url_var_2 = "";
					filterSelectedButton2.setVisibility( View.GONE );
				}
				
				if( ( !(var3.equals("")) && var3 != null ) && 
					( (var4.equals("")) || var4.equals( String.valueOf( NearByFilterActivity.PRICE_MAXIMUM ) ) || var4 == null ) ){
					filterSelectedButton3.setFilterButtonText( "$"+var3+"-"+NearByFilterActivity.PRICE_MAXIMUM_TEXT );
					filterSelectedButton3.setVisibility( View.VISIBLE );
					url_var_3 = "&priceRange.minimum="+var3;
				}else if( ( (var3.equals("")) || var3.equals( String.valueOf( NearByFilterActivity.PRICE_MINIMUM ) ) || var3 == null ) && 
					      ( !(var4.equals("")) && var4 != null ) ){
					filterSelectedButton3.setFilterButtonText( "$0-"+"$"+var4 );
					filterSelectedButton3.setVisibility( View.VISIBLE );
					url_var_3 = "&priceRange.minimum="+var3+"&priceRange.maximum="+var4;
				}else if( ( !(var3.equals("")) && var3 != null ) && 
					( !(var4.equals("")) && var4 != null ) ){
					filterSelectedButton3.setFilterButtonText( "$"+var3+"-"+"$"+var4 );
					filterSelectedButton3.setVisibility( View.VISIBLE );
					url_var_3 = "&priceRange.minimum="+var3+"&priceRange.maximum="+var4;
				}else{
					url_var_3 = "";
					filterSelectedButton3.setVisibility( View.GONE );
				}
				
				if( (!(filterSelectedButton1.getFilterButtonText().equals("")) && filterSelectedButton1.getFilterButtonText() != null) ||
					(!(filterSelectedButton2.getFilterButtonText().equals("")) && filterSelectedButton2.getFilterButtonText() != null) ||
					(!(filterSelectedButton3.getFilterButtonText().equals("")) && filterSelectedButton3.getFilterButtonText() != null) ){
					filterSelectedLayout.setVisibility( View.VISIBLE );
				}else{
					filterSelectedLayout.setVisibility( View.GONE );
				}
			}
		});
		
		loadData();
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}
	
	private class NearByAdapter extends BaseAdapter {
		
		private ArrayList<ProductActivityData> data;
		
		public void setData(ArrayList<ProductActivityData> arrayProductData) {
			data = arrayProductData;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get( position );
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			
			NearbyItem returnView;
			
			if( convertView == null ){
				returnView = new NearbyItem( NearbyLayout.this.getContext() );
			}else{
				returnView = (NearbyItem) convertView;
			}
			
			String setUserName = data.get( position ).getActor().getName();
			String setProductName = data.get( position ).getProduct().getBrand().getName();
			String setProductAddress = data.get( position ).getProduct().getStore().getName()+", "+
								       data.get( position ).getProduct().getStore().getStreetAddress()+" "+
								       data.get( position ).getProduct().getStore().getPostalCode();
			String setProductImageURL = data.get( position ).getProduct().getProductPicture().getImageUrls().getImageURLT();
			
			returnView.setUserName( setUserName );
			returnView.setProductName( setProductName );
			returnView.setProductAddress( setProductAddress );
			returnView.setProductActivityData( data.get( position ) );
			
			//Set Mile Value.
			float storeLat = Float.valueOf( data.get( position ).getProduct().getStore().getCoondinate().getLat() );
			float storeLng = Float.valueOf( data.get( position ).getProduct().getStore().getCoondinate().getLng() );
			float distance = getCurrentLocation.gps2m( Float.valueOf( currentLat ), Float.valueOf( currentLng ), storeLat, storeLng);
		    String distanceText = String.valueOf( roundScaleNumber.round( (distance*METER_TO_MILE_VALUE), 1, BigDecimal.ROUND_HALF_UP ) );
			returnView.setMiText( distanceText );
				
			imageLoader.DisplayImage( setProductImageURL, NearbyLayout.this.getActivity(), returnView.getProductImageView(), true, asyncHttpClient );
			
			returnView.setOnClickListener( NearbyLayout.this );
			
			return returnView;
		}
		
	}

	/*@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray newArray = jsonObject.getJSONArray("entities");
			for(int i = 0; i<newArray.length(); i++){
				//Load Product Data
				ProductActivityData newData = new ProductActivityData( (JSONObject) newArray.get(i) );
				arrayProductData.add( newData );
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		nearByAdapter.setData( arrayProductData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				nearByListView.setAdapter( nearByAdapter );
			}
		});
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
	}*/

	@Override
	public void onClick(View v) {
		if( v.equals( filterButton ) ){
			Intent newIntent = new Intent( this.getContext(), NearByFilterActivity.class );
			this.getActivity().startActivityForResult( newIntent, MainActivity.REQUEST_NEARBY_FILTER );
		}else if( v.equals( filterSelectedButton1 ) ){
			url_var_1 = "";
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					filterSelectedButton1.setFilterButtonText("");
					filterSelectedButton1.setVisibility( View.GONE );
					
					if( (!(filterSelectedButton1.getFilterButtonText().equals("")) && filterSelectedButton1.getFilterButtonText() != null) ||
						(!(filterSelectedButton2.getFilterButtonText().equals("")) && filterSelectedButton2.getFilterButtonText() != null) ||
						(!(filterSelectedButton3.getFilterButtonText().equals("")) && filterSelectedButton3.getFilterButtonText() != null) ){
						filterSelectedLayout.setVisibility( View.VISIBLE );
					}else{
						filterSelectedLayout.setVisibility( View.GONE );
					}
				}
			});
			loadData();
		}else if( v.equals( filterSelectedButton2 ) ){
			url_var_2 = "";
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					filterSelectedButton2.setFilterButtonText("");
					filterSelectedButton2.setVisibility( View.GONE );
					
					if( (!(filterSelectedButton1.getFilterButtonText().equals("")) && filterSelectedButton1.getFilterButtonText() != null) ||
						(!(filterSelectedButton2.getFilterButtonText().equals("")) && filterSelectedButton2.getFilterButtonText() != null) ||
						(!(filterSelectedButton3.getFilterButtonText().equals("")) && filterSelectedButton3.getFilterButtonText() != null) ){
						filterSelectedLayout.setVisibility( View.VISIBLE );
					}else{
						filterSelectedLayout.setVisibility( View.GONE );
					}
				}
			});
			loadData();
		}else if( v.equals( filterSelectedButton3 ) ){
			url_var_3 = "";
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					filterSelectedButton3.setFilterButtonText("");
					filterSelectedButton3.setVisibility( View.GONE );
					
					if( (!(filterSelectedButton1.getFilterButtonText().equals("")) && filterSelectedButton1.getFilterButtonText() != null) ||
						(!(filterSelectedButton2.getFilterButtonText().equals("")) && filterSelectedButton2.getFilterButtonText() != null) ||
						(!(filterSelectedButton3.getFilterButtonText().equals("")) && filterSelectedButton3.getFilterButtonText() != null) ){
						filterSelectedLayout.setVisibility( View.VISIBLE );
					}else{
						filterSelectedLayout.setVisibility( View.GONE );
					}
				}
			});
			loadData();
		}else{
			if(listener != null){
				if( v instanceof NearbyItem ){
					NearbyItem nearbyItem = (NearbyItem) v;
					SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
					SharedPreferences.Editor prefsEditor = myPref.edit();
					prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, nearbyItem.getProductActivityData().getId() );
			        prefsEditor.commit();
			        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
				}
			}
		}
	}
	
}
