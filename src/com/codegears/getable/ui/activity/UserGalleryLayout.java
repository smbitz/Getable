package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.ProductActorLikeData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.ProductImageThumbnail;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;

public class UserGalleryLayout extends AbstractViewLayout implements OnItemClickListener {

	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String USER_GALLERY_VIEW_TYPE_PHOTOS = "USER_GALLERY_VIEW_TYPE_PHOTOS";
	public static final String USER_GALLERY_VIEW_TYPE_LIKES = "USER_GALLERY_VIEW_TYPE_LIKES";
	
	private BodyLayoutStackListener listener;
	private GridView userGalleryGrid;
	private UserGalleryAdapter userGalleryAdapter;
	private String userDataURL;
	private ArrayList<ProductActivityData> arrayProductData;
	private String viewDataType;
	private ImageLoader imageLoader;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	
	public UserGalleryLayout(Activity activity, String setDataURL, String setViewDataType) {
		super(activity);
		View.inflate( this.getContext(), R.layout.usergallerylayout, this);
		
		viewDataType = setViewDataType;
		
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		
		userGalleryGrid = (GridView) findViewById( R.id.userGalleryGridView );
		userGalleryAdapter = new UserGalleryAdapter();
		userGalleryGrid.setOnItemClickListener( this );
		arrayProductData = new ArrayList<ProductActivityData>();
		imageLoader = new ImageLoader( this.getContext() );
		
		userDataURL = setDataURL;

		loadData();
	}

	private void loadData() {
		recycleResource();
		//NetworkThreadUtil.getRawData( userDataURL, null, this);
		asyncHttpClient.get( userDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject getJsonObject) {
				super.onSuccess(getJsonObject);
				onUserDataURLSuccess(getJsonObject);
			}
		});
	}
	
	private void onUserDataURLSuccess(JSONObject jsonObject){
		try {
			JSONArray newArray = jsonObject.getJSONArray("entities");
			for(int i = 0; i<newArray.length(); i++){
				//Load Product Data
				ProductActivityData newData = null;
				
				if( viewDataType.equals( USER_GALLERY_VIEW_TYPE_PHOTOS ) ){
					newData = new ProductActivityData( (JSONObject) newArray.get(i) );
				}else if( viewDataType.equals( USER_GALLERY_VIEW_TYPE_LIKES ) ){
					ProductActorLikeData productActorLikeActivity = new ProductActorLikeData( (JSONObject) newArray.get(i) );
					newData = productActorLikeActivity.getLike().getActivityData();
				}
				
				arrayProductData.add(newData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		userGalleryAdapter.setData( arrayProductData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				userGalleryGrid.setAdapter( userGalleryAdapter );
			}
		});
	}

	private void recycleResource() {
		arrayProductData.clear();
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutStackListener( BodyLayoutStackListener setListener ){
		this.listener = setListener;
	}
	
	private class UserGalleryAdapter extends BaseAdapter {
		
		private ArrayList<ProductActivityData> arrayProductData;
		
		public void setData(ArrayList<ProductActivityData> setArrayProductData){
			arrayProductData = setArrayProductData;
		}
		
		@Override
		public int getCount() {
			return arrayProductData.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayProductData.get( position );
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			/*ProductImageThumbnailDetail returnView;
			
			if (convertView == null) {
				ProductImageThumbnailDetail newImageThumbnailDetail = new ProductImageThumbnailDetail( UserGalleryLayout.this.getContext() );
				returnView = newImageThumbnailDetail;
			}else{
				returnView = (ProductImageThumbnailDetail) convertView;
			}
			
			returnView.setProductData( arrayProductData.get( position ) );
			returnView.setProductImage( arrayProductImage.get( position ) );
			returnView.setProductName( arrayProductData.get( position ).getProduct().getBrand().getName() );
			returnView.setUserName( arrayProductData.get( position ).getActor().getName() );*/
			ProductImageThumbnail returnView;
			
			if (convertView == null) {
				ProductImageThumbnail newImageThumbnail = new ProductImageThumbnail( UserGalleryLayout.this.getContext() );
				returnView = newImageThumbnail;
			}else{
				returnView = (ProductImageThumbnail) convertView;
			}
			
			String productImageURL = arrayProductData.get( position ).getProduct().getProductPicture().getImageUrls().getImageURLT();
			
			returnView.setProductData( arrayProductData.get( position ) );
			imageLoader.DisplayImage(productImageURL, UserGalleryLayout.this.getActivity(), returnView.getProductImageView(), true, asyncHttpClient );
			
			return returnView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		if(listener != null){
			if( v instanceof  ProductImageThumbnail ){
				ProductImageThumbnail productSelect = (ProductImageThumbnail) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, productSelect.getProductData().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
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
				ProductActivityData newData = null;
				
				if( viewDataType.equals( USER_GALLERY_VIEW_TYPE_PHOTOS ) ){
					newData = new ProductActivityData( (JSONObject) newArray.get(i) );
				}else if( viewDataType.equals( USER_GALLERY_VIEW_TYPE_LIKES ) ){
					ProductActorLikeData productActorLikeActivity = new ProductActorLikeData( (JSONObject) newArray.get(i) );
					newData = productActorLikeActivity.getLike().getActivityData();
				}
				
				arrayProductData.add(newData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		userGalleryAdapter.setData( arrayProductData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				userGalleryGrid.setAdapter( userGalleryAdapter );
			}
		});
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}*/
}
