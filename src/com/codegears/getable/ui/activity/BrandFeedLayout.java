package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.ProductBrandFeedData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.BrandFeedItem;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import android.view.View.OnClickListener;

public class BrandFeedLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {
	
	public static final String SHARE_PREF_VALUE_BRAND_ID = "SHARE_PREF_VALUE_BRAND_ID";
	public static final String SHARE_PREF_KEY_BRAND_ID = "SHARE_PREF_KEY_BRAND_ID";
	public static final String URL_DEFAULT = "URL_DEFAULT";

	private BodyLayoutStackListener listener;
	private String brandId;
	private ListView brandListView;
	private BrandAdapter brandAdapter;
	private ArrayList<ProductBrandFeedData> arrayBrandFeedData;
	private ArrayList<Bitmap> arrayUserImage;
	private ArrayList<Bitmap> arrayProductImage;
	private Config config;
	private ProgressDialog loadingDialog;
	
	public BrandFeedLayout(Activity activity) {
		super(activity);
		
		loadingDialog = ProgressDialog.show(this.getActivity(), "", 
	               "Loading. Please wait...", true);
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_BRAND_ID, this.getActivity().MODE_PRIVATE );
		brandId = myPrefs.getString( SHARE_PREF_KEY_BRAND_ID, null );
		
		brandListView = new ListView( this.getContext() );
		brandAdapter = new BrandAdapter();
		arrayBrandFeedData = new ArrayList<ProductBrandFeedData>();
		arrayUserImage = new ArrayList<Bitmap>();
		arrayProductImage = new ArrayList<Bitmap>();
		config = new Config( this.getContext() );
		
		this.addView( brandListView );
		
		String getBrandFeedData = config.get( MyApp.URL_DEFAULT ).toString()+"brands/"+brandId+"/activities.json";
		
		NetworkThreadUtil.getRawData( getBrandFeedData, null, this );
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	private class BrandAdapter extends BaseAdapter {
		
		ArrayList<ProductBrandFeedData> data;
		
		public void setData(ArrayList<ProductBrandFeedData> arrayBrandFeedData) {
			data = arrayBrandFeedData;
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
			
			BrandFeedItem brandFeedItem;
			
			if( convertView == null ){
				brandFeedItem = new BrandFeedItem( BrandFeedLayout.this.getContext() );
			}else{
				brandFeedItem = (BrandFeedItem) convertView;
			}
			
			String setUserName = data.get( position ).getActor().getName();
			String setProductBrandName = data.get( position ).getProduct().getBrand().getName();
			String setNumberLikes = String.valueOf( data.get( position ).getStatisitc().getNumberOfLikes() );
			String setNumberComments = String.valueOf( data.get( position ).getStatisitc().getNumberOfComments() );

			brandFeedItem.setUserName( setUserName );
			brandFeedItem.setProductBrandName( setProductBrandName );
			brandFeedItem.setUserImage( arrayUserImage.get( position ) );
			brandFeedItem.setProductImage( arrayProductImage.get( position ) );
			brandFeedItem.setNumberLikes( setNumberLikes );
			brandFeedItem.setNumberComments( setNumberComments );
			brandFeedItem.setProductData( data.get( position ) );
			brandFeedItem.setOnClickListener( BrandFeedLayout.this );
			
			return brandFeedItem;
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
				ProductBrandFeedData newData = new ProductBrandFeedData( (JSONObject) newArray.get(i) );
				
				//Load Product and user Image
				URL productPictureURL = null;
				URL userPictureURL = null;
				Bitmap imageProductBitmap = null;
				Bitmap imageUserBitmap = null;
				try {
					productPictureURL = new URL( newData.getProduct().getProductPicture().getImageUrls().getImageURLT() );
					userPictureURL = new URL( newData.getActor().getPicture().getImageUrls().getImageURLT() );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				try {
					imageProductBitmap = BitmapFactory.decodeStream( productPictureURL.openConnection().getInputStream() );
					imageUserBitmap = BitmapFactory.decodeStream( userPictureURL.openConnection().getInputStream() );
				} catch (IOException e) {
					e.printStackTrace();
				}

				arrayProductImage.add( imageProductBitmap );
				arrayUserImage.add( imageUserBitmap );
				arrayBrandFeedData.add( newData );
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		brandAdapter.setData( arrayBrandFeedData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				brandListView.setAdapter( brandAdapter );
			}
		});
		
		loadingDialog.dismiss();
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if(listener != null){
			if( v instanceof BrandFeedItem ){
				BrandFeedItem brandFeedItem = (BrandFeedItem) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, brandFeedItem.getProductData().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
		}
	}

}
