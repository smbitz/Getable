package com.codegears.getable.ui.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.FollowButton;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.codegears.getable.util.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;

public class ProfileFindFriendsListLayout extends AbstractViewLayout implements OnClickListener, TextWatcher {
	
	public static final String SHARE_PREF_FIND_FRIENDS_ID = "SHARE_PREF_FIND_FRIENDS_ID";
	public static final String FIND_FRIENDS_FROM_VALUE_ID = "FIND_FROM_VALUE_ID";
	
	public static final int FIND_FROM_CONTACT = 1;
	public static final int FIND_FROM_FACEBOOK = 2;
	public static final int FIND_FROM_TWITTER = 3;
	public static final int FIND_FROM_SEARCH = 4;
	public static final int FIND_FROM_SUGGEST = 5;
	
	private BodyLayoutStackListener listener;
	private Button followAllButton;
	private ListView findFriendsListView;
	private int findFriendTypeId;
	private MyApp app;
	//private List<String> appCookie;
	private Config config;
	private ArrayList<ActorData> arrayActorData;
	private FriendsAdapter friendsAdapter;
	private ImageLoader imageLoader;
	private String followUserURL;
	private String unFollowUserURL;
	private String findFriendsURL;
	private String followAllUserURL;
	private ProgressDialog loadingDialog;
	private String findFriendsURLFromContact;
	private String findFriendsURLFromFacebook;
	private LinearLayout searchLayout;
	private EditText searchDialog;
	//private String postData;
	private RequestParams params;
	private ConnectFacebook connectFacebook;
	private AsyncHttpClient asyncHttpClient;
	private MultipartEntity friendFromContactReqEntity;
	
