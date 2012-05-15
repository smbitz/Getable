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
import com.codegears.getable.data.WishlistData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.view.View.OnClickListener;

public class UserWishlistsLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {

	public static final String SHARE_PREF_WISHLISTS_ID = "SHARE_PREF_WISHLISTS_ID";
	public static final String SHARE_PREF_KEY_WISHLISTS_ID = "SHARE_PREF_KEY_WISHLISTS_ID";
	
	private ListView wishlistsGallery;
	private WishlistsAdapter wishlistsAdapter;
	private ArrayList<WishlistData> arrayWishlistsData;
	private ArrayList<Bitmap> arrayWishlistsImage;
	private BodyLayoutStackListener listener;
	
	public UserWishlistsLayout(Activity activity, String getWishlistsDataURL) {
		super(activity);
		
		wishlistsGallery = new ListView(this.getContext());
		arrayWishlistsData = new ArrayList<WishlistData>();
		arrayWishlistsImage = new ArrayList<Bitmap>();
		wishlistsAdapter = new WishlistsAdapter();
		
		this.addView( wishlistsGallery );
		
		NetworkThreadUtil.getRawData( getWishlistsDataURL, null, this);
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
			
			userWishlistGrouptItem.setWishlistsImage( arrayWishlistsImage.get( position ) );
			userWishlistGrouptItem.setWishlistsName( wishlistsGroupItemName );
			userWishlistGrouptItem.setWishlistsItemNumber( wishlistsGroupItemNumber+" items" );
			userWishlistGrouptItem.setWishlistData( arrayWishlistsData.get( position ) );
			userWishlistGrouptItem.setOnClickListener( UserWishlistsLayout.this );
			return userWishlistGrouptItem;
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
				//Load Wishlists Data
				WishlistData newData = new WishlistData( (JSONObject) newArray.get(i) );
				JSONObject object = (JSONObject) newArray.get(i);
				
				//Load Wishlists Image
				URL mainPictureURL = null;
				Bitmap imageBitmap = null;
				try {
					mainPictureURL = new URL( newData.getPicture().getImageUrls().getImageURLT() );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				try {
					imageBitmap = BitmapFactory.decodeStream( mainPictureURL.openConnection().getInputStream() );
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				arrayWishlistsImage.add(imageBitmap);
				arrayWishlistsData.add(newData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		wishlistsAdapter.setData( arrayWishlistsData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				wishlistsGallery.setAdapter( wishlistsAdapter );
			}
		});
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
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
