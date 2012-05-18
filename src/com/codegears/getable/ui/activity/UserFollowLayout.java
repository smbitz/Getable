package com.codegears.getable.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActorFollowData;
import com.codegears.getable.data.WishlistData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.FollowButton;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkUtil;
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
	
	public static final int GET_FOLLOWERS = 0;
	public static final int GET_FOLLOWINGS = 1;
	
	private ListView followListView;
	private FollowAdapter followAdapter;
	private ArrayList<Bitmap> arrayUserImage;
	private ArrayList<ProductActorFollowData> arrayFollowData;
	private BodyLayoutStackListener listener;
	private Config config;
	private String followUserURL;
	private String unFollowUserURL;
	private MyApp app;
	private List<String> appCookie;
	private String defaultGetDataURL;
	private int requestPageType;
	
	public UserFollowLayout(Activity activity, String getDataFollowTypeURL, int getRequestPageType) {
		super(activity);
		
		app = (MyApp) this.getActivity().getApplication();
		appCookie = app.getAppCookie();
		config = new Config( this.getContext() );
		followListView = new ListView( this.getContext() );
		followAdapter = new FollowAdapter();
		arrayUserImage = new ArrayList<Bitmap>();
		arrayFollowData = new ArrayList<ProductActorFollowData>();
		requestPageType = getRequestPageType;
		
		this.addView( followListView );
		
		defaultGetDataURL = getDataFollowTypeURL;
		
		NetworkThreadUtil.getRawDataWithCookie(defaultGetDataURL, null, appCookie, this);
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
				userFollowItemLayout.getFollowButton().setVisibility( View.VISIBLE );
			}
			
			FollowButton userFollowButton = userFollowItemLayout.getFollowButton();
			
			userFollowItemLayout.setUserImage( arrayUserImage.get( position ) );
			userFollowItemLayout.setFollowUserData( data.get( position ) );
			
			if( requestPageType == GET_FOLLOWERS ){
				userFollowItemLayout.setMainText( data.get( position ).getActor().getName() );
				userFollowItemLayout.setSecondText( data.get( position ).getActor().getName() );
				userFollowButton.setActorData( data.get( position ).getActor() );
				
				//Set text/image follow/following
				if( data.get( position ).getActor().getMyRelation().getFollowActivity() != null ){
					userFollowButton.setText( "Following" );
					userFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
				}else{
					userFollowButton.setText( "Follow" );
					userFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
				}
			}else if( requestPageType == GET_FOLLOWINGS ){
				userFollowItemLayout.setMainText( data.get( position ).getFollowedUser().getName() );
				userFollowItemLayout.setSecondText( data.get( position ).getFollowedUser().getName() );
				userFollowButton.setActorData( data.get( position ).getFollowedUser() );
				
				//Set text/image follow/following
				if( data.get( position ).getFollowedUser().getMyRelation().getFollowActivity() != null ){
					userFollowButton.setText( "Following" );
					userFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
				}else{
					userFollowButton.setText( "Follow" );
					userFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
				}
			}
			
			if( app.getUserId().equals( data.get( position ).getActor().getId() ) ){
				userFollowButton.setVisibility( View.INVISIBLE );
			}
			
			userFollowItemLayout.setOnClickListener( UserFollowLayout.this );
			userFollowButton.setOnClickListener( UserFollowLayout.this );
			
			return userFollowItemLayout;
		}

	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( defaultGetDataURL ) ){
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
		}else if( urlString.equals( followUserURL ) ){
			//On click follow result.
		}
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
		if( v instanceof FollowButton ){
			final FollowButton followButton = (FollowButton) v;
			followButton.setEnabled( false );
			if( followButton.getFollowButtonStatus() == FollowButton.BUTTON_STATUS_UNFOLLOW ){
				String followUserId = followButton.getActorData().getId();
				
				followUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+followUserId+"/followers.json";
				Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "_a", "follow" );
				String postData = NetworkUtil.createPostData( newMapData );
				
				NetworkThreadUtil.getRawDataWithCookie(followUserURL, postData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						try {
							JSONObject jsonObject = new JSONObject(result);
							ActorData actorData = null;
							if( jsonObject.optJSONObject("followedUser") != null ){
								actorData = new ActorData( jsonObject.optJSONObject("followedUser") );
							}
							followButton.setActorData( actorData );
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						UserFollowLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								followButton.setEnabled( true );
							}
						});
					}
					
					@Override
					public void onNetworkFail(String urlString) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onNetworkDocSuccess(String urlString, Document document) {
						// TODO Auto-generated method stub
						
					}
				});
				
				//Set text/image follow/following
				followButton.setText( "Following" );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
			}else if( followButton.getFollowButtonStatus() == FollowButton.BUTTON_STATUS_FOLLOWING ){
				String followActivityId = followButton.getActorData().getMyRelation().getFollowActivity().getId();
				
				unFollowUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+followActivityId+".json";
				Map< String, String > newMapData = new HashMap<String, String>();
				newMapData.put( "_a", "delete" );
				String postData = NetworkUtil.createPostData( newMapData );
				
				NetworkThreadUtil.getRawDataWithCookie(unFollowUserURL, postData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						UserFollowLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								followButton.setEnabled( true );
							}
						});
					}
					
					@Override
					public void onNetworkFail(String urlString) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onNetworkDocSuccess(String urlString, Document document) {
						// TODO Auto-generated method stub
						
					}
				});
				
				//Set text/image follow/following
				followButton.setText( "Follow" );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
			}
		}else if(listener != null){
			if( v instanceof UserFollowItemLayout ){
				UserFollowItemLayout userFollowItemLayout = (UserFollowItemLayout) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				
				if( requestPageType == GET_FOLLOWERS ){
					prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userFollowItemLayout.getFollowUserData().getActor().getId() );
				}else if( requestPageType == GET_FOLLOWINGS ){
					prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userFollowItemLayout.getFollowUserData().getFollowedUser().getId() );
				}
				
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
		}
	}

}
