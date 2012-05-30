package com.codegears.getable;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.util.Config;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class NearByFilterActivity extends Activity implements TextWatcher, NetworkThreadListener {
	
	private Config config;
	private AutoCompleteTextView brandStoreEditText;
	private String getBrandStoreDataURL;
	private MyApp app;
	private List<String> appCookie;
	private ArrayList<String> arrayDataBrandStoreName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.nearbyfilteractivity );
		
		config = new Config( this );
		app = (MyApp) this.getApplication();
		appCookie = app.getAppCookie();
		arrayDataBrandStoreName = new ArrayList<String>();
		
		brandStoreEditText = (AutoCompleteTextView) findViewById( R.id.nearByFilterEditText );
		brandStoreEditText.addTextChangedListener( this );
		
		getBrandStoreDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/suggest/brand-store.json?page.size=20";
	}

	@Override
	public void afterTextChanged( Editable s ) {
		clearDataAndStatus();
		
		String tempURL = getBrandStoreDataURL+"&q="+s;
		
		NetworkThreadUtil.getRawDataWithCookie( tempURL, null, appCookie, this );
	}

	@Override
	public void beforeTextChanged( CharSequence s, int start, int count,	int after ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged( CharSequence s, int start, int before, int count ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		try {
			JSONObject jsonObject = new JSONObject( result );
			JSONArray jsonArray = jsonObject.getJSONArray("values");
			for(int i = 0; i<jsonArray.length(); i++){
				//Load Brand/Store Data
				String name = jsonArray.optString( i );
				arrayDataBrandStoreName.add( name );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.nearbyautocompleteitem, arrayDataBrandStoreName);
		this.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				brandStoreEditText.setAdapter( adapter );
				brandStoreEditText.showDropDown();
			}
		});
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
	
	private void clearDataAndStatus() {
		arrayDataBrandStoreName.clear();
	}
	
}
