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
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.WishlistData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.FooterListView;
import com.codegears.getable.ui.ProductWishlistItem;
import com.codegears.getable.ui.WishlistAddRemoveButton;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;

public class ProductWishlistLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_WISHLLIST_VALUE = "SHARE_PREF_WISHLLIST_VALUE";
	public static final String SHARE_PREF_KEY_PRODUCT_ID = "SHARE_PREF_KEY_PRODUCT_ID";
	public static final String SHARE_PREF_KEY_WISHLIST_ID = "SHARE_PREF_KEY_WISHLIST_ID";
	
	private String productActivityId;
	private Config config;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	//private List<String> appCookie;
	private JSONArray arrayWishlistId;
	private ArrayList<WishlistData> arrayWishlistData;
	private ListView wishlistList;
	private WishlistAdapter wishlistAdapter;
	private Button createButton;
	private LinearLayout createWishlistLayout;
	private LinearLayout createWishlistLayoutTop;
	private EditText wishlistNameEditText;
	private Button wishlistNameSubmitButton;
	private String wishlistURL;
	private ProgressDialog loadingDialog;
	private ImageButton backButton;
	private TextView chooseWishlistText;
	private TextView createWishlistText;
	private BodyLayoutStackListener listener;
	
	public ProductWishlistLayout(Activity activity) {
		super(activity);
		View.inflate(this.getContext(), R.layout.productwishlistlayout, this);
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_WISHLLIST_VALUE, this.getActivity().MODE_PRIVATE );
		//Get product act id.
		productActivityId = myPrefs.getString( SHARE_PREF_KEY_PRODUCT_ID, null );
		
		//Get array wishlist id
		try {
			arrayWishlistId = new JSONArray( myPrefs.getString( SHARE_PREF_KEY_WISHLIST_ID, "" ) );
		} catch (JSONException e) {
			e.printStackTrace();
			arrayWishlistId = new JSONArray();
		}
		
		config = new Config( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		arrayWishlistData = new ArrayList<WishlistData>();
		wishlistList = (ListView) findViewById( R.id.productWishlistListView );
		wishlistAdapter = new WishlistAdapter();
		createButton = (Button) findViewById( R.id.productWishlistCreateButton );
		createWishlistLayout = (LinearLayout) findViewById( R.id.productWishlistCreateLayout );
		createWishlistLayoutTop = (LinearLayout) findViewById( R.id.productWishlistCreateLayoutTop );
		wishlistNameEditText = (EditText) findViewById( R.id.productWishlistCreateLayoutNameEditText );
		wishlistNameSubmitButton = (Button) findViewById( R.id.productWishlistCreateLayoutNameSubmitButton );
		backButton = (ImageButton) findViewById( R.id.productWishlistBackButton );
		chooseWishlistText = (TextView) findViewById( R.id.productWishlistChooseWishlistText );
		createWishlistText = (TextView) findViewById( R.id.productWishlistCreateWishlistText );
		
		//Add foot view
		wishlistList.addFooterView( new FooterListView( this.getContext() ) );
		
		//Set font
		chooseWishlistText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		createWishlistText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		wishlistNameEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		createButton.setOnClickListener( this );
		createWishlistLayoutTop.setOnClickListener( this );
		wishlistNameSubmitButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		wishlistNameEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
	        	  InputMethodManager imm = (InputMethodManager) ProductWishlistLayout.this.getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				  imm.hideSoftInputFromWindow( wishlistNameEditText.getWindowToken(), 0 );
		          submitCreatNewWishlist();
		          return true;
		        }
		        return false;
			}
		});
		
		wishlistURL = config.get( MyApp.URL_DEFAULT )+"me/wishlists.json";
		
		loadData();
	}
	
	private void loadData(){
		loadingDialog.show();
		
		asyncHttpClient.get( wishlistURL, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject getJsonObject) {
				super.onSuccess(getJsonObject);
				onWishlistURLSuccess(getJsonObject);
			}
		});
	}
	
	private void submitCreatNewWishlist(){
		createWishlistLayout.setVisibility( View.GONE );
		String text = wishlistNameEditText.getText().toString();
		if( !(text.equals("")) && !(text.equals(null)) ){
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "name", text );
			RequestParams params = new RequestParams(paramMap);
			asyncHttpClient.post( wishlistURL, params, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					onWishlistURLSuccess(getJsonObject);
				}
			});
		}
	}
	
	private void onWishlistURLSuccess(JSONObject jsonObject){
		//Load Product Data
		if( jsonObject.optJSONArray( "entities" ) != null ){
			JSONArray newArray = jsonObject.optJSONArray( "entities" );
			for(int i = 0; i<newArray.length(); i++){
				
				//Load Product Data
				WishlistData newData = null;
				if( newArray.optJSONObject(i) != null ){
					newData = new WishlistData( newArray.optJSONObject(i) );
				}
				
				arrayWishlistData.add( newData );
			}
			
			wishlistAdapter.setData( arrayWishlistData );
			wishlistList.setAdapter( wishlistAdapter );
		}else{
			//Create new wishlist data.
			//Load Product Data
			final WishlistData newData = new WishlistData( jsonObject );
			String currentWishlistId = newData.getId();
			
			String addRemoveWishlistURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/wishlists/"+currentWishlistId+"/activities/"+productActivityId+".json";
			
			asyncHttpClient.post( addRemoveWishlistURL, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					
					//Add new id to array
					arrayWishlistId.put( newData.getId() );
					
					//Refresh adapter
					/*arrayWishlistData.add( newData );
					
					wishlistAdapter.setData( arrayWishlistData );
					wishlistList.setAdapter( wishlistAdapter );*/
					Toast toast = Toast.makeText( ProductWishlistLayout.this.getContext(), "Your new Wish List was created and the item was added to it.", Toast.LENGTH_LONG );
					toast.show();
					refreshView();
				}
			});
		}
		
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
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
	
	private void recycleResource(){
		arrayWishlistData.clear();
	}
	
	private class WishlistAdapter extends BaseAdapter {
		
		ArrayList<WishlistData> data;
		
		public void setData(ArrayList<WishlistData> arrayWishlistData) {
			data = arrayWishlistData;
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
			
			int checkWishlist = 0;
			
			ProductWishlistItem wishlistItem;
			if( convertView == null ){
				wishlistItem = new ProductWishlistItem( ProductWishlistLayout.this.getContext() );
			}else{
				wishlistItem = (ProductWishlistItem) convertView;
			}

			//Set information
			String numQty = String.valueOf( data.get( position ).getStatistic().getNumberOfActivities() );
			String wishlistName = data.get( position ).getName();
			final String wishlistId = data.get( position ).getId();
			WishlistAddRemoveButton addRemoveButton = wishlistItem.getAddRemoveButton();
			
			addRemoveButton.setWishlistData( data.get( position ) );
			
			wishlistItem.setItemQty( numQty );
			wishlistItem.setName( wishlistName );
			
			if( arrayWishlistId != null ){
				for( int i = 0; i < arrayWishlistId.length(); i++ ){
					if( arrayWishlistId.optString( i ).equals( wishlistId ) ){
						checkWishlist++;
					}
				}
				
				//Set button image and status.
				if( checkWishlist > 0 ){
					//addRemoveButton.setText( "Remove" );
					addRemoveButton.setImageButton( R.drawable.remove_button );
					addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_REMOVE );
				}else{
					//addRemoveButton.setText( "Add" );
					addRemoveButton.setImageButton( R.drawable.add_button );
					addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_ADD );
				}
			}else{
				//addRemoveButton.setText( "Add" );
				addRemoveButton.setImageButton( R.drawable.add_button );
				addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_ADD );
			}
			
			addRemoveButton.setWishlistData( data.get( position ) );
			addRemoveButton.setOnClickListener( ProductWishlistLayout.this );
			
			return wishlistItem;
		}
		
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onClick(View v) {
		if( v instanceof WishlistAddRemoveButton ){
			loadingDialog.show();
			
			final WishlistAddRemoveButton addRemoveButton = (WishlistAddRemoveButton) v;
			
			int buttonState = addRemoveButton.getButtonState();
			String currentWishlistId = addRemoveButton.getWishlistData().getId();
			String addRemoveWishlistURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/wishlists/"+currentWishlistId+"/activities/"+productActivityId+".json";
			final String wishlistId = addRemoveButton.getWishlistData().getId();
			
			//Post delete data
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put( "_a", "delete" );
			RequestParams deletePostData = new RequestParams(paramMap);
			
			if( buttonState == WishlistAddRemoveButton.BUTTON_STATE_ADD ){
				asyncHttpClient.post( addRemoveWishlistURL, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						//Set button image and status.
						Toast toast = Toast.makeText( ProductWishlistLayout.this.getContext(), "Added to Wish List.", Toast.LENGTH_LONG );
						toast.show();
						arrayWishlistId.put( wishlistId );
						refreshView();
					}
				});
			}else if( buttonState == WishlistAddRemoveButton.BUTTON_STATE_REMOVE ){
				asyncHttpClient.post( addRemoveWishlistURL, deletePostData, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						//Set button image and status.
						//Remove from wishlist
						JSONArray jsonArray = new JSONArray();
						for (int i = 0; i < arrayWishlistId.length(); i++) {
						    try {
								if( !(arrayWishlistId.getString( i ).toString().equals( wishlistId )) ){
									jsonArray.put( arrayWishlistId.getString( i ).toString() );
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						arrayWishlistId = jsonArray;
						refreshView();
					}
				});
			}
		}else if( v.equals( createButton ) ){
			createWishlistLayout.setVisibility( View.VISIBLE );
		}else if( v.equals( createWishlistLayoutTop ) ){
			createWishlistLayout.setVisibility( View.GONE );
		}else if( v.equals( wishlistNameSubmitButton ) ){
			submitCreatNewWishlist();
		}else if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( wishlistNameEditText.getWindowToken(), 0 );
			
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}

}
