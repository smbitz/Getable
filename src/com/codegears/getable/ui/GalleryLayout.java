package com.codegears.getable.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.GalleryFilterActivity;
import com.codegears.getable.MainActivity;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetCurrentLocation;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GalleryLayout extends AbstractViewLayout implements OnClickListener, OnItemClickListener, NetworkThreadListener {

	public static final int LAYOUTCHANGE_PRODUCTDETAIL = 1;
	private static final String URL_GET_PRODUCT_ACTIVITIES = "URL_GET_PRODUCT_ACTIVITIES";
	public static final String SHARE_PREF_PRODUCT_ACT_ID = "SHARE_PREF_PRODUCT_ACT_ID";
	public static final String SHARE_PREF_KEY_ACTIVITY_ID = "SHARE_PREF_KEY_ACTIVITY_ID";
	
	private Button filterButton;
	private GridView galleryGrid;
	private GalleryAdapter galleryAdapter;
	private BodyLayoutStackListener listener;
	
	private ArrayList<ProductActivityData> arrayProductData;
	private ArrayList<Bitmap> arrayProductImage;
	private Config config;
	private String urlVar1;
	private String urlVar2;
	private String urlVar3;
	private String currentLat;
	private String currentLng;
	private GetCurrentLocation getCurrentLocation;
	private ProgressDialog loadingDialog;
	
	public GalleryLayout( Activity activity ) {
		super( activity );
		
		View.inflate( activity, R.layout.gallerylayout, this );
		filterButton = (Button)this.findViewById( R.id.FilterButton );
		filterButton.setOnClickListener( this );
		galleryGrid = (GridView)this.findViewById( R.id.GalleryGrid );
		galleryAdapter = new GalleryAdapter();
		galleryGrid.setOnItemClickListener( this );
		
		arrayProductData = new ArrayList<ProductActivityData>();
		arrayProductImage = new ArrayList<Bitmap>();
		config = new Config( activity );
		getCurrentLocation = new GetCurrentLocation( activity );
		
		currentLat = getCurrentLocation.getCurrentLat();
		currentLng = getCurrentLocation.getCurrentLng();
		urlVar1 = "?page.number=1&page.size=32";
		urlVar2 = "&sort.properties[0].name=statistic.score.active&sort.properties[0].reverse=true&sort.properties[1].name=statistic.score.allTime&sort.properties[1].reverse=true";
		urlVar3 = "&currentCoordinate.latitude="+currentLat+"&currentCoordinate.longitude="+currentLng;
		
		//galleryGrid.setOnItemClickListener( this );
		//SharedPreference.get(image.id);	// image which clicked at GalleryLayout, so use id as ....
		loadData();
	}
	
	public void loadData(){
		//NetworkThread.load();
		//public void onReceipt(){
			//Array<Image> image = data get;
			//click image
			//image.storeAsClickedImageAtGalleryLayout();
			//SharedPreference.put(key, image.id);
			//which image clicked??
		//}
		loadingDialog = ProgressDialog.show(this.getActivity(), "", 
	               "Loading. Please wait...", true);
		
		recycleResource();
		NetworkThreadUtil.getRawData(
				config.get( URL_GET_PRODUCT_ACTIVITIES ).toString()+urlVar1+urlVar2+urlVar3,
        		null, this);
	}
	
	private void recycleResource() {
		arrayProductData.clear();
		arrayProductImage.clear();
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onClick( View view ) {
		if(filterButton.equals( view )){
			Intent intent = new Intent(this.getContext(), GalleryFilterActivity.class);
			this.getActivity().startActivityForResult( intent, MainActivity.REQUEST_GALLERY_FILTER );
		}
	}
	
	@Override
	public void onItemClick( AdapterView< ? > arg0, View v, int arg2, long arg3 ) {
		if(listener != null){
			if( v instanceof  ProductImageThumbnail ){
				ProductImageThumbnail productSelect = (ProductImageThumbnail) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( SHARE_PREF_KEY_ACTIVITY_ID, productSelect.getProductData().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack(LAYOUTCHANGE_PRODUCTDETAIL);
		}
	}

	private class GalleryAdapter extends BaseAdapter {
		
		private ArrayList<ProductActivityData> arrayProductData;
		
		public void setData(ArrayList<ProductActivityData> setArrayProductData){
			arrayProductData = setArrayProductData;
		}
		
		@Override
		public int getCount() {
			return arrayProductData.size();
		}

		@Override
		public Object getItem( int position ) {
			return arrayProductData.get( position );
		}

		@Override
		public long getItemId( int position ) {
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup arg2 ) {
			ProductImageThumbnail returnView;
			
			if (convertView == null) {
				ProductImageThumbnail newImageThumbnail = new ProductImageThumbnail(GalleryLayout.this.getContext());
				returnView = newImageThumbnail;
			}else{
				returnView = (ProductImageThumbnail) convertView;
			}
			
			returnView.setProductData( arrayProductData.get( position ) );
			returnView.setProductImage( arrayProductImage.get( position ) );
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
				
				//Load Product Image
				URL mainPictureURL = null;
				Bitmap imageBitmap = null;
				try {
					mainPictureURL = new URL( newData.getProduct().getProductPicture().getImageUrls().getImageURLT() );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				try {
					imageBitmap = BitmapFactory.decodeStream( mainPictureURL.openConnection().getInputStream() );
				} catch (IOException e) {
					e.printStackTrace();
				}

				arrayProductImage.add(imageBitmap);
				arrayProductData.add(newData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		galleryAdapter.setData( arrayProductData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				galleryGrid.setAdapter( galleryAdapter );
			}
		});
		
		loadingDialog.dismiss();
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView( Intent getData ) {
		urlVar2 = getData.getExtras().getString( GalleryFilterActivity.PUT_EXTRA_URL_VAR_1 );
		urlVar3 = getData.getExtras().getString( GalleryFilterActivity.PUT_EXTRA_URL_VAR_2 );
		loadData();
	}

}