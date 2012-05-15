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
import com.codegears.getable.data.ProductActorFollowData;
import com.codegears.getable.data.WishlistData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.view.View.OnClickListener;

public class UserFollowLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {

	private ListView followListView;
	private FollowAdapter followAdapter;
	private ArrayList<Bitmap> arrayUserImage;
	private ArrayList<ProductActorFollowData> arrayFollowData;
	private BodyLayoutStackListener listener;
	
	public UserFollowLayout(Activity activity, String getDataFollowTypeURL) {
		super(activity);
		
		followListView = new ListView( this.getContext() );
		followAdapter = new FollowAdapter();
		arrayUserImage = new ArrayList<Bitmap>();
		arrayFollowData = new ArrayList<ProductActorFollowData>();
		
		this.addView( followListView );
		
		NetworkThreadUtil.getRawData( getDataFollowTypeURL, null, this );
	}

	@Override
	public void refreshView(Intent getData) {

	}
	
	private class FollowAdapter extends BaseAdapter{
		
		private ArrayList<ProductActorFollowData> data;
		
		public void setData(ArrayList<ProductActorFollowData> arrayFollowData) {
			data = arrayFollowData;
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
			UserFollowItemLayout userFollowItemLayout;
			
			if( convertView == null ){
				userFollowItemLayout = new UserFollowItemLayout( UserFollowLayout.this.getContext() );
			}else{
				userFollowItemLayout = (UserFollowItemLayout) convertView;
			}
			
			userFollowItemLayout.setUserImage( arrayUserImage.get( position ) );
			userFollowItemLayout.setMainText( data.get( position ).getFollowedUser().getName() );
			userFollowItemLayout.setSecondText( data.get( position ).getFollowedUser().getName() );
			userFollowItemLayout.setFollowUserData( data.get( position ) );
			userFollowItemLayout.setOnClickListener( UserFollowLayout.this );
			
			return userFollowItemLayout;
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
				//Load Follow Data
				ProductActorFollowData newData = new ProductActorFollowData( (JSONObject) newArray.get(i) );
				JSONObject object = (JSONObject) newArray.get(i);
				
				//Load Follow Image
				URL mainPictureURL = null;
				Bitmap imageBitmap = null;
				try {
					mainPictureURL = new URL( newData.getFollowedUser().getPicture().getImageUrls().getImageURLT() );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				try {
					imageBitmap = BitmapFactory.decodeStream( mainPictureURL.openConnection().getInputStream() );
				} catch (IOException e) {
					e.printStackTrace();
				}

				arrayUserImage.add(imageBitmap);
				arrayFollowData.add(newData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		followAdapter.setData( arrayFollowData );
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				followListView.setAdapter( followAdapter );
			}
		});
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutStackListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if(listener != null){
			if( v instanceof UserWishlistsGrouptItem ){
				UserFollowItemLayout userFollowItemLayout = (UserFollowItemLayout) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userFollowItemLayout.getFollowUserData().getFollowedUser().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
		}
	}

}
