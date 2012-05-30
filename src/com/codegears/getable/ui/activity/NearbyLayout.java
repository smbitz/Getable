package com.codegears.getable.ui.activity;

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
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.NearByFilterActivity;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.NearbyItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetCurrentLocation;
import com.codegears.getable.util.ImageLoader;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import android.view.View.OnClickListener;

public class NearbyLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {
	
	private BodyLayoutStackListener listener;
	private ListView nearByListView;
	private NearByAdapter nearByAdapter;
	private Config config;
	private GetCurrentLocation getCurrentLocation;
	private MyApp app;
	private List<String> appCookie;
	private ArrayList<ProductActivityData> arrayProductData;
	private ImageLoader imageLoader;
	private Button filterButton;
	
	public NearbyLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.nearbylayout, this );
		
		nearByListView = ( ListView ) findViewById( R.id.nearByLayoutListView );
		nearByAdapter = new NearByAdapter();
		config = new Config( this.getContext() );
		getCurrentLocation = new GetCurrentLocation( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		appCookie = app.getAppCookie();
		arrayProductData = new ArrayList<ProductActivityData>();
		imageLoader = new ImageLoader( this.getContext() );
		filterButton = (Button) findViewById( R.id.NearbyLayoutFilterButton );
		
		filterButton.setOnClickListener( this );
		
		String nearByURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities.json?";
		String url_var_1 = "currentCoordinate.latitude="+getCurrentLocation.getCurrentLat()+
			"&currentCoordinate.longitude="+getCurrentLocation.getCurrentLng();
		String url_var_2 = "&sort.properties[0].name=distance&sort.properties[0].reverse=false";
	
		NetworkThreadUtil.getRawDataWithCookie( nearByURL+url_var_1+url_var_2, null, appCookie, this );
	}

	@Override
	public void refreshView(Intent getData) {
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
			imageLoader.DisplayImage( setProductImageURL, NearbyLayout.this.getActivity(), returnView.getProductImageView(), true );
			
			returnView.setOnClickListener( NearbyLayout.this );
			
			return returnView;
		}
		
	}

	@Override
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
		
	}

	@Override
	public void onClick(View v) {
		if( v.equals( filterButton ) ){
			Intent newIntent = new Intent( this.getContext(), NearByFilterActivity.class );
			this.getActivity().startActivityForResult( newIntent, MainActivity.REQUEST_NEARBY_FILTER );
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
