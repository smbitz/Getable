package com.codegears.getable.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.WishlistData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.ProductWishlistItem;
import com.codegears.getable.ui.WishlistAddRemoveButton;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import android.view.View.OnClickListener;

public class ProductWishlistLayout extends AbstractViewLayout implements NetworkThreadListener, OnClickListener {
	
	public static final String SHARE_PREF_WISHLLIST_VALUE = "SHARE_PREF_WISHLLIST_VALUE";
	public static final String SHARE_PREF_KEY_PRODUCT_ID = "SHARE_PREF_KEY_PRODUCT_ID";
	public static final String SHARE_PREF_KEY_WISHLIST_ID = "SHARE_PREF_KEY_WISHLIST_ID";
	
	private String productActivityId;
	private Config config;
	private MyApp app;
	private List<String> appCookie;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		config = new Config( this.getContext() );
		app = (MyApp) this.getActivity().getApplication();
		appCookie = app.getAppCookie();
		arrayWishlistData = new ArrayList<WishlistData>();
		wishlistList = (ListView) findViewById( R.id.productWishlistListView );
		wishlistAdapter = new WishlistAdapter();
		createButton = (Button) findViewById( R.id.productWishlistCreateButton );
		createWishlistLayout = (LinearLayout) findViewById( R.id.productWishlistCreateLayout );
		createWishlistLayoutTop = (LinearLayout) findViewById( R.id.productWishlistCreateLayoutTop );
		wishlistNameEditText = (EditText) findViewById( R.id.productWishlistCreateLayoutNameEditText );
		wishlistNameSubmitButton = (Button) findViewById( R.id.productWishlistCreateLayoutNameSubmitButton );
		
		createButton.setOnClickListener( this );
		createWishlistLayoutTop.setOnClickListener( this );
		wishlistNameSubmitButton.setOnClickListener( this );
		
		wishlistURL = config.get( MyApp.URL_DEFAULT )+"me/wishlists.json";
		
		NetworkThreadUtil.getRawDataWithCookie( wishlistURL, null, appCookie, this );
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		try {
			//Load Product Data
			JSONObject jsonObject = new JSONObject( result );
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
				this.getActivity().runOnUiThread( new Runnable() {
					@Override
					public void run() {
						wishlistList.setAdapter( wishlistAdapter );
					}
				});
			}else{
				//Create new wishlist data.
				//Load Product Data
				final WishlistData newData = new WishlistData( jsonObject );
				String currentWishlistId = newData.getId();
				
				String addRemoveWishlistURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/wishlists/"+currentWishlistId+"/activities/"+productActivityId+".json";
				
				HashMap< String, String > addDataMap = new HashMap<String, String>();
				addDataMap.put( "emtpy", "emtpy" );
				String addPostData = NetworkUtil.createPostData( addDataMap );
				
				NetworkThreadUtil.getRawDataWithCookie(addRemoveWishlistURL, addPostData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						//Add new id to array
						arrayWishlistId.put( newData.getId() );
						
						//Refresh adapter
						arrayWishlistData.add( newData );
						
						wishlistAdapter.setData( arrayWishlistData );
						ProductWishlistLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								wishlistList.setAdapter( wishlistAdapter );
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
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
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
			
			int chechWishlist = 0;
			
			ProductWishlistItem wishlistItem;
			if( convertView == null ){
				wishlistItem = new ProductWishlistItem( ProductWishlistLayout.this.getContext() );
			}else{
				wishlistItem = (ProductWishlistItem) convertView;
			}

			//Set information
			String numQty = String.valueOf( data.get( position ).getStatistic().getNumberOfActivities() );
			String wishlistName = data.get( position ).getName();
			String wishlistId = data.get( position ).getId();
			WishlistAddRemoveButton addRemoveButton = wishlistItem.getAddRemoveButton();
			
			wishlistItem.setItemQty( numQty );
			wishlistItem.setName( wishlistName );
			
			if( arrayWishlistId != null ){
				for( int i = 0; i < arrayWishlistId.length(); i++ ){
					if( arrayWishlistId.optString( i ).equals( wishlistId ) ){
						chechWishlist++;
					}
				}
				
				//Set button image and status.
				if( chechWishlist > 0 ){
					addRemoveButton.setText( "Remove" );
					addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_REMOVE );
				}else{
					addRemoveButton.setText( "Add" );
					addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_ADD );
				}
			}else{
				addRemoveButton.setText( "Add" );
				addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_ADD );
			}
			
			addRemoveButton.setWishlistData( data.get( position ) );
			addRemoveButton.setOnClickListener( ProductWishlistLayout.this );
			
			return wishlistItem;
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v instanceof WishlistAddRemoveButton ){
			final WishlistAddRemoveButton addRemoveButton = (WishlistAddRemoveButton) v;
			int buttonState = addRemoveButton.getButtonState();
			String currentWishlistId = addRemoveButton.getWishlistData().getId();
			String addRemoveWishlistURL = config.get( MyApp.URL_DEFAULT ).toString()+"me/wishlists/"+currentWishlistId+"/activities/"+productActivityId+".json";
			
			//Post delete data
			HashMap< String, String > removeDataMap = new HashMap<String, String>();
			removeDataMap.put( "_a", "delete" );
			String deletePostData = NetworkUtil.createPostData( removeDataMap );
			
			HashMap< String, String > addDataMap = new HashMap<String, String>();
			addDataMap.put( "emtpy", "emtpy" );
			String addPostData = NetworkUtil.createPostData( addDataMap );
			
			if( buttonState == WishlistAddRemoveButton.BUTTON_STATE_ADD ){
				NetworkThreadUtil.getRawDataWithCookie( addRemoveWishlistURL, addPostData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						ProductWishlistLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								//Set button image and status.
								addRemoveButton.setText( "Remove" );
								addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_REMOVE );
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
			}else if( buttonState == WishlistAddRemoveButton.BUTTON_STATE_REMOVE ){
				NetworkThreadUtil.getRawDataWithCookie( addRemoveWishlistURL, deletePostData, appCookie, new NetworkThreadListener() {
					
					@Override
					public void onNetworkRawSuccess(String urlString, String result) {
						ProductWishlistLayout.this.getActivity().runOnUiThread( new Runnable() {
							@Override
							public void run() {
								//Set button image and status.
								addRemoveButton.setText( "Add" );
								addRemoveButton.setButtonState( WishlistAddRemoveButton.BUTTON_STATE_ADD );
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
			}
		}else if( v.equals( createButton ) ){
			createWishlistLayout.setVisibility( View.VISIBLE );
		}else if( v.equals( createWishlistLayoutTop ) ){
			createWishlistLayout.setVisibility( View.GONE );
		}else if( v.equals( wishlistNameSubmitButton ) ){
			createWishlistLayout.setVisibility( View.GONE );
			String text = wishlistNameEditText.getText().toString();
			if( !(text.equals("")) && !(text.equals(null)) ){
				HashMap< String, String > dataMap = new HashMap<String, String>();
				dataMap.put( "name", text );
				String postData = NetworkUtil.createPostData( dataMap );
				
				NetworkThreadUtil.getRawDataWithCookie( wishlistURL, postData, appCookie, this );
			}
		}
	}

}
