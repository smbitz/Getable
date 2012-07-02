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
import com.codegears.getable.GalleryFilterActivity;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.ProductImageThumbnail;
import com.codegears.getable.ui.ProductImageThumbnailDetail;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetCurrentLocation;
import com.codegears.getable.util.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.Keyboard.Key;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GalleryLayout extends AbstractViewLayout implements OnClickListener, OnItemClickListener {

	private static final String URL_GET_PRODUCT_ACTIVITIES = "URL_GET_PRODUCT_ACTIVITIES";
	
	private static final int GALLERY_THREE_ITEM_VIEW = 0;
	private static final int GALLERY_TWO_ITEM_VIEW = 1;
	
	private Button filterButton;
	private GridView galleryGrid;
	private GalleryAdapter galleryAdapter;
	private BodyLayoutStackListener listener;
	
	private ArrayList<ProductActivityData> arrayProductData;
	private Config config;
	private String urlVar1;
	private String urlVar2;
	private String urlVar3;
	private String currentLat;
	private String currentLng;
	private GetCurrentLocation getCurrentLocation;
	private ImageLoader imageLoader;
	private TextView textViewFilter1;
	private TextView textViewFilter2;
	private ImageButton threeItemViewButton;
	private ImageButton twoItemViewButton;
	private int galleryViewItemStatus;
	private AsyncHttpClient asyncHttpClient;
	private MyApp app;
	
	public GalleryLayout( Activity activity ) {
		super( activity );
		View.inflate( activity, R.layout.gallerylayout, this );
		
		filterButton = (Button)this.findViewById( R.id.FilterButton );
		galleryGrid = (GridView)this.findViewById( R.id.GalleryGrid );
		galleryAdapter = new GalleryAdapter();
		arrayProductData = new ArrayList<ProductActivityData>();
		config = new Config( this.getContext() );
		getCurrentLocation = new GetCurrentLocation( this.getContext() );
		imageLoader = new ImageLoader( this.getContext() );
		textViewFilter1 = (TextView) findViewById( R.id.galleryLayoutFilterText1 );
		textViewFilter2 = (TextView) findViewById( R.id.galleryLayoutFilterText2 );
		threeItemViewButton = (ImageButton) findViewById( R.id.galleryLayoutThreeItemViewButton );
		twoItemViewButton = (ImageButton) findViewById( R.id.galleryLayoutTwoItemViewButton );
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		
		galleryViewItemStatus = GALLERY_THREE_ITEM_VIEW;
		
		filterButton.setOnClickListener( this );
		galleryGrid.setOnItemClickListener( this );
		threeItemViewButton.setOnClickListener( this );
		twoItemViewButton.setOnClickListener( this );
		
		currentLat = getCurrentLocation.getCurrentLat();
		currentLng = getCurrentLocation.getCurrentLng();
		urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		urlVar2 = "&sort.properties[0].name=statistic.score.active&sort.properties[0].reverse=true&sort.properties[1].name=statistic.score.allTime&sort.properties[1].reverse=true";
		urlVar3 = "&currentCoordinate.latitude="+currentLat+"&currentCoordinate.longitude="+currentLng;
		
		loadData();
	}
	
	public void loadData(){
		recycleResource();
		/*NetworkThreadUtil.getRawData(
				config.get( URL_GET_PRODUCT_ACTIVITIES ).toString()+urlVar1+urlVar2+urlVar3,
        		null, this);*/
		asyncHttpClient.post( 
			config.get( URL_GET_PRODUCT_ACTIVITIES ).toString()+urlVar1+urlVar2+urlVar3,
			new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						JSONArray newArray = jsonObject.getJSONArray("entities");
						for(int i = 0; i<newArray.length(); i++){
							//Load Product Data
							ProductActivityData newData = new ProductActivityData( newArray.getJSONObject(i) );
							arrayProductData.add(newData);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					galleryAdapter.setData( arrayProductData );
					galleryGrid.setAdapter( galleryAdapter );
				}
		});
	}
	
	private void recycleResource() {
		arrayProductData.clear();
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onClick( View v ) {
		if( v.equals( filterButton )){
			Intent intent = new Intent(this.getContext(), GalleryFilterActivity.class);
			this.getActivity().startActivityForResult( intent, MainActivity.REQUEST_GALLERY_FILTER );
		}else if( v.equals( threeItemViewButton ) ){
			if( galleryViewItemStatus == GALLERY_TWO_ITEM_VIEW ){
				galleryGrid.setNumColumns( 3 );
				galleryViewItemStatus = GALLERY_THREE_ITEM_VIEW;
				threeItemViewButton.setImageResource( R.drawable.gallery_3_item_view_selected );
				twoItemViewButton.setImageResource( R.drawable.gallery_2_item_view );
				refreshView();
			}
		}else if( v.equals( twoItemViewButton ) ){
			if( galleryViewItemStatus == GALLERY_THREE_ITEM_VIEW ){
				galleryGrid.setNumColumns( 2 );
				galleryViewItemStatus = GALLERY_TWO_ITEM_VIEW;
				twoItemViewButton.setImageResource( R.drawable.gallery_2_item_view_selected );
				threeItemViewButton.setImageResource( R.drawable.gallery_3_item_view );
				refreshView();
			}
		}
	}
	
	@Override
	public void onItemClick( AdapterView< ? > arg0, View v, int arg2, long arg3 ) {
		if(listener != null){
			if( v instanceof  ProductImageThumbnail ){
				ProductImageThumbnail productSelect = (ProductImageThumbnail) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, productSelect.getProductData().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
			}
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
			
			View returnView = null;
			
			if( galleryViewItemStatus == GALLERY_THREE_ITEM_VIEW ){
				ProductImageThumbnail newImageThumbnail;
				
				if (convertView == null) {
					ProductImageThumbnail newImageThumbnailTemp = new ProductImageThumbnail(GalleryLayout.this.getContext());
					newImageThumbnail = newImageThumbnailTemp;
				}else{
					newImageThumbnail = (ProductImageThumbnail) convertView;
					newImageThumbnail.setProductImageDefault();
				}
				
				String productImageURL = arrayProductData.get( position ).getProduct().getProductPicture().getImageUrls().getImageURLT();
				
				newImageThumbnail.setProductData( arrayProductData.get( position ) );
				imageLoader.DisplayImage( productImageURL, GalleryLayout.this.getActivity(), newImageThumbnail.getProductImageView(), true, asyncHttpClient );
				
				returnView = newImageThumbnail;
			}else if( galleryViewItemStatus == GALLERY_TWO_ITEM_VIEW ){
				ProductImageThumbnailDetail newImageThumbnailDetail;
				
				if (convertView == null) {
					ProductImageThumbnailDetail newImageThumbnailDetailTemp = new ProductImageThumbnailDetail(GalleryLayout.this.getContext());
					newImageThumbnailDetail = newImageThumbnailDetailTemp;
				}else{
					newImageThumbnailDetail = (ProductImageThumbnailDetail) convertView;
					newImageThumbnailDetail.setProductImageDefault();
				}
				
				String productImageURL = arrayProductData.get( position ).getProduct().getProductPicture().getImageUrls().getImageURLT();
				
				newImageThumbnailDetail.setProductData( arrayProductData.get( position ) );
				newImageThumbnailDetail.setProductName( arrayProductData.get( position ).getProduct().getBrand().getName() );
				newImageThumbnailDetail.setUserName( arrayProductData.get( position ).getActor().getName() );
				imageLoader.DisplayImage( productImageURL, GalleryLayout.this.getActivity(), newImageThumbnailDetail.getProductImageView(), true, asyncHttpClient );
				
				returnView = newImageThumbnailDetail;
			}
			
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
				ProductActivityData newData = new ProductActivityData( newArray.getJSONObject(i) );
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
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public void refreshView( Intent getData ) {
		urlVar2 = getData.getExtras().getString( GalleryFilterActivity.PUT_EXTRA_URL_VAR_1 );
		urlVar3 = getData.getExtras().getString( GalleryFilterActivity.PUT_EXTRA_URL_VAR_2 );
		
		final String showTextFilter1 = getData.getExtras().getString( GalleryFilterActivity.PUT_EXTRA_FILTER_1 );
		final String showTextFilter2 = getData.getExtras().getString( GalleryFilterActivity.PUT_EXTRA_FILTER_2 );
		
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				textViewFilter1.setText( showTextFilter1 );
				textViewFilter2.setText( showTextFilter2 );
			}
		});
		
		loadData();
	}

	@Override
	public void refreshView() {
		recycleResource();
		loadData();
	}
	
}