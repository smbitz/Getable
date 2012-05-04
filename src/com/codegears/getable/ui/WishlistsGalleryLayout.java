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
import com.codegears.getable.MainActivity;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;

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
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;

public class WishlistsGalleryLayout extends AbstractViewLayout implements OnItemClickListener, NetworkThreadListener {

	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String WISHLISTS_GALLERY_VIEW = "WISHLISTS_GALLERY_VIEW";
	
	private BodyLayoutStackListener listener;
	private GridView wishlistsGalleryGrid;
	private WishlistsGalleryAdapter wishlistsGalleryAdapter;
	private String wishlistsDataURL;
	private ArrayList<ProductActivityData> arrayProductData;
	private ArrayList<Bitmap> arrayProductImage;
	private Button filterButton;
	private Config config;
	private String wishlistsId;
	
	public WishlistsGalleryLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.wishlistsgallerylayout, this);
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserWishlistsLayout.SHARE_PREF_WISHLISTS_ID, this.getActivity().MODE_PRIVATE );
		wishlistsId = myPreferences.getString( UserWishlistsLayout.SHARE_PREF_KEY_WISHLISTS_ID, null );
		
		wishlistsGalleryGrid = (GridView) findViewById( R.id.wishlistsGalleryGridView );
		wishlistsGalleryAdapter = new WishlistsGalleryAdapter();
		wishlistsGalleryGrid.setOnItemClickListener( this );
		arrayProductData = new ArrayList<ProductActivityData>();
		arrayProductImage = new ArrayList<Bitmap>();
		filterButton = (Button) findViewById( R.id.wishlistsGalleryFilterButton );
		config = new Config(this.getContext());
		
		wishlistsDataURL = config.get( URL_DEFAULT ).toString()+"wishlists/"+wishlistsId+"/activities.json";

		loadData();
	}

	private void loadData() {
		recycleResource();
		NetworkThreadUtil.getRawData( wishlistsDataURL, null, this);
	}

	private void recycleResource() {
		arrayProductData.clear();
		arrayProductImage.clear();
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener( BodyLayoutStackListener setListener ){
		this.listener = setListener;
	}
	
	private class WishlistsGalleryAdapter extends BaseAdapter {
		
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
			productImageThumbnailDetail returnView;
			
			if (convertView == null) {
				productImageThumbnailDetail newImageThumbnailDetail = new productImageThumbnailDetail( WishlistsGalleryLayout.this.getContext() );
				returnView = newImageThumbnailDetail;
			}else{
				returnView = (productImageThumbnailDetail) convertView;
			}
			
			returnView.setProductData( arrayProductData.get( position ) );
			returnView.setProductImage( arrayProductImage.get( position ) );
			returnView.setProductName( arrayProductData.get( position ).getProduct().getBrand().getName() );
			returnView.setUserName( arrayProductData.get( position ).getActor().getName() );
			return returnView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		if(listener != null){
			if( v instanceof  productImageThumbnailDetail ){
				productImageThumbnailDetail productSelect = (productImageThumbnailDetail) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ProductDetailLayout.SHARE_PREF_PRODUCT_ACT_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ProductDetailLayout.SHARE_PREF_KEY_ACTIVITY_ID, productSelect.getProductData().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCTDETAIL );
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
		
		wishlistsGalleryAdapter.setData( arrayProductData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				wishlistsGalleryGrid.setAdapter( wishlistsGalleryAdapter );
			}
		});
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
}
