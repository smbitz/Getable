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
import com.codegears.getable.WishlistsFilterActivity;
import com.codegears.getable.data.ProductActivityData;
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
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class WishlistsGalleryLayout extends AbstractViewLayout implements OnItemClickListener, OnClickListener {

	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String WISHLISTS_GALLERY_VIEW = "WISHLISTS_GALLERY_VIEW";
	
	private BodyLayoutStackListener listener;
	private GridView wishlistsGalleryGrid;
	private WishlistsGalleryAdapter wishlistsGalleryAdapter;
	private String wishlistsDataURL;
	private ArrayList<ProductActivityData> arrayProductData;
	private Button filterButton;
	private Config config;
	private String wishlistsId;
	private String urlVar1;
	private String urlVar2;
	private ImageLoader imageLoader;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private ImageButton backButton;
	private TextView filterTextView;
	
	public WishlistsGalleryLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.wishlistsgallerylayout, this);
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( UserWishlistsLayout.SHARE_PREF_WISHLISTS_ID, this.getActivity().MODE_PRIVATE );
		wishlistsId = myPreferences.getString( UserWishlistsLayout.SHARE_PREF_KEY_WISHLISTS_ID, null );
		
		wishlistsGalleryGrid = (GridView) findViewById( R.id.wishlistsGalleryGridView );
		wishlistsGalleryAdapter = new WishlistsGalleryAdapter();
		wishlistsGalleryGrid.setOnItemClickListener( this );
		arrayProductData = new ArrayList<ProductActivityData>();
		filterButton = (Button) findViewById( R.id.wishlistsGalleryFilterButton );
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config(this.getContext());
		imageLoader = new ImageLoader( this.getContext() );
		backButton = (ImageButton) findViewById( R.id.wishlistsGalleryBackButton );
		filterTextView = (TextView) findViewById( R.id.wishlistsGalleryFilterText );
		
		filterTextView.setTypeface( Typeface.createFromAsset( this.getActivity().getAssets(), MyApp.APP_FONT_PATH ) );
		
		filterButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		urlVar1 = "?page.number=1&page.size=32";
		urlVar2 = "&sort.properties[0].name=product.brand.name&sort.properties[0].reverse=false";

		loadData();
	}

	private void loadData() {
		recycleResource();
		wishlistsDataURL = config.get( URL_DEFAULT ).toString()+"wishlists/"+wishlistsId+"/activities.json"+urlVar1+urlVar2;
		//NetworkThreadUtil.getRawData( wishlistsDataURL, null, this);
		asyncHttpClient.get( wishlistsDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				try {
					JSONArray newArray = jsonObject.getJSONArray("entities");
					for(int i = 0; i<newArray.length(); i++){
						//Load Product Data
						ProductActivityData newData = new ProductActivityData( (JSONObject) newArray.get(i) );
						arrayProductData.add(newData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				wishlistsGalleryAdapter.setData( arrayProductData );
				wishlistsGalleryGrid.setAdapter( wishlistsGalleryAdapter );
			}
		});
	}

	private void recycleResource() {
		arrayProductData.clear();
	}

	@Override
	public void refreshView(Intent getData) {
		urlVar2 = getData.getExtras().getString( WishlistsFilterActivity.PUT_EXTRA_URL_VAR_1 );
		String filterText = getData.getExtras().getString( WishlistsFilterActivity.PUT_EXTRA_FILTER_TEXT );
		filterTextView.setText( filterText );
		loadData();
	}
	
	@Override
	public void refreshView() {
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
			ProductImageThumbnail returnView;
			
			if (convertView == null) {
				ProductImageThumbnail newImageThumbnailDetail = new ProductImageThumbnail( WishlistsGalleryLayout.this.getContext() );
				returnView = newImageThumbnailDetail;
			}else{
				returnView = (ProductImageThumbnail) convertView;
			}
			
			String productImageURL = arrayProductData.get( position ).getProduct().getProductPicture().getImageUrls().getImageURLT();
			
			returnView.setProductData( arrayProductData.get( position ) );
			imageLoader.DisplayImage( productImageURL, WishlistsGalleryLayout.this.getActivity(), returnView.getProductImageView(), true, asyncHttpClient );
			
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

	@Override
	public void onClick(View v) {
		if( v.equals( filterButton ) ){
			Intent newIntent = new Intent( this.getContext(), WishlistsFilterActivity.class );
			this.getActivity().startActivityForResult( newIntent, MainActivity.REQUEST_WISHLISTS_FILTER );
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}
}