	public ProfileFindFriendsListLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.profilefindfriendslistlayout, this );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_FIND_FRIENDS_ID, this.getActivity().MODE_PRIVATE );
		findFriendTypeId = myPrefs.getInt( FIND_FRIENDS_FROM_VALUE_ID, 0 );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		arrayActorData = new ArrayList<ActorData>();
		friendsAdapter = new FriendsAdapter();
		imageLoader = new ImageLoader( this.getContext() );
		connectFacebook = new ConnectFacebook( this.getActivity() );
		
		followAllButton = (Button) findViewById( R.id.profileFindFriendsListFollowAllButton );
		findFriendsListView = (ListView) findViewById( R.id.profileFindFriendsListView );
		searchLayout = (LinearLayout) findViewById( R.id.profileFindfriendsListLayoutSearchLayout );
		searchDialog = (EditText) findViewById( R.id.profileFindfriendsListLayoutSearchDialog );
		
		followAllButton.setOnClickListener( this );
		
		findFriendsURL = "";
		
		if( findFriendTypeId == FIND_FROM_CONTACT ){
			loadingDialog.show();

			friendFromContactReqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			Cursor emailCursor = this.getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,null,null, null);
			while (emailCursor.moveToNext())
			{
				String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				try {
		        	friendFromContactReqEntity.addPart( "emails", new StringBody( email ));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			emailCursor.close();
			
			Cursor phones = this.getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			while (phones.moveToNext())
			{
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				try {
		        	friendFromContactReqEntity.addPart( "phoneNumbers", new StringBody( phoneNumber ));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			phones.close();
			
			findFriendsURLFromContact = config.get( MyApp.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=500";
			findFriendsURL = findFriendsURLFromContact;
		}else if( findFriendTypeId == FIND_FROM_FACEBOOK ){
			loadingDialog.show();
			
			connectFacebook.connectToFacebook();
			findFriendsURLFromFacebook = config.get( MyApp.URL_DEFAULT ).toString()+"me/social/facebook/friends.json?page.number=1&page.size=20";
			findFriendsURL = findFriendsURLFromFacebook;
		}else if( findFriendTypeId == FIND_FROM_TWITTER ){
			findFriendsURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/social/twitter/friends.json?page.number=1&page.size=20";
		}else if( findFriendTypeId == FIND_FROM_SEARCH ){
			searchLayout.setVisibility( View.VISIBLE );
			searchDialog.addTextChangedListener( this );
		}else if( findFriendTypeId == FIND_FROM_SUGGEST ){
			loadingDialog.show();
			
			findFriendsURL = config.get( MyApp.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=20&sort.properties[0].name=statistic.score.active&sort.properties[0].reverse=true&sort.properties[1].name=statistic.score.allTime&sort.properties[1].reverse=true";
		}
		
		loadData();
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		arrayActorData.clear();
		
		this.getActivity().runOnUiThread( new Runnable() {
			@Override
			public void run() {
				ProfileFindFriendsListLayout.this.invalidate();
			}
		});
	}
	
	private void loadData(){
		if( findFriendTypeId == FIND_FROM_CONTACT ){
			asyncHttpClient.post( this.getContext(), findFriendsURL, friendFromContactReqEntity, null, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					onFindFriendURLSuccess( getJsonObject );
				}
			});
		}else if( findFriendTypeId == FIND_FROM_FACEBOOK ){
			asyncHttpClient.get( findFriendsURL, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					onFindFriendURLSuccess( getJsonObject );
				}
			});
		}else{
			asyncHttpClient.post( findFriendsURL, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					onFindFriendURLSuccess( getJsonObject );
				}
			});
		}
	}
	
	private void onFindFriendURLSuccess( JSONObject jsonObject ){
		System.out.println("FromContact : "+jsonObject);
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
		}
		
		if( findFriendsURL.equals( findFriendsURLFromContact ) ){
			try {
				JSONArray newArray = jsonObject.getJSONArray("entities");
				for(int i = 0; i<newArray.length(); i++){
					//Load Actor Data
					ActorData newData = new ActorData( newArray.getJSONObject(i) );
					arrayActorData.add(newData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			friendsAdapter.setData( arrayActorData );
			findFriendsListView.setAdapter( friendsAdapter );
		}else if( findFriendsURL.equals( findFriendsURLFromFacebook ) ){
			try {
				JSONArray newArray = jsonObject.getJSONObject( "registeredFriends" ).getJSONArray("entities");
				for(int i = 0; i<newArray.length(); i++){
					//Load Actor Data
					ActorData newData = new ActorData( newArray.getJSONObject(i) );
					arrayActorData.add(newData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			friendsAdapter.setData( arrayActorData );
			findFriendsListView.setAdapter( friendsAdapter );
		}else{
			try {
				JSONArray newArray = jsonObject.getJSONArray("entities");
				for(int i = 0; i<newArray.length(); i++){
					//Load Actor Data
					ActorData newData = new ActorData( newArray.getJSONObject(i) );
					arrayActorData.add(newData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			friendsAdapter.setData( arrayActorData );
			findFriendsListView.setAdapter( friendsAdapter );
		}
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	private class FriendsAdapter extends BaseAdapter {
		
		private ArrayList<ActorData> data;
		
		public void setData(ArrayList<ActorData> arrayActorData) {
			data = arrayActorData;
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
			
			UserFollowItemLayout returnView;
			
			if( convertView == null ){
				returnView = new UserFollowItemLayout( ProfileFindFriendsListLayout.this.getContext() );
			}else{
				returnView = (UserFollowItemLayout) convertView;
			}
			
			ActorData currentData = data.get( position );
			returnView.setMainText( currentData.getName() );
			returnView.setSecondText( currentData.getName() );
			returnView.setActorData( currentData );
			
			String imageURL = currentData.getPicture().getImageUrls().getImageURLT();
			imageLoader.DisplayImage(imageURL, ProfileFindFriendsListLayout.this.getActivity(), returnView.getUserImageView(), true, asyncHttpClient);
			
			FollowButton userFollowButton = returnView.getFollowButton();
			
			//Set text/image follow/following
			if( currentData.getMyRelation().getFollowActivity() != null ){
				//userFollowButton.setText( "Following" );
				userFollowButton.setBackgroundResource( R.drawable.button_following );
				userFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_FOLLOWING );
			}else{
				//userFollowButton.setText( "Follow" );
				userFollowButton.setBackgroundResource( R.drawable.button_follow );
				userFollowButton.setFollowButtonStatus( FollowButton.BUTTON_STATUS_UNFOLLOW );
			}
			
			userFollowButton.setActorData( currentData );
			
			returnView.setOnClickListener( ProfileFindFriendsListLayout.this );
			userFollowButton.setOnClickListener( ProfileFindFriendsListLayout.this );
			
			return returnView;
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v.equals( followAllButton ) ){
			loadingDialog.show();
			
			followAllUserURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/followings.json";
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			for( ActorData fetchData : arrayActorData ){
				try {
					reqEntity.addPart("users", new StringBody( fetchData.getId() ));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			asyncHttpClient.post( this.getContext(), followAllUserURL, reqEntity, null, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					loadData();
					refreshView();
				}
			});
		}else if( v instanceof FollowButton ){
			loadingDialog.show();
			
			final FollowButton followButton = (FollowButton) v;
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
						
						followButton.setEnabled( true );
						
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
					}
				});
				
				//Set text/image follow/following
				//followButton.setText( "Following" );
				followButton.setBackgroundResource( R.drawable.button_following );
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
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
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
				
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userFollowItemLayout.getActorData().getId() );
				
		        prefsEditor.commit();
			}
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
		}
	}
	
	private void clearResource(){
		arrayActorData.clear();
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if( !(s.equals("")) && s != null && (s.length()>0) ){
			findFriendsURL = config.get( app.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=20&name="+s;
			clearResource();
			loadData();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
}
