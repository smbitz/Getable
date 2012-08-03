package com.codegears.getable.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.ProductCommentItemLayout;
import com.codegears.getable.ui.UserFollowItemLayout;
import com.codegears.getable.util.CalculateTime;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ImageLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;

public class ProductCommentLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID = "SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID";
	public static final String SHARE_PREF_KEY_ACT_ID = "SHARE_PREF_KEY_ACT_ID";
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
	private String addCommentDataURL;
	private String deleteCommentUrl;
	private MyApp app;
	private Button editButton;
	private int deleteButtonStatus;
	//private List<String> appCookie;
	private String activityUserId;
	private AsyncHttpClient asyncHttpClient;
	private ImageButton backButton;
	private ProgressDialog loadingDialog;
	
	public ProductCommentLayout(Activity activity) {
		super(activity);
		View.inflate(this.getContext(), R.layout.productcommentlayout, this);
		
		SharedPreferences myPreferences = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID, this.getActivity().MODE_PRIVATE );
		productId = myPreferences.getString( SHARE_PREF_KEY_ACT_ID, null );
		activityUserId = myPreferences.getString( SHARE_PREF_KEY_USER_ID, null );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		commentListView = (ListView) findViewById( R.id.productCommentLayoutListView );
		commentText = (EditText) findViewById( R.id.productCommentLayoutText );
		submitButton = (Button) findViewById( R.id.productCommentLayoutSubmitButton );
		config = new Config( this.getContext() );
		activityCommentsData = new ArrayList<ProductActivityCommentsData>();
		commentAdapter = new CommentAdapter();
		imageLoader = new ImageLoader( this.getContext() );
		editButton = (Button) findViewById( R.id.productCommentLayoutEditButton );
		backButton = (ImageButton) findViewById( R.id.productCommentLayoutBackButton );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		//Set footer listview
		commentListView.addFooterView( new FooterListView( this.getContext() ) );
		
		//Set font
		submitButton.setTypeface( Typeface.createFromAsset( this.getActivity().getAssets(), MyApp.APP_FONT_PATH) );
		commentText.setTypeface( Typeface.createFromAsset( this.getActivity().getAssets(), MyApp.APP_FONT_PATH) );
		
		submitButton.setOnClickListener( this );
		editButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		commentText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
	        	  InputMethodManager imm = (InputMethodManager) ProductCommentLayout.this.getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow( commentText.getWindowToken(), 0 );
		          submitCommentText();
		          return true;
		        }
		        return false;
			}
		});
		
		if( app.getUserId().equals( activityUserId ) ){
			editButton.setVisibility( View.VISIBLE );
		}
		
		deleteButtonStatus = DELETE_BUTTON_STATUS_HIDE;
		
		String urlVar1 = MyApp.DEFAULT_URL_VAR_1;
		
		getCommentDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+productId+"/comments.json"+urlVar1;
		addCommentDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/"+productId+"/comments.json";
		
		//NetworkThreadUtil.getRawData(getCommentDataURL, null, this);
		loadData();
	}
	
	public void loadData(){
		loadingDialog.show();
		
		asyncHttpClient.get( getCommentDataURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				super.onSuccess(jsonObject);
				onGetCommentURLSuccess( jsonObject, getCommentDataURL );
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
	
	private void onGetCommentURLSuccess(JSONObject jsonObject, String urlString){
		if( urlString.equals( getCommentDataURL ) ){
			JSONArray newArray = null;
			try {
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
			commentListView.setAdapter( commentAdapter );
		}else if( urlString.equals( addCommentDataURL ) ){
			ProductActivityCommentsData newData = new ProductActivityCommentsData( jsonObject );
			activityCommentsData.add( newData );
			
			commentAdapter.setData( activityCommentsData );
			commentText.setText("");
			commentListView.setAdapter( commentAdapter );
		}
		
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
		}
	}
	
	private void recycleResource() {
		activityCommentsData.clear();
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
				returnView.setUsetImageDefault();
			}
			
			String getUserImageURL = data.get( position ).getActor().getPicture().getImageUrls().getImageURLT();
			String setUserName = data.get( position ).getActor().getName();
			String setCommentText = data.get( position ).getComment().getCommentText();
			String setPostTime = CalculateTime.getPostTime( data.get( position ).getActivityTime() );
			
			imageLoader.DisplayImage(getUserImageURL, ProductCommentLayout.this.getActivity(), returnView.getUserImageView(), true, asyncHttpClient);
			returnView.setUserName( setUserName );
			returnView.setCommentText( setCommentText );
			returnView.setPostTime( setPostTime );
			returnView.setOnClickListener( ProductCommentLayout.this );
			returnView.setProductCommentActivityData( data.get( position ) );
			returnView.getDeleteButton().setActivityCommentsData( data.get( position ) );
			returnView.getDeleteButton().setOnClickListener( ProductCommentLayout.this );
			
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
			submitCommentText();
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
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "_a", "delete" );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( deleteCommentUrl, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					JSONObject jsonObject = null;
					jsonObject = getJsonObject.optJSONObject( "status" );
					
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
					commentText.setText("");
					commentListView.setAdapter( commentAdapter );
					commentListView.invalidateViews();
				}
			});
		}else if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( commentText.getWindowToken(), 0 );
			
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
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

	private void submitCommentText() {
		String text = commentText.getText().toString();
		if( !(text.equals("")) && !(text.equals(null)) ){
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "text", text );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( addCommentDataURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					super.onSuccess(jsonObject);
					try {
						String checkJson = jsonObject.getString("id");
						onGetCommentURLSuccess( jsonObject, addCommentDataURL );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						final AlertDialog alertDialog = new AlertDialog.Builder( ProductCommentLayout.this.getContext() ).create();
						alertDialog.setTitle( "Error" );
						alertDialog.setMessage( "Add comment fail." );
						alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
						alertDialog.show();
					}
				}
			});
		}
	}
	
}
