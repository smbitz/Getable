package com.codegears.getable.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityCommentsData;
import com.codegears.getable.data.ProductActorLikeData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.CommentRowLayout;
import com.codegears.getable.ui.FollowButton;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import android.view.View.OnClickListener;

public class ProductLikeLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {
	
	public static final String SHARE_PREF_VALUE_PRODUCT_ID = "SHARE_PREF_VALUE_PRODUCT_ID";
	public static final String SHARE_PREF_KEY_PRODUCT_ID = "SHARE_PREF_KEY_PRODUCT_ID";
	
	private ListView likeListView;
	private BodyLayoutStackListener listener;
	private LikeAdapter listLikeAdapter;
	private Config config;
	private String productId;
	private ArrayList<ProductActorLikeData> arrayLikeData;
	private ImageLoader imageLoader;
	private String getLikeDataURL;
	private String followUserURL;
	private String unFollowUserURL;
	private MyApp app;
	private List<String> appCookie;
	
	public ProductLikeLayout(Activity activity) {
		super(activity);
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_PRODUCT_ID, this.getActivity().MODE_PRIVATE );
		productId = myPreferences.getString( SHARE_PREF_KEY_PRODUCT_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		likeListView = new ListView( this.getContext() );
		listLikeAdapter = new LikeAdapter();
		config = new Config( this.getContext() );
		imageLoader = new ImageLoader( this.getContext() );
		arrayLikeData = new ArrayList<ProductActorLikeData>();
		appCookie = app.getAppCookie();
		
		this.addView( likeListView );
		
		getLikeDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+productId+"/likes.json";
		
		NetworkThreadUtil.getRawDataWithCookie(getLikeDataURL, null, appCookie, this);
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
	
	private class LikeAdapter extends BaseAdapter {
		
		ArrayList<ProductActorLikeData> data;
		
		public void setData(ArrayList<ProductActorLikeData> arrayLikeData) {
			data = arrayLikeData;
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
			
			UserFollowItemLayout returnView = null;
			
			if( convertView == null ){
				returnView = new UserFollowItemLayout( ProductLikeLayout.this.getContext() );
			}else{
				returnView = (UserFollowItemLayout) convertView;
				returnView.getFollowButton().setVisibility( View.VISIBLE );
			}
			
			String getUserImageURL = data.get( position ).getLike().getActivityData().getActor().getPicture().getImageUrls().getImageURLT();
			String getUserName = data.get( position ).getActor().getName();
			
			imageLoader.DisplayImage(getUserImageURL, ProductLikeLayout.this.getActivity(), returnView.getUserImageView(), true);
			returnView.setMainText( getUserName );
			returnView.setSecondText( getUserName );
			returnView.setLikeUserData( data.get( position ) );
			returnView.setOnClickListener( ProductLikeLayout.this );
			
			//FollowButton
			FollowButton followButton = returnView.getFollowButton();
			if( app.getUserId().equals( data.get( position ).getActor().getId() ) ){
				followButton.setVisibility( View.INVISIBLE );
			}else{
				followButton.setActorData( data.get( position ).getActor() );
				
				//Set text/image follow/following
				if( data.get( position ).getActor().getMyRelation().getFollowActivity() != null ){
					//followButton.setText( "Following" );
					followButton.setBackgroundResource( R.drawable.button_following );
					followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
				}else{
					//followButton.setText( "Follow" );
					followButton.setBackgroundResource( R.drawable.button_follow );
					followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
				}
				
				followButton.setOnClickListener( ProductLikeLayout.this );
			}
			
			return returnView;
		}
		
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( getLikeDataURL ) ){
			try {
				JSONObject jsonObject = new JSONObject(result);
				JSONArray newArray = jsonObject.getJSONArray("entities");
				for(int i = 0; i<newArray.length(); i++){
					//Load Like Data
					ProductActorLikeData newData = new ProductActorLikeData( (JSONObject) newArray.get(i) );
					arrayLikeData.add( newData );
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			listLikeAdapter.setData( arrayLikeData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					likeListView.setAdapter( listLikeAdapter );
				}
			});
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
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
						
						ProductLikeLayout.this.getActivity().runOnUiThread( new Runnable() {
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
				//followButton.setText( "Following" );
				followButton.setBackgroundResource( R.drawable.button_following );
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
						ProductLikeLayout.this.getActivity().runOnUiThread( new Runnable() {
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
				//followButton.setText( "Follow" );
				followButton.setBackgroundResource( R.drawable.button_follow );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
			}
		}else if(listener != null){
			if( v instanceof UserFollowItemLayout ){
				UserFollowItemLayout userFollowItemLayout = (UserFollowItemLayout) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userFollowItemLayout.getLikeUserData().getActor().getId() );
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
		}
	}

}
