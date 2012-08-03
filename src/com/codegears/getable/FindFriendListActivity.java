package com.codegears.getable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codegears.getable.data.ActorData;
import com.codegears.getable.ui.FollowButton;
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.ui.activity.ProfileFindFriendsListLayout;
import com.codegears.getable.ui.activity.UserProfileLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConnectFacebook;
import com.codegears.getable.util.ImageLoader;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FindFriendListActivity extends Activity implements OnClickListener {

	public static final String PUT_EXTRA_FIND_FRIENDS_ID = "PUT_EXTRA_FIND_FRIENDS_ID";
	
	private static final String NO_USER_FOUND_FROM_CONTACT = "\"No user found from your contact list.\"";
	private static final String NO_USER_FOUND_FROM_FACEBOOK = "\"No user found from your facebook.\"";
	private static final String NO_USER_FOUND_FROM_TWITTER = "\"No user found from your twitter.\"";
	private static final String NO_USER_FOUND_FROM_SUGGEST = "\"No user found from your suggest.\"";
	
	public static final int FIND_FROM_CONTACT = 1;
	public static final int FIND_FROM_FACEBOOK = 2;
	public static final int FIND_FROM_TWITTER = 3;
	public static final int FIND_FROM_SEARCH = 4;
	public static final int FIND_FROM_SUGGEST = 5;
	
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
	private String connectFacebookURL;
	private String getCurrentUserDataURL;
	private ProgressDialog loadingDialog;
	private String findFriendsURLFromContact;
	private String findFriendsURLFromFacebook;
	private LinearLayout searchLayout;
	private EditText searchDialog;
	//private String postData;
	private RequestParams params;
	private AsyncHttpClient asyncHttpClient;
	private MultipartEntity friendFromContactReqEntity;
	private LinearLayout noUserFoundTextLayout;
	private TextView noUserFoundText;
	private ImageButton backButton;
	private Facebook facebook;
	private int checkUserContactValue;
	private AlertDialog alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.profilefindfriendslistlayout );
		
		findFriendTypeId = this.getIntent().getExtras().getInt( PUT_EXTRA_FIND_FRIENDS_ID );
		
		loadingDialog = new ProgressDialog( this );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		app = (MyApp) this.getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this );
		arrayActorData = new ArrayList<ActorData>();
		friendsAdapter = new FriendsAdapter();
		imageLoader = new ImageLoader( this );
		facebook = app.getFacebook();
		alertDialog = new AlertDialog.Builder( this ).create();
		
		followAllButton = (Button) findViewById( R.id.profileFindFriendsListFollowAllButton );
		findFriendsListView = (ListView) findViewById( R.id.profileFindFriendsListView );
		searchLayout = (LinearLayout) findViewById( R.id.profileFindfriendsListLayoutSearchLayout );
		searchDialog = (EditText) findViewById( R.id.profileFindfriendsListLayoutSearchDialog );
		noUserFoundTextLayout = (LinearLayout) findViewById( R.id.profileFindFriendsListLayoutNoUserFoundLayout );
		noUserFoundText = (TextView) findViewById( R.id.profileFindFriendsListLayoutNoUserFoundText );
		backButton = (ImageButton) findViewById( R.id.profileFindFriendsListBackButton );
		
		//Set font
		followAllButton.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		
		//Set line at footer
		findFriendsListView.addFooterView( new FooterListView( this ) );
		
		followAllButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		findFriendsURL = "";
		connectFacebookURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/integrations/facebook.json";
		getCurrentUserDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"me.json";
		
		if( findFriendTypeId == FIND_FROM_CONTACT ){
			loadingDialog.show();

			friendFromContactReqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			Cursor emailCursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,null,null, null);
			while (emailCursor.moveToNext())
			{
				String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				try {
		        	friendFromContactReqEntity.addPart( "emails", new StringBody( email ));
		        	checkUserContactValue++;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			emailCursor.close();
			
			Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			while (phones.moveToNext())
			{
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				try {
		        	friendFromContactReqEntity.addPart( "phoneNumbers", new StringBody( phoneNumber ));
		        	checkUserContactValue++;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			phones.close();
			
			findFriendsURLFromContact = config.get( MyApp.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=500";
			findFriendsURL = findFriendsURLFromContact;
			
			loadData();
		}else if( findFriendTypeId == FIND_FROM_FACEBOOK ){
			findFriendsURLFromFacebook = config.get( MyApp.URL_DEFAULT ).toString()+"me/social/facebook/friends.json?page.number=1&page.size=20";
			findFriendsURL = findFriendsURLFromFacebook;
			
			loadingDialog.show();
			
			ActorData currentActorData = app.getCurrentProfileData();
			if( currentActorData.getSocialConnections().getFacebook().getStatus() ){
				loadData();
			}else{
				facebook.authorize( this, new DialogListener() {
					
					@Override
					public void onFacebookError(FacebookError e) {
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot find friends with facebook." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
						
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
						
						noUserFoundTextLayout.setVisibility( View.VISIBLE );
						noUserFoundText.setText( NO_USER_FOUND_FROM_FACEBOOK );
					}
					
					@Override
					public void onError(DialogError e) {
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Cannot find friends with facebook." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
						
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
						
						noUserFoundTextLayout.setVisibility( View.VISIBLE );
						noUserFoundText.setText( NO_USER_FOUND_FROM_FACEBOOK );
					}
					
					@Override
					public void onComplete(Bundle values) {
						String token = facebook.getAccessToken();  //get access token
						Long expires = facebook.getAccessExpires();  //get access expire
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( FindFriendListActivity.this );
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString( ConnectFacebook.FACEBOOK_ACCESS_TOKEN , token);
						editor.putLong( ConnectFacebook.FACEBOOK_ACCESS_EXPIRES , expires);
						editor.commit();
						
						HashMap<String, String> paramMap = new HashMap<String, String>();
						paramMap.put( "accessToken", facebook.getAccessToken() );
						paramMap.put( "_a", "connect" );
						RequestParams params = new RequestParams(paramMap);
						asyncHttpClient.post( connectFacebookURL, params, new JsonHttpResponseHandler(){
							@Override
							public void onSuccess(JSONObject jsonObject) {
								super.onSuccess(jsonObject);
								try {
									String checkValue = jsonObject.getString( "status" );
									asyncHttpClient.post( getCurrentUserDataURL, new JsonHttpResponseHandler(){
										@Override
										public void onSuccess(JSONObject userJSON) {
											super.onSuccess(userJSON);
											ActorData actorData = new ActorData( userJSON );
											app.setCurrentProfileData( actorData );
											loadData();
										}
									});
								} catch (JSONException e) {
									e.printStackTrace();
									if( loadingDialog.isShowing() ){
										loadingDialog.dismiss();
									}
									
									JSONObject errorObject;
									try {
										errorObject = jsonObject.getJSONObject( "error" );
										String message = errorObject.optString( "message" );
										alertDialog.setTitle( "Error" );
										alertDialog.setMessage( message );
										alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												alertDialog.dismiss();
											}
										});
										alertDialog.show();
									} catch (JSONException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										alertDialog.setTitle( "Error" );
										alertDialog.setMessage( "Cannot find friends with facebook." );
										alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												alertDialog.dismiss();
											}
										});
										alertDialog.show();
									}

									noUserFoundTextLayout.setVisibility( View.VISIBLE );
									noUserFoundText.setText( NO_USER_FOUND_FROM_FACEBOOK );
								}
							}
							
							@Override
							public void onFailure(Throwable arg0, JSONObject jsonObject) {
								// TODO Auto-generated method stub
								super.onFailure(arg0, jsonObject);
								if( loadingDialog.isShowing() ){
									loadingDialog.dismiss();
								}
								
								alertDialog.setTitle( "Error" );
								alertDialog.setMessage( "Find friends fail." );
								alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										alertDialog.dismiss();
									}
								});
								alertDialog.show();
								
								noUserFoundTextLayout.setVisibility( View.VISIBLE );
								noUserFoundText.setText( NO_USER_FOUND_FROM_FACEBOOK );
							}
						});
					}
					
					@Override
					public void onCancel() {
						if( loadingDialog.isShowing() ){
							loadingDialog.dismiss();
						}
					}
					
				});
			}
		}else if( findFriendTypeId == FIND_FROM_TWITTER ){
			findFriendsURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/social/twitter/friends.json?page.number=1&page.size=20";
			
			loadData();
		}else if( findFriendTypeId == FIND_FROM_SEARCH ){
			searchLayout.setVisibility( View.VISIBLE );
			/*searchDialog.addTextChangedListener( new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					if( !(s.equals("")) && s != null && (s.length()>0) ){
						findFriendsURL = config.get( app.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=20&name="+s;
						clearResource();
						loadData();
					}
				}
			});*/
			
			searchDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
			        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			            (keyCode == KeyEvent.KEYCODE_ENTER)) {
			            // Perform action on key press
			            searchData();
			            return true;
			        }
			        return false;
				}
			});
		}else if( findFriendTypeId == FIND_FROM_SUGGEST ){
			loadingDialog.show();
			
			findFriendsURL = config.get( MyApp.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=20&sort.properties[0].name=statistic.score.active&sort.properties[0].reverse=true&sort.properties[1].name=statistic.score.allTime&sort.properties[1].reverse=true";
			
			loadData();
		}
	}
	
	private void searchData(){
		String s = searchDialog.getText().toString();
		if( !(s.equals("")) && s != null && (s.length()>0) ){
			findFriendsURL = config.get( app.URL_DEFAULT ).toString()+"users.json?page.number=1&page.size=20&name="+s;
			clearResource();
			loadData();
		}
	}

	/*@Override
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
	}*/
	
	private void loadData(){
		if( findFriendTypeId == FIND_FROM_CONTACT ){
			if( checkUserContactValue > 0 ){
				asyncHttpClient.post( this, findFriendsURL, friendFromContactReqEntity, null, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject getJsonObject) {
						super.onSuccess(getJsonObject);
						onFindFriendURLSuccess( getJsonObject );
					}
				});
			}else{
				onFindFriendURLSuccess( null );
			}
			
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
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
		}
		
		if( findFriendsURL.equals( findFriendsURLFromContact ) ){
			if( jsonObject != null ){
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
				
				if( arrayActorData.size() == 0 ){
					noUserFoundTextLayout.setVisibility( View.VISIBLE );
					noUserFoundText.setText( NO_USER_FOUND_FROM_CONTACT );
				}else{
					noUserFoundTextLayout.setVisibility( View.INVISIBLE );
				}
				
				friendsAdapter.setData( arrayActorData );
				findFriendsListView.setAdapter( friendsAdapter );
			}else{
				noUserFoundTextLayout.setVisibility( View.VISIBLE );
				noUserFoundText.setText( NO_USER_FOUND_FROM_CONTACT );
			}
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
			
			if( arrayActorData.size() == 0 ){
				noUserFoundTextLayout.setVisibility( View.VISIBLE );
				noUserFoundText.setText( NO_USER_FOUND_FROM_FACEBOOK );
			}else{
				noUserFoundTextLayout.setVisibility( View.INVISIBLE );
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
			
			if( arrayActorData.size() == 0 ){
				if( findFriendTypeId == FIND_FROM_SEARCH ){
					String searchText = searchDialog.getText().toString();
					if( !(searchText.equals("")) && searchText != null && (searchText.length()>0) ){
						noUserFoundTextLayout.setVisibility( View.VISIBLE );
						noUserFoundText.setText( "\"No user name '"+searchText+"' found.\"" );
					}
				}else if( findFriendTypeId == FIND_FROM_SUGGEST ){
					noUserFoundTextLayout.setVisibility( View.VISIBLE );
					noUserFoundText.setText( NO_USER_FOUND_FROM_SUGGEST );
				}
			}else{
				noUserFoundTextLayout.setVisibility( View.INVISIBLE );
			}
			
			friendsAdapter.setData( arrayActorData );
			findFriendsListView.setAdapter( friendsAdapter );
		}
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
				returnView = new UserFollowItemLayout( FindFriendListActivity.this );
			}else{
				returnView = (UserFollowItemLayout) convertView;
			}
			
			ActorData currentData = data.get( position );
			returnView.setMainText( currentData.getName() );
			returnView.setSecondText( currentData.getName() );
			returnView.setActorData( currentData );
			
			String imageURL = currentData.getPicture().getImageUrls().getImageURLT();
			imageLoader.DisplayImage(imageURL, FindFriendListActivity.this, returnView.getUserImageView(), true, asyncHttpClient);
			
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
			
			returnView.setOnClickListener( FindFriendListActivity.this );
			userFollowButton.setOnClickListener( FindFriendListActivity.this );
			
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
			
			asyncHttpClient.post( this, followAllUserURL, reqEntity, null, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					loadData();
					arrayActorData.clear();
					//refreshView();
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
		}else if( v instanceof UserFollowItemLayout ){
			/*UserFollowItemLayout userFollowItemLayout = (UserFollowItemLayout) v;
			SharedPreferences myPref = this.getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			
			prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, userFollowItemLayout.getActorData().getId() );
			
	        prefsEditor.commit();
	        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );*/
		}else if( v.equals( backButton ) ){
			this.finish();
		}
	}
	
	private void clearResource(){
		arrayActorData.clear();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
}
