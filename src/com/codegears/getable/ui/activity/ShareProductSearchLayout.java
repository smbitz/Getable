package com.codegears.getable.ui.activity;

import java.util.ArrayList;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.R.string;
import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.CategoryData;
import com.codegears.getable.data.ExternalStoresData;
import com.codegears.getable.data.GenderData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.data.StoreData;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.ShareProductSearchItem;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.ConvertURL;
import com.codegears.getable.util.GetCurrentLocation;
import com.codegears.getable.util.GetGender;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.view.View.OnClickListener;

public class ShareProductSearchLayout extends AbstractViewLayout implements OnClickListener, TextWatcher {

	public static final String SHARE_PREF_SEARCH_TYPE = "SHARE_PREF_SEARCH_TYPE";
	public static final String SHARE_PREF_KEY_SEARCH_TYPE = "SHARE_PREF_KEY_SEARCH_TYPE";
	
	public static final String SHARE_PREF_KEY_SEARCH_PRODUCT = "SHARE_PREF_KEY_SEARCH_PRODUCT";
	public static final String SHARE_PREF_KEY_SEARCH_BRAND = "SHARE_PREF_KEY_SEARCH_BRAND";
	public static final String SHARE_PREF_KEY_SEARCH_STORE = "SHARE_PREF_KEY_SEARCH_STORE";
	public static final String SHARE_PREF_KEY_SEARCH_GENDER = "SHARE_PREF_KEY_SEARCH_GENDER";
	
	private static final String SHARE_PREF_DETAIL_SUB_CATEGORY = "SHARE_PREF_DETAIL_SUB_CATEGORY";
	private static final String SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_ID = "SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_ID";
	private static final String SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_NAME = "SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_NAME";
	
	private BodyLayoutStackListener listener;
	private String searchType;
	private String getSubCategoryId;
	private String getSubCategoryName;
	private MyApp app;
	//private List<String> appCookie;
	private Config config;
	private ArrayList<CategoryData> arrayCategoryData;
	private ArrayList<BrandData> arrayBrandData;
	private ArrayList<StoreData> arrayStoreData;
	private ArrayList<GenderData> arrayGendetData;
	private ListView nameItemListView;
	private ProductNameItemAdapter productNameItemAdapter;
	private BrandNameItemAdapter brandNameItemAdapter;
	private StoreNameItemAdapter storeNameItemAdapter;
	private GenderNameItemAdapter genderNameItemAdapter;
	private EditText searchEditText;
	private GetCurrentLocation currentLocation;
	private GetGender getGender;
	private String getDataURL;
	private AsyncHttpClient asyncHttpClient;
	private ProgressDialog loadingDialog;
	
