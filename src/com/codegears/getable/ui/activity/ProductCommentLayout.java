package com.codegears.getable.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.LoginActivity;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityCommentsData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.CommentDeleteButton;
import com.codegears.getable.ui.CommentRowLayout;
import com.codegears.getable.ui.ProductCommentItemLayout;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.view.View.OnClickListener;

public class ProductCommentLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {
	
	public static final String SHARE_PREF_VALUE_PRODUCT_ID = "SHARE_PREF_VALUE_PRODUCT_ID";
	public static final String SHARE_PREF_KEY_PRODUCT_ID = "SHARE_PREF_KEY_PRODUCT_ID";
	public static final String SHARE_PREF_KEY_USER_ID = "SHARE_PREF_KEY_USER_ID";
	private static final int DELETE_BUTTON_STATUS_HIDE = 0;
	private static final int DELETE_BUTTON_STATUS_SHOW = 1;
	
	private BodyLayoutStackListener listener;
	private ListView commentListView;
	private String productId;
	private Config config;
	private ArrayList<ProductActivityCommentsData> activityCommentsData;
	private CommentAdapter commentAdapter;
	private ImageLoader imageLoader;
	private EditText commentText;
	private Button submitButton;
	private String getCommentDataURL;
	private String deleteCommentUrl;
	private MyApp app;
	private Button editButton;
	private int deleteButtonStatus;
	private List<String> appCookie;
	private String activityUserId;
	
