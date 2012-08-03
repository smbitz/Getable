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
import com.codegears.getable.data.WishlistData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.view.View.OnClickListener;

public class UserWishlistsLayout extends AbstractViewLayout implements OnClickListener {

	public static final String SHARE_PREF_WISHLISTS_ID = "SHARE_PREF_WISHLISTS_ID";
	public static final String SHARE_PREF_KEY_WISHLISTS_ID = "SHARE_PREF_KEY_WISHLISTS_ID";
	
	private ListView wishlistsGallery;
	private WishlistsAdapter wishlistsAdapter;
	private ArrayList<WishlistData> arrayWishlistsData;
	private BodyLayoutStackListener listener;
	private ImageLoader imageLoader;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private String getWishlistsDataURL;
	private ProgressDialog loadingDialog;
	
	public UserWishlistsLayout(Activity activity, String setWishlistsDataURL) {
		super(activity);
		View.inflate( this.getContext(), R.layout.userwishlistslayout, this );
		
		wishlistsGallery = (ListView) findViewById( R.id.userWishlistsLayoutListView );
		arrayWishlistsData = new ArrayList<WishlistData>();
		wishlistsAdapter = new WishlistsAdapter();
		imageLoader = new ImageLoader( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		//Set line at footer
		wishlistsGallery.addFooterView( new FooterListView( this.getContext() ) );
		
		//NetworkThreadUtil.getRawData( getWishlistsDataURL, null, this);
		getWishlistsDataURL = setWishlistsDataURL;
		
		loadData();
	}
	
	private void recycleResource() {
		arrayWishlistsData.clear();
	}
	
	public void loadData(){
		loadingDialog.show();
		
		asyncHttpClient.get( getWishlistsDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
				
				try {
					JSONArray newArray = jsonObject.getJSONArray("entities");
					for(int i = 0; i<newArray.length(); i++){
						//Load Wishlists Data
						WishlistData newData = new WishlistData( (JSONObject) newArray.get(i) );
						JSONObject object = (JSONObject) newArray.get(i);
						arrayWishlistsData.add(newData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				wishlistsAdapter.setData( arrayWishlistsData );
				wishlistsGallery.setAdapter( wishlistsAdapter );
			}
		});
	}
	
	private class WishlistsAdapter extends BaseAdapter {
		
		private ArrayList<WishlistData> data;

		public void setData(ArrayList<WishlistData> arrayWishlistsData) {
			data = arrayWishlistsData;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			UserWishlistsGrouptItem userWishlistGrouptItem;
			
			if( convertView == null ){
				UserWishlistsGrouptItem wishlistGrouptItem = new UserWishlistsGrouptItem( UserWishlistsLayout.this.getContext() );
				userWishlistGrouptItem = wishlistGrouptItem;
			}else{
				userWishlistGrouptItem = (UserWishlistsGrouptItem) convertView;
			}
			
			String wishlistsGroupItemName = arrayWishlistsData.get( position ).getName();
			String wishlistsGroupItemNumber = String.valueOf( arrayWishlistsData.get( position ).getStatistic().getNumberOfActivities() );
			
			String imageURL = data.get( position ).getPicture().getImageUrls().getImageURLT();
			
			imageLoader.DisplayImage( imageURL, UserWishlistsLayout.this.getActivity(), userWishlistGrouptItem.getWishlistsImageView(), true, asyncHttpClient );
			userWishlistGrouptItem.setWishlistsName( wishlistsGroupItemName );
			userWishlistGrouptItem.setWishlistsItemNumber( wishlistsGroupItemNumber+" items" );
			userWishlistGrouptItem.setWishlistData( arrayWishlistsData.get( position ) );
			userWishlistGrouptItem.setOnClickListener( UserWishlistsLayout.this );
			return userWishlistGrouptItem;
		}
		
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		recycleResource();
		loadData();
	}

	public void setBodyLayoutStackListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if(listener != null){
			if( v instanceof UserWishlistsGrouptItem ){
				UserWishlistsGrouptItem wishlistGrouptItem = (UserWishlistsGrouptItem) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( SHARE_PREF_WISHLISTS_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( SHARE_PREF_KEY_WISHLISTS_ID, wishlistGrouptItem.getWishlistData().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_WISHLISTS_GALLERY );
		}
	}
	
}