	public ShareProductSearchLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareproductsearchlayout, this );
		
		//Get search type.
		SharedPreferences myPrefsSearchType = this.getActivity().getSharedPreferences( SHARE_PREF_SEARCH_TYPE, this.getActivity().MODE_PRIVATE );
		searchType = myPrefsSearchType.getString( SHARE_PREF_KEY_SEARCH_TYPE, null );
		
		//Get subcategory value.
		SharedPreferences myPrefsSubCate = this.getActivity().getSharedPreferences( SHARE_PREF_DETAIL_SUB_CATEGORY, this.getActivity().MODE_PRIVATE );
		getSubCategoryId = myPrefsSubCate.getString( SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_ID, null );
		getSubCategoryName = myPrefsSubCate.getString( SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_NAME, null );
		
		app = (MyApp) this.getActivity().getApplication();
		//appCookie = app.getAppCookie();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		arrayCategoryData = new ArrayList<CategoryData>();
		arrayBrandData = new ArrayList<BrandData>();
		arrayStoreData = new ArrayList<StoreData>();
		arrayGendetData = new ArrayList<GenderData>();
		nameItemListView = (ListView) findViewById( R.id.shareProductSearchLayoutListView );
		productNameItemAdapter = new ProductNameItemAdapter();
		brandNameItemAdapter = new BrandNameItemAdapter();
		storeNameItemAdapter = new StoreNameItemAdapter();
		genderNameItemAdapter = new GenderNameItemAdapter();
		searchEditText = (EditText) findViewById( R.id.shareProductSearchLayoutEditText );
		currentLocation = new GetCurrentLocation( this.getContext() );
		getGender = new GetGender( this.getContext() );
		
		//searchEditText.addTextChangedListener( this );
		
		loadingDialog = new ProgressDialog( this.getContext() );
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading. Please wait...");
		loadingDialog.setIndeterminate( true );
		loadingDialog.setCancelable( true );
		
		String currentLat = currentLocation.getCurrentLat();
		String currentLng = currentLocation.getCurrentLng();
		
		if( searchType.equals( SHARE_PREF_KEY_SEARCH_PRODUCT ) ){
			getDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"categories.json?";
		}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_BRAND ) ){
			getDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"brands.json?";
		}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_STORE ) ){
			getDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"stores.json?currentCoordinate.latitude="+currentLat+"&currentCoordinate.longitude="+currentLng+"&";
		}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_GENDER ) ){
			Map<String, Object> map = getGender.getGenderData();
			for( int i = 1; i<(map.size()+1); i++ ){
				String genderName = (map.get( String.valueOf( i )).toString() );
				GenderData genderData = new GenderData( genderName, String.valueOf( i ) );
				arrayGendetData.add( genderData );
			}
			
			genderNameItemAdapter.setData( arrayGendetData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					nameItemListView.setAdapter( genderNameItemAdapter );
				}
			});
		}
		
		if( getDataURL != null ){
			loadingDialog.show();
			
			asyncHttpClient.get( getDataURL, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject getJsonObject) {
					super.onSuccess(getJsonObject);
					onGetDataURLSuccess(getJsonObject);
				}
			});
		}
	}
	
	private void onGetDataURLSuccess(JSONObject jsonObject){
		if( loadingDialog.isShowing() ){
			loadingDialog.dismiss();
		}
		
		if( searchType.equals( SHARE_PREF_KEY_SEARCH_PRODUCT ) ){
			try {
				JSONArray newArray = jsonObject.getJSONArray( "entities" );
				if( (getSubCategoryId != null) &&
					(getSubCategoryName != null)){
					for(int i = 0; i<newArray.length(); i++){
						//Load Category & SubCategory Data
						if( newArray.optJSONObject( i ) != null ){
							CategoryData categoryData = new CategoryData( newArray.optJSONObject( i ) );
							if( getSubCategoryId.equals( categoryData.getId() ) &&
								getSubCategoryName.equals( categoryData.getName() )){
								JSONArray jsonArray = categoryData.getSubCategories();
								for(int j = 0; j<jsonArray.length(); j++){
									if( jsonArray.optJSONObject( j ) != null ){
										CategoryData newCategoryData = new CategoryData( jsonArray.optJSONObject( j ) );
										arrayCategoryData.add( newCategoryData );
									}
								}
							}
						}
					}
				}else{
					for(int i = 0; i<newArray.length(); i++){
						//Load Category Data
						if( newArray.optJSONObject( i ) != null ){
							CategoryData categoryData = new CategoryData( newArray.optJSONObject( i ) );
							arrayCategoryData.add( categoryData );
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			productNameItemAdapter.setData( arrayCategoryData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					nameItemListView.setAdapter( productNameItemAdapter );
				}
			});
		}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_BRAND ) ){
			try {
				JSONArray newArray = jsonObject.getJSONArray( "entities" );
				for(int i = 0; i<newArray.length(); i++){
					//Load Brand Data
					if( newArray.optJSONObject( i ) != null ){
						BrandData brandData = new BrandData( newArray.optJSONObject( i ) );
						arrayBrandData.add( brandData );
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			brandNameItemAdapter.setData( arrayBrandData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					nameItemListView.setAdapter( brandNameItemAdapter );
				}
			});
		}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_STORE ) ){
			try {
				jsonObject = jsonObject.optJSONObject( "stores" );
				JSONArray newArrayStoreData = jsonObject.getJSONArray( "entities" );
				for(int i = 0; i<newArrayStoreData.length(); i++){
					//Load Store Data
					if( newArrayStoreData.optJSONObject( i ) != null ){
						StoreData newStoreData = new StoreData( newArrayStoreData.optJSONObject( i ) );
						arrayStoreData.add( newStoreData );
					}
				}
				
				JSONArray newArrayExternalStoreData = jsonObject.getJSONArray( "externalStores" );
				for(int i = 0; i<newArrayExternalStoreData.length(); i++){
					//Load External Store Data
					if( newArrayExternalStoreData.optJSONObject( i ) != null ){
						ExternalStoresData externalStoresData = new ExternalStoresData( newArrayExternalStoreData.optJSONObject( i ) );
						JSONArray newArrayExternalStoreDataFetch = externalStoresData.getStores();
						for(int j = 0; j<newArrayExternalStoreDataFetch.length(); j++){
							//Load Store Data
							if( newArrayExternalStoreDataFetch.optJSONObject( j ) != null ){
								StoreData newStoreData = new StoreData( newArrayExternalStoreDataFetch.optJSONObject( j ) );
								arrayStoreData.add( newStoreData );
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			storeNameItemAdapter.setData( arrayStoreData );
			this.getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					nameItemListView.setAdapter( storeNameItemAdapter );
				}
			});
		}
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
	
	private class ProductNameItemAdapter extends BaseAdapter {
		
		private ArrayList<CategoryData> data;
		
		public void setData(ArrayList<CategoryData> arrayCategoryData) {
			data = arrayCategoryData;
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
			
			ShareProductSearchItem returnView;
			
			if( convertView == null ){
				returnView = new ShareProductSearchItem( ShareProductSearchLayout.this.getContext() );
			}else{
				returnView = (ShareProductSearchItem) convertView;
				returnView.setArrowVisibility( View.GONE );
			}
			
			JSONArray jsonItemArray = data.get( position ).getSubCategories();
			if( (jsonItemArray != null) && 
				(jsonItemArray.length() > 0) ){
				returnView.setArrowVisibility( View.VISIBLE );
			}
			
			returnView.setName( data.get( position ).getName() );
			returnView.setCategoryData( data.get( position ) );
			returnView.setOnClickListener( ShareProductSearchLayout.this );
			
			return returnView;
		}
		
	}
	
	private class BrandNameItemAdapter extends BaseAdapter {
		
		private ArrayList<BrandData> data;
		
		public void setData(ArrayList<BrandData> arrayBrandData) {
			data = arrayBrandData;
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
			
			ShareProductSearchItem returnView;
			
			if( convertView == null ){
				returnView = new ShareProductSearchItem( ShareProductSearchLayout.this.getContext() );
			}else{
				returnView = (ShareProductSearchItem) convertView;
			}
			
			returnView.setName( data.get( position ).getName() );
			returnView.setBrandData( data.get( position ) );
			returnView.setOnClickListener( ShareProductSearchLayout.this );
			
			return returnView;
		}
		
	}
	
	private class StoreNameItemAdapter extends BaseAdapter {
		
		private ArrayList<StoreData> data;
		
		public void setData(ArrayList<StoreData> arrayStoreData) {
			data = arrayStoreData;
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
			
			ShareProductSearchItem returnView;
			
			if( convertView == null ){
				returnView = new ShareProductSearchItem( ShareProductSearchLayout.this.getContext() );
			}else{
				returnView = (ShareProductSearchItem) convertView;
			}
			
			returnView.setName( data.get( position ).getName() );
			returnView.setStoreData( data.get( position ) );
			returnView.setOnClickListener( ShareProductSearchLayout.this );
			
			return returnView;
		}
		
	}
	
	private class GenderNameItemAdapter extends BaseAdapter {
		
		private ArrayList<GenderData> data;
		
		public void setData(ArrayList<GenderData> arrayGenderData) {
			data = arrayGenderData;
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
			
			ShareProductSearchItem returnView;
			
			if( convertView == null ){
				returnView = new ShareProductSearchItem( ShareProductSearchLayout.this.getContext() );
			}else{
				returnView = (ShareProductSearchItem) convertView;
			}
			
			returnView.setName( data.get( position ).getName() );
			returnView.setGenderId( String.valueOf( data.get( position ).getId() ) );
			returnView.setOnClickListener( ShareProductSearchLayout.this );
			
			return returnView;
		}
		
	}

	@Override
	public void onClick(View v) {
		if( listener != null ){
			if( v instanceof ShareProductSearchItem ){
				ShareProductSearchItem productSearchItem = (ShareProductSearchItem) v;
				if( searchType.equals( SHARE_PREF_KEY_SEARCH_PRODUCT ) ){
					JSONArray jsonItemArray = productSearchItem.getCategoryData().getSubCategories();
					if( (jsonItemArray != null) && 
						(jsonItemArray.length() > 0) ){
						SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY, this.getActivity().MODE_PRIVATE );
						SharedPreferences.Editor prefsEditor = myPref.edit();
						prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_ID, productSearchItem.getCategoryData().getId() );
						prefsEditor.putString( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_NAME, productSearchItem.getCategoryData().getName() );
				        prefsEditor.commit();
						listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE );
					}else{
						SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareImageDetailLayout.SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
						SharedPreferences.Editor prefsEditor = myPref.edit();
						prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_CATEGORY_NAME, productSearchItem.getCategoryData().getName() );
						prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_CATEGORY_ID, productSearchItem.getCategoryData().getId() );
				        prefsEditor.commit();
				        
				        //Delete subCategory pref.
				        SharedPreferences myPrefSubCategory = this.getActivity().getSharedPreferences( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY, this.getActivity().MODE_PRIVATE );
						SharedPreferences.Editor prefsEditorSubCategory = myPrefSubCategory.edit();
						prefsEditorSubCategory.putString( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_ID, null );
						prefsEditorSubCategory.putString( ShareProductSearchLayout.SHARE_PREF_DETAIL_SUB_CATEGORY_PRODUCT_NAME, null );
						prefsEditorSubCategory.commit();
				        
						listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT );
					}
				}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_BRAND ) ){
					SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareImageDetailLayout.SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
					SharedPreferences.Editor prefsEditor = myPref.edit();
					prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_BRAND_NAME, productSearchItem.getBrandData().getName() );
					//prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_BRAND_ID, productSearchItem.getBrandData().getId() );
			        prefsEditor.commit();
			        
			        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT );
				}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_STORE ) ){
					SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareImageDetailLayout.SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
					SharedPreferences.Editor prefsEditor = myPref.edit();
					prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_NAME, productSearchItem.getStoreData().getName() );
					prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_ID, productSearchItem.getStoreData().getId() );
					if( productSearchItem.getStoreData().getExternalReference() != null ){
						prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_EXTERNAL_ID, productSearchItem.getStoreData().getExternalReference().getId() );
						prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID, productSearchItem.getStoreData().getExternalReference().getType().getId() );
					}
					prefsEditor.commit();
			        
			        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT );
				}else if( searchType.equals( SHARE_PREF_KEY_SEARCH_GENDER ) ){
					SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareImageDetailLayout.SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
					SharedPreferences.Editor prefsEditor = myPref.edit();
					prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_GENDER_ID, productSearchItem.getGenderId() );
			        prefsEditor.commit();
			        
			        listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT );
				}
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if( !(s.equals("")) && 
				s != null  &&
			    (s.length() > 0) ){
				String searchByNameURL = getDataURL+"q="+s;
				searchByNameURL = searchByNameURL.replace( " ", "%20" );
				
				clearArrayData();
				
				//NetworkThreadUtil.getRawDataWithCookie( searchByNameURL, null, appCookie, this );
				asyncHttpClient.get( searchByNameURL, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject getJsonObject) {
						super.onSuccess(getJsonObject);
						onGetDataURLSuccess(getJsonObject);
					}
				});
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

	private void clearArrayData() {
		arrayCategoryData.clear();
		arrayBrandData.clear();
		arrayStoreData.clear();
		arrayGendetData.clear();
	}

}