	public ProductCommentLayout(Activity activity) {
		super(activity);
		View.inflate(this.getContext(), R.layout.productcommentlayout, this);
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_PRODUCT_ID, this.getActivity().MODE_PRIVATE );
		productId = myPreferences.getString( SHARE_PREF_KEY_PRODUCT_ID, null );
		activityUserId = myPreferences.getString( SHARE_PREF_KEY_USER_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		appCookie = app.getAppCookie();
		commentListView = (ListView) findViewById( R.id.productCommentLayoutListView );
		commentText = (EditText) findViewById( R.id.productCommentLayoutText );
		submitButton = (Button) findViewById( R.id.productCommentLayoutSubmitButton );
		config = new Config( this.getContext() );
		activityCommentsData = new ArrayList<ProductActivityCommentsData>();
		commentAdapter = new CommentAdapter();
		imageLoader = new ImageLoader( this.getContext() );
		editButton = (Button) findViewById( R.id.productCommentLayoutEditButton );
		
		submitButton.setOnClickListener( this );
		editButton.setOnClickListener( this );
		
		if( app.getUserId().equals( activityUserId ) ){
			editButton.setVisibility( View.VISIBLE );
		}
		
		deleteButtonStatus = DELETE_BUTTON_STATUS_HIDE;
		
		String urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		
		getCommentDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+productId+"/comments.json"+urlVar1;
		
		NetworkThreadUtil.getRawData(getCommentDataURL, null, this);
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

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		if( urlString.equals( getCommentDataURL ) ){
			JSONArray newArray = null;
			try {
				JSONObject jsonObject = new JSONObject( result );
				newArray = jsonObject.optJSONArray("entities");
				
				ProductActivityCommentsData newData;
				if( newArray == null ){
					newData = new ProductActivityCommentsData( jsonObject );
					activityCommentsData.add( newData );
				}else{
					for(int i = 0; i<newArray.length(); i++){
						//Load Product Data
						newData = new ProductActivityCommentsData( (JSONObject) newArray.get(i) );
						activityCommentsData.add( newData );
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			commentAdapter.setData( activityCommentsData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					commentText.setText("");
					commentListView.setAdapter( commentAdapter );
				}
			});
		}else if( urlString.equals( deleteCommentUrl ) ){
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject( result ).optJSONObject( "status" );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if( jsonObject != null ){
				String deleteCommentId = jsonObject.optString( "id" );
				ArrayList<ProductActivityCommentsData> newActivityCommentsData = new ArrayList<ProductActivityCommentsData>();
				for( ProductActivityCommentsData fetchData:activityCommentsData ){
					String currentCommentId = fetchData.getId();
					if( !(deleteCommentId.equals( currentCommentId )) ){
						newActivityCommentsData.add( fetchData );
					}
				}
				activityCommentsData = newActivityCommentsData;
			}
			
			commentAdapter.setData( activityCommentsData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					commentText.setText("");
					commentListView.setAdapter( commentAdapter );
					commentListView.invalidateViews();
				}
			});
		}
		
	}
	
	private void recycleResource() {
		activityCommentsData.clear();
	}
	
	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
	
	private class CommentAdapter extends BaseAdapter {
		
		private ArrayList<ProductActivityCommentsData> data;
		
		public void setData(ArrayList<ProductActivityCommentsData> activityCommentsData) {
			data = activityCommentsData;
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
			ProductCommentItemLayout returnView;
			
			if( convertView == null ){
				returnView = new ProductCommentItemLayout( ProductCommentLayout.this.getContext() );
			}else{
				returnView = (ProductCommentItemLayout) convertView;
			}
			
			String getUserImageURL = data.get( position ).getActor().getPicture().getImageUrls().getImageURLT();
			String setUserName = data.get( position ).getActor().getName();
			String setCommentText = data.get( position ).getComment().getCommentText();
			
			imageLoader.DisplayImage(getUserImageURL, ProductCommentLayout.this.getActivity(), returnView.getUserImageView(), true);
			returnView.setUserName( setUserName );
			returnView.setCommentText( setCommentText );
			returnView.setOnClickListener( ProductCommentLayout.this );
			returnView.setProductCommentActivityData( data.get( position ) );
			returnView.getDeleteButton().setActivityCommentsData( data.get( position ) );
			returnView.getDeleteButton().setOnClickListener( ProductCommentLayout.this );
			//returnView.setCommentTime(  );
			
			if( deleteButtonStatus == DELETE_BUTTON_STATUS_HIDE ){
				returnView.setDeleteButtonLayout( ProductCommentItemLayout.DELETE_LAYOUT_INVISIBLE );
			}else if( deleteButtonStatus == DELETE_BUTTON_STATUS_SHOW ){
				returnView.setDeleteButtonLayout( ProductCommentItemLayout.DELETE_LAYOUT_VISIBLE );
			}
			
			return returnView;
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v.equals( submitButton ) ){
			String text = commentText.getText().toString();
			if( !(text.equals("")) && !(text.equals(null)) ){
				HashMap< String, String > dataMap = new HashMap<String, String>();
				dataMap.put( "text", text );
				String postData = NetworkUtil.createPostData( dataMap );
				
				NetworkThreadUtil.getRawDataWithCookie(getCommentDataURL, postData, appCookie, this);
			}
		}else if( v.equals( editButton ) ){
			if( deleteButtonStatus == DELETE_BUTTON_STATUS_HIDE ){
				deleteButtonStatus = DELETE_BUTTON_STATUS_SHOW;
			}else if( deleteButtonStatus == DELETE_BUTTON_STATUS_SHOW ){
				deleteButtonStatus = DELETE_BUTTON_STATUS_HIDE;
			}
			
			commentListView.invalidateViews();
		}else if( v instanceof CommentDeleteButton ){
			CommentDeleteButton commentDeleteButton = (CommentDeleteButton) v;
			String commentActivityId = commentDeleteButton.getActivityCommentsData().getId();
			deleteCommentUrl = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+commentActivityId+".json";
			
			HashMap< String, String > dataMap = new HashMap<String, String>();
			dataMap.put( "_a", "delete" );
			String postData = NetworkUtil.createPostData( dataMap );
			
			NetworkThreadUtil.getRawDataWithCookie( deleteCommentUrl, postData, appCookie, this );
		}else if(listener != null){
			if( v instanceof ProductCommentItemLayout ){
				ProductCommentItemLayout productCommentItemLayout = (ProductCommentItemLayout) v;
				SharedPreferences myPref = this.getActivity().getSharedPreferences( UserProfileLayout.SHARE_PREF_VALUE_USER_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( UserProfileLayout.SHARE_PREF_KEY_USER_ID, productCommentItemLayout.getProductCommentActivityData().getActor().getId() );
		        prefsEditor.commit();
		        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_USERPROFILE );
			}
		}
	}
	
}
