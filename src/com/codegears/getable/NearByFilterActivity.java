package com.codegears.getable;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.codegears.getable.util.Config;
import com.codegears.getable.util.RangeSeekBar;
import com.codegears.getable.util.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.codegears.getable.util.compoundbuttongroup.CompoundButtonGroup;
import com.codegears.getable.util.compoundbuttongroup.CompoundButtonGroupListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NearByFilterActivity extends Activity implements TextWatcher, CompoundButtonGroupListener, OnClickListener, OnRangeSeekBarChangeListener<Integer>, OnItemClickListener {
	
	public static final String PUT_EXTRA_URL_VAR_1 = "PUT_EXTRA_URL_VAR_1";
	public static final String PUT_EXTRA_URL_VAR_2 = "PUT_EXTRA_URL_VAR_2";
	public static final String PUT_EXTRA_URL_VAR_3 = "PUT_EXTRA_URL_VAR_3";
	public static final String PUT_EXTRA_URL_VAR_4 = "PUT_EXTRA_URL_VAR_4";
	
	private static final String DISTANCE_VALUE_1 = "0.5";
	private static final String DISTANCE_VALUE_2 = "1";
	private static final String DISTANCE_VALUE_3 = "10";
	private static final String DISTANCE_VALUE_4 = "25";
	
	public static int PRICE_MINIMUM = 0;
	public static int PRICE_MAXIMUM = 1000;
	public static String PRICE_MAXIMUM_TEXT = "Unlimited";
	
	private Config config;
	private AutoCompleteTextView brandStoreEditText;
	private String getBrandStoreDataURL;
	private MyApp app;
	//private List<String> appCookie;
	private ArrayList<String> arrayDataBrandStoreName;
	private ToggleButton distanceButton1;
	private ToggleButton distanceButton2;
	private ToggleButton distanceButton3;
	private ToggleButton distanceButton4;
	private CompoundButtonGroup distanceButtonGroup;
	private Button doneButton;
	private String resultTxt1;
	private String resultTxt2;
	private String resultTxt3;
	private String resultTxt4;
	private LinearLayout priceRangeSeekBarLayout;
	private TextView priceMinimumText;
	private TextView priceMaximumText;
	private TextView dollarSignPriceMaximum;
	private AsyncHttpClient asyncHttpClient;
	private Boolean showDropDownState;
	private String tempEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.nearbyfilteractivity );
		
		config = new Config( this );
		app = (MyApp) this.getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		//appCookie = app.getAppCookie();
		arrayDataBrandStoreName = new ArrayList<String>();
		distanceButtonGroup = new CompoundButtonGroup( false );
		showDropDownState = true;
		
		brandStoreEditText = (AutoCompleteTextView) findViewById( R.id.nearByFilterEditText );
		distanceButton1 = (ToggleButton) findViewById( R.id.nearbyFilterDistanceButton1 );
		distanceButton2 = (ToggleButton) findViewById( R.id.nearbyFilterDistanceButton2 );
		distanceButton3 = (ToggleButton) findViewById( R.id.nearbyFilterDistanceButton3 );
		distanceButton4 = (ToggleButton) findViewById( R.id.nearbyFilterDistanceButton4 );
		doneButton = (Button) findViewById( R.id.nearByFilterDoneButton );
		priceRangeSeekBarLayout = (LinearLayout) findViewById( R.id.nearByFilterPriceRangeSeekBarLayout );
		priceMinimumText = (TextView) findViewById( R.id.nearByFilterPriceMinimumText );
		priceMaximumText = (TextView) findViewById( R.id.nearByFilterPriceMaximumText );
		dollarSignPriceMaximum = (TextView) findViewById( R.id.nearByFilterDollarSignPriceMaximum );
		
		//Set font
		brandStoreEditText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		priceMinimumText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		priceMaximumText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		dollarSignPriceMaximum.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		
		brandStoreEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
		          return true;
		        }
		        return false;
			}
		});
		
		// create RangeSeekBar as Integer range between 20 and 75
		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(PRICE_MINIMUM, PRICE_MAXIMUM, this);
		
		priceMinimumText.setText( String.valueOf( PRICE_MINIMUM ) );
		priceMaximumText.setText( PRICE_MAXIMUM_TEXT );
		dollarSignPriceMaximum.setVisibility( View.INVISIBLE );
		
		distanceButton1.setTextOn("");
		distanceButton1.setTextOff("");
		distanceButton2.setTextOn("");
		distanceButton2.setTextOff("");
		distanceButton3.setTextOn("");
		distanceButton3.setTextOff("");
		distanceButton4.setTextOn("");
		distanceButton4.setTextOff("");
		
		distanceButton1.setBackgroundResource( R.drawable.nearby_filter_mi_1_button );
		distanceButton2.setBackgroundResource( R.drawable.nearby_filter_mi_2_button );
		distanceButton3.setBackgroundResource( R.drawable.nearby_filter_mi_3_button );
		distanceButton4.setBackgroundResource( R.drawable.nearby_filter_mi_4_button );
		
		priceRangeSeekBarLayout.addView( seekBar );
		
		distanceButtonGroup.addButton( distanceButton1 );
		distanceButtonGroup.addButton( distanceButton2 );
		distanceButtonGroup.addButton( distanceButton3 );
		distanceButtonGroup.addButton( distanceButton4 );
		
		brandStoreEditText.addTextChangedListener( this );
		distanceButtonGroup.setCompoundButtonGroupListener( this );
		doneButton.setOnClickListener( this );
		seekBar.setOnRangeSeekBarChangeListener( this );
		seekBar.setNotifyWhileDragging( true );
		brandStoreEditText.setOnItemClickListener( this );
		
		resultTxt1 = "";
		resultTxt2 = "";
		resultTxt3 = "";
		resultTxt4 = "";
		
		getBrandStoreDataURL = config.get( MyApp.URL_DEFAULT ).toString()+"activities/suggest/brand-store.json?page.size=20";
	}

	@Override
	public void afterTextChanged( Editable s ) {
		System.out.println("CheckSValue : "+s.toString());
		System.out.println("CheckTempEditText : "+tempEditText);
		if( !(s.toString().equals( tempEditText )) ){
			clearDataAndStatus();
			
			String tempURL = getBrandStoreDataURL+"&q="+s;
			tempURL = tempURL.replace( " ", "%20" );
			
			if( showDropDownState ){
				System.out.println("EditTextShowDropDown");
				//NetworkThreadUtil.getRawDataWithCookie( tempURL, null, appCookie, this );
				asyncHttpClient.post(
					tempURL, 
					new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject jsonObject) {
							super.onSuccess(jsonObject);
							try {
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
							
							final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NearByFilterActivity.this, R.layout.nearbyautocompleteitem, arrayDataBrandStoreName);
							brandStoreEditText.setAdapter( adapter );
							brandStoreEditText.showDropDown();
						}
				});
			}else{
				System.out.println("EditTextHideDropDown");
				brandStoreEditText.dismissDropDown();
				showDropDownState = true;
			}
			
			tempEditText = s.toString();
		}
	}

	@Override
	public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged( CharSequence s, int start, int before, int count ) {
		// TODO Auto-generated method stub
		
	}
	
	private void clearDataAndStatus() {
		arrayDataBrandStoreName.clear();
	}

	@Override
	public void onButtonGroupClick(CompoundButton cButton) {
		if( cButton.equals( distanceButton1 ) ){
			resultTxt2 = DISTANCE_VALUE_1;
		}else if( cButton.equals( distanceButton2 ) ){
			resultTxt2 = DISTANCE_VALUE_2;
		}else if( cButton.equals( distanceButton3 ) ){
			resultTxt2 = DISTANCE_VALUE_3;
		}else if( cButton.equals( distanceButton4 ) ){
			resultTxt2 = DISTANCE_VALUE_4;
		}
	}

	@Override
	public void onClick(View v) {
		if( v.equals( doneButton ) ){
			resultTxt1 = brandStoreEditText.getText().toString();
			
			Intent intent = new Intent();
			intent.putExtra( PUT_EXTRA_URL_VAR_1, resultTxt1 );
			intent.putExtra( PUT_EXTRA_URL_VAR_2, resultTxt2 );
			intent.putExtra( PUT_EXTRA_URL_VAR_3, resultTxt3 );
			intent.putExtra( PUT_EXTRA_URL_VAR_4, resultTxt4 );
			this.setResult( MainActivity.RESULT_NEARBY_FILTER_FINISH, intent );
			this.finish();
		}
	}

	@Override
	public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
			final Integer minValue, final Integer maxValue) {
		this.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				priceMinimumText.setText( String.valueOf( minValue ) );
		        
		        if( maxValue == PRICE_MAXIMUM ){
		        	dollarSignPriceMaximum.setVisibility( View.INVISIBLE );
		        	priceMaximumText.setText( PRICE_MAXIMUM_TEXT );
		        }else{
		        	dollarSignPriceMaximum.setVisibility( View.VISIBLE );
		        	priceMaximumText.setText( String.valueOf( maxValue ) );
		        }
		        
		        resultTxt3 = String.valueOf( minValue );
		        resultTxt4 = String.valueOf( maxValue );
			}
		});
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			this.setResult( MainActivity.RESULT_NEARBY_FILTER_BACK, intent );
			this.finish();
			
    		return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showDropDownState = false;
		System.out.println("OnItemClickHideDropDown!!");
	}
	
}
