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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.ui.UserWishlistsGrouptItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;

public class ProductLikeLayout extends AbstractViewLayout implements OnClickListener {
	
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
	//private List<String> appCookie;
	private AsyncHttpClient asyncHttpClient;
	private ProgressDialog loadingDialog;
	private ImageButton backButton;
	
	public ProductLikeLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.productlikelayout, this );
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_PRODUCT_ID, this.getActivity().MODE_PRIVATE );
		productId = myPreferences.getString( SHARE_PREF_KEY_PRODUCT_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		likeListView = (ListView) findViewById( R.id.productLikeLayoutListview );
		backButton = (ImageButton) findViewById( R.id.productLikeLayoutBackButton );
		listLikeAdapter = new LikeAdapter();
		config = new Config( this.getContext() );
		imageLoader = new ImageLoader( this.getContext() );
		arrayLikeData = new ArrayList<ProductActorLikeData>();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		//Set line at footer
		likeListView.addFooterView( new FooterListView( this.getContext() ) );
		
		getLikeDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+productId+"/likes.json";
		
		backButton.setOnClickListener( this );
		
		loadData();
	}
	
	public void recycleResource(){
		arrayLikeData.clear();
	}
	
	public void loadData(){
		loadingDialog.show();
		
		asyncHttpClient.get( getLikeDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				
				try {
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
				likeListView.setAdapter( listLikeAdapter );
				
				if( loadingDialog.isShowing() ){
					loadingDialog.dismiss();
				}
			}
		});
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
				returnView.setUserImageDefault();
			}
			
			//String getUserImageURL = data.get( position ).getLike().getActivityData().getActor().getPicture().getImageUrls().getImageURLT();
			String getUserImageURL = data.get( position ).getActor().getPicture().getImageUrls().getImageURLT();
			String getUserName = data.get( position ).getActor().getName();
			
			System.out.println("Name : "+getUserName+", MyImageURL : "+getUserImageURL);
			
			imageLoader.DisplayImage(getUserImageURL, ProductLikeLayout.this.getActivity(), returnView.getUserImageView(), true, asyncHttpClient );
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
					followButton.setImageResource( R.drawable.button_following );
					followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
				}else{
					//followButton.setText( "Follow" );
					followButton.setImageResource( R.drawable.button_follow );
					followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
				}
				
				followButton.setOnClickListener( ProductLikeLayout.this );
				followButton.setTag( data.get( position ) );
			}
			
			return returnView;
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v instanceof FollowButton ){
			final FollowButton followButton = (FollowButton) v;
			final ProductActorLikeData followButtonActorData = (ProductActorLikeData) v.getTag();
			followButton.setEnabled( false );
			if( followButton.getFollowButtonStatus() == FollowButton.BUTTON_STATUS_UNFOLLOW ){
				String followUserId = followButton.getActorData().getId();
				
				followUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"users/"+followUserId+"/followers.json";
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "follow" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( followUserURL, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						super.onSuccess(jsonObject);
						ActorData actorData = null;
						if( jsonObject.optJSONObject("followedUser") != null ){
							actorData = new ActorData( jsonObject.optJSONObject("followedUser") );
						}
						followButton.setActorData( actorData );
						followButtonActorData.setActorData( actorData );
						
						followButton.setEnabled( true );
					}
				});
				
				//Set text/image follow/following
				//followButton.setText( "Following" );
				followButton.setImageResource( R.drawable.button_following );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
			}else if( followButton.getFollowButtonStatus() == FollowButton.BUTTON_STATUS_FOLLOWING ){
				String followActivityId = followButton.getActorData().getMyRelation().getFollowActivity().getId();
				
				unFollowUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+followActivityId+".json";
				
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put( "_a", "delete" );
				RequestParams params = new RequestParams(paramMap);
				asyncHttpClient.post( unFollowUserURL, params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						followButton.setEnabled( true );
						followButtonActorData.getActor().getMyRelation().setFollowActivity( null );
					}
				});
				
				//Set text/image follow/following
				//followButton.setText( "Follow" );
				followButton.setImageResource( R.drawable.button_follow );
				followButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
			}
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
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
