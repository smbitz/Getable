package com.codegears.getable;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.data.MetroData;
import com.codegears.getable.data.ProductActivityData;
import com.codegears.getable.ui.MetroListTextView;
import com.codegears.getable.util.Config;
import com.codegears.getable.util.GetCurrentLocation;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.compoundbuttongroup.CompoundButtonGroup;
import com.codegears.getable.util.compoundbuttongroup.CompoundButtonGroupListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GalleryFilterActivity extends Activity implements CompoundButtonGroupListener, OnClickListener, NetworkThreadListener {
	
	private static final String URL_VAR_LOCAL_TEXT_LAT = "&currentCoordinate.latitude=";
	private static final String URL_VAR_LOCAL_TEXT_LNG = "&currentCoordinate.longitude=";
	private static final String URL_VAR_LOCAL_TEXT_METOR = "&metro=";
	private static final String URL_VAR_LOCAL_TEXT_POPULAR = "&sort.properties[0].name=statistic.score.active&sort.properties[0].reverse=true&sort.properties[1].name=statistic.score.allTime&sort.properties[1].reverse=true";
	private static final String URL_VAR_LOCAL_TEXT_RECENT = "&sort.properties[0].name=activityTime&sort.properties[0].reverse=true";
	private static final String URL_GET_METROS = "URL_GET_METROS";
	public static final String PUT_EXTRA_URL_VAR_1 = "PUT_EXTRA_URL_VAR_1";
	public static final String PUT_EXTRA_URL_VAR_2 = "PUT_EXTRA_URL_VAR_2";
	public static final String PUT_EXTRA_FILTER_1 = "PUT_EXTRA_FILTER_1";
	public static final String PUT_EXTRA_FILTER_2 = "PUT_EXTRA_FILTER_2";
	private static final String SHOW_TEXT_FILTER_1_1 = "LOCAL";
	private static final String SHOW_TEXT_FILTER_1_2 = "ALL";
	private static final String SHOW_TEXT_FILTER_2_1 = "POPULAR";
	private static final String SHOW_TEXT_FILTER_2_2 = "RECENT";
	private static final String DEFAULT_SHOW_TEXT_FILTER_1 = SHOW_TEXT_FILTER_1_1;
	private static final String DEFAULT_SHOW_TEXT_FILTER_2 = SHOW_TEXT_FILTER_2_1;
	
	private CompoundButtonGroup locationButtonGroup;
	private CompoundButtonGroup sortButtonGroup;
	private ToggleButton localButton;
	private ToggleButton allButton;
	private ToggleButton cityButton;
	private ToggleButton popularButton;
	private ToggleButton recentButton;
	private Button closeButton;
	private String resultTxt1;
	private String resultTxt2;
	private String currentLat;
	private String currentLng;
	private GetCurrentLocation getCurrentLocation;
	private ListView metroList;
	private MetroAdapter metroAdapter;
	private Config config;
	private ArrayList<MetroData> arrayMetroName;
	private LinearLayout metroLayout;
	private String default_resultTxt1;
	private String default_resultTxt2;
	private String showText1;
	private String showText2;
	private MetroListTextView metroListTextViewTemp;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.galleryfilterlayout);
      
      app = (MyApp) this.getApplication();
      asyncHttpClient = app.getAsyncHttpClient();
      
      locationButtonGroup = new CompoundButtonGroup();
      sortButtonGroup = new CompoundButtonGroup();
      closeButton = (Button) findViewById( R.id.galleryFilterCloseButton );
      
      locationButtonGroup.setCompoundButtonGroupListener( this );
      sortButtonGroup.setCompoundButtonGroupListener( this );
      closeButton.setOnClickListener( this );
      
      localButton = (ToggleButton) findViewById( R.id.galleryFilterLocalButton );
      allButton = (ToggleButton) findViewById( R.id.galleryFilterAllButton );
      cityButton = (ToggleButton) findViewById( R.id.galleryFilterCityButton );
      popularButton = (ToggleButton) findViewById( R.id.galleryFilterPopularButton );
      recentButton = (ToggleButton) findViewById( R.id.galleryFilterRecentButton );
      
      localButton.setBackgroundResource( R.drawable.gallery_filter_local_button );
      allButton.setBackgroundResource( R.drawable.gallery_filter_all_button );
      cityButton.setBackgroundResource( R.drawable.gallery_filter_city_button );
      popularButton.setBackgroundResource( R.drawable.gallery_filter_popular_button );
      recentButton.setBackgroundResource( R.drawable.gallery_filter_recent_button );
      
      locationButtonGroup.addButton( localButton );
      locationButtonGroup.addButton( allButton );
      locationButtonGroup.addButton( cityButton );
      sortButtonGroup.addButton( popularButton );
      sortButtonGroup.addButton( recentButton );
      
      getCurrentLocation = new GetCurrentLocation( this );
      
      currentLat = getCurrentLocation.getCurrentLat();
      currentLng = getCurrentLocation.getCurrentLng();
      
      config = new Config( this );
      arrayMetroName = new ArrayList<MetroData>();
      metroLayout = (LinearLayout) findViewById( R.id.galleryFilterMetroLayout );
      default_resultTxt1 = URL_VAR_LOCAL_TEXT_LAT+currentLat+URL_VAR_LOCAL_TEXT_LNG+currentLng;
      default_resultTxt2 = URL_VAR_LOCAL_TEXT_POPULAR;
      resultTxt1 = default_resultTxt1;
      resultTxt2 = default_resultTxt2;
      
      showText1 = DEFAULT_SHOW_TEXT_FILTER_1;
      showText2 = DEFAULT_SHOW_TEXT_FILTER_2;
      
      metroList = (ListView) findViewById( R.id.galleryFilterMetrosList );
      metroAdapter = new MetroAdapter();
      //NetworkThreadUtil.getRawData( config.get( URL_GET_METROS ).toString(), null, this );
      
      asyncHttpClient.post( 
    	config.get( URL_GET_METROS ).toString(),
    	new JsonHttpResponseHandler(){
    		@Override
    		public void onSuccess(JSONObject jsonObject) {
    			super.onSuccess(jsonObject);
    			try {
    				JSONArray newArray = jsonObject.getJSONArray("entities");
    				//Load Metro Data
    				for(int i = 0; i<newArray.length(); i++){
    					MetroData newData = new MetroData( (JSONObject) newArray.get(i) );
    					arrayMetroName.add( newData );
    				}
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			metroAdapter.setData( arrayMetroName );
    			metroList.setAdapter( metroAdapter );
    		}
    	});
    }

	@Override
	public void onButtonGroupClick(CompoundButton compoundButton) {
		if( compoundButton.equals( localButton ) ){
			metroLayout.setVisibility( View.GONE );
			resultTxt1 = URL_VAR_LOCAL_TEXT_LAT+currentLat+URL_VAR_LOCAL_TEXT_LNG+currentLng;
			showText1 = SHOW_TEXT_FILTER_1_1;
		}else if( compoundButton.equals( allButton ) ){
			metroLayout.setVisibility( View.GONE );
			resultTxt1 = "";
			showText1 = SHOW_TEXT_FILTER_1_2;
		}else if( compoundButton.equals( cityButton ) ){
			metroLayout.setVisibility( View.VISIBLE );
			resultTxt1 = URL_VAR_LOCAL_TEXT_METOR+arrayMetroName.get(0).getId();
			showText1 = arrayMetroName.get(0).getName();
		}else if( compoundButton.equals( popularButton ) ){
			resultTxt2 = URL_VAR_LOCAL_TEXT_POPULAR;
			showText2 = SHOW_TEXT_FILTER_2_1;
		}else if( compoundButton.equals( recentButton ) ){
			resultTxt1 = URL_VAR_LOCAL_TEXT_RECENT;
			showText2 = SHOW_TEXT_FILTER_2_2;
		}
	}

	@Override
	public void onClick(View v) {
		if( v.equals( closeButton ) ){
			Intent intent = new Intent();
			intent.putExtra( PUT_EXTRA_URL_VAR_1, resultTxt1 );
			intent.putExtra(PUT_EXTRA_URL_VAR_2, resultTxt2 );
			intent.putExtra( PUT_EXTRA_FILTER_1, showText1 );
			intent.putExtra(PUT_EXTRA_FILTER_2, showText2 );
			this.setResult( MainActivity.RESULT_GALLERY_FILTER_FINISH, intent );
			this.finish();
		}else if( v instanceof MetroListTextView ){
			if( metroListTextViewTemp != null ){
				metroListTextViewTemp.setItemNormal();
			}
			
			MetroListTextView metroData = (MetroListTextView) v;
			metroData.setItemSelected();
			resultTxt1 = URL_VAR_LOCAL_TEXT_METOR+metroData.getData().getId();
			showText1 = metroData.getData().getName();
			
			metroListTextViewTemp = metroData;
		}
	}
	
	private class MetroAdapter extends BaseAdapter{
		
		private ArrayList<MetroData> data;
		
		public void setData( ArrayList<MetroData> setData ){
			data = setData;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get( position );
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			MetroListTextView resultView = null;
			if( convertView == null ){
				MetroListTextView newTextView = new MetroListTextView( GalleryFilterActivity.this );
				resultView = newTextView;
			}else{
				resultView = (MetroListTextView) convertView;
			}
			
			resultView.setData( data.get( position ) );
			resultView.setText( data.get( position ).getName() );
			resultView.setOnClickListener( GalleryFilterActivity.this );
			return resultView;
		}
		
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		// TODO Auto-generated method stub
		/*try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray newArray = jsonObject.getJSONArray("entities");
			//Load Metro Data
			for(int i = 0; i<newArray.length(); i++){
				MetroData newData = new MetroData( (JSONObject) newArray.get(i) );
				arrayMetroName.add( newData );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		metroAdapter.setData( arrayMetroName );
		this.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				 metroList.setAdapter( metroAdapter );
			}
		});*/
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			this.setResult( MainActivity.RESULT_GALLERY_FILTER_BACK, intent );
			this.finish();
			
    		return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
}
