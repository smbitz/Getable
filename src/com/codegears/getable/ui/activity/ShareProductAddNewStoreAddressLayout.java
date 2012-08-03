package com.codegears.getable.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class ShareProductAddNewStoreAddressLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_VALUE_ADD_STORE = "SHARE_PREF_VALUE_ADD_STORE";
	public static final String SHARE_PREF_KEY_ADDRESS = "SHARE_PREF_KEY_ADDRESS";
	public static final String SHARE_PREF_KEY_CROSS_STREET = "SHARE_PREF_KEY_CROSS_STREET";
	public static final String SHARE_PREF_KEY_CITY = "SHARE_PREF_KEY_CITY";
	public static final String SHARE_PREF_KEY_STATE = "SHARE_PREF_KEY_STATE";
	public static final String SHARE_PREF_KEY_POST_CODE = "SHARE_PREF_KEY_POST_CODE";
	public static final String SHARE_PREF_KEY_PHONE = "SHARE_PREF_KEY_PHONE";
	public static final String SHARE_PREF_KEY_TWITTER = "SHARE_PREF_KEY_TWITTER";
	public static final String SHARE_PREF_KEY_MAP_LAT = "SHARE_PREF_KEY_MAP_LAT";
	public static final String SHARE_PREF_KEY_MAP_LONG = "SHARE_PREF_KEY_MAP_LONG";
	
	private ImageButton backButton;
	private Button nextButton;
	private TextView addressText;
	private TextView crossStreetText;
	private TextView cityText;
	private TextView stateText;
	private TextView postCodeText;
	private TextView phoneText;
	private TextView twitterText;
	private EditText addressEditText;
	private EditText crossStreetEditText;
	private EditText cityEditText;
	private EditText stateEditText;
	private EditText postCodeEditText;
	private EditText phoneEditText;
	private EditText twitterEditText;
	private BodyLayoutStackListener listener;

	public ShareProductAddNewStoreAddressLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareproductaddnewstoreaddresslayout, this );
		
		SharedPreferences myPref = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
		String addressValue = myPref.getString( SHARE_PREF_KEY_ADDRESS, "" );
		String crossStreetValue = myPref.getString( SHARE_PREF_KEY_CROSS_STREET, "" );
		String cityValue = myPref.getString( SHARE_PREF_KEY_CITY, "" );
		String stateValue = myPref.getString( SHARE_PREF_KEY_STATE, "" );
		String postCodeValue = myPref.getString( SHARE_PREF_KEY_POST_CODE, "" );
		String phoneValue = myPref.getString( SHARE_PREF_KEY_PHONE, "" );
		String twitterValue = myPref.getString( SHARE_PREF_KEY_TWITTER, "" );
		
		backButton = (ImageButton) findViewById( R.id.shareProductAddNewStoreAddressBackButton );
		nextButton = (Button) findViewById( R.id.shareProductAddNewStoreAddressNextButton );
		addressText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressText );
		crossStreetText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressCrossStreetText );
		cityText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressCityText );
		stateText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressStateText );
		postCodeText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressPostCodeText );
		phoneText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressPhoneText );
		twitterText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressTwitterText );
		addressEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddress );
		crossStreetEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddressCrossStreet );
		cityEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddressCity );
		stateEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddressState );
		postCodeEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddressPostCode );
		phoneEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddressPhone );
		twitterEditText = (EditText) findViewById( R.id.shareProductAddNewStoreAddressTwitter );
		
		nextButton.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		addressText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		crossStreetText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		cityText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		stateText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		postCodeText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		phoneText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		twitterText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		addressEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		crossStreetEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		cityEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		stateEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		postCodeEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		phoneEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		twitterEditText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		
		addressEditText.setText( addressValue );
		crossStreetEditText.setText( crossStreetValue );
		cityEditText.setText( cityValue );
		stateEditText.setText( stateValue );
		postCodeEditText.setText( postCodeValue );
		phoneEditText.setText( phoneValue );
		twitterEditText.setText( twitterValue );
		
		backButton.setOnClickListener( this );
		nextButton.setOnClickListener( this );
		
		nextButton.setVisibility( View.INVISIBLE );
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
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
	public void onClick(View v) {
		if( v.equals( backButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( addressEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( crossStreetEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( cityEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( stateEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( postCodeEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( phoneEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( twitterEditText.getWindowToken(), 0 );
			
			SharedPreferences myPref = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			prefsEditor.putString( SHARE_PREF_KEY_ADDRESS, addressEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_CROSS_STREET, crossStreetEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_CITY, cityEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_STATE, stateEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_POST_CODE, postCodeEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_PHONE, phoneEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_TWITTER, twitterEditText.getText().toString() );
	        prefsEditor.commit();
			
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
		}else if( v.equals( nextButton ) ){
			InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow( addressEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( crossStreetEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( cityEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( stateEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( postCodeEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( phoneEditText.getWindowToken(), 0 );
			imm.hideSoftInputFromWindow( twitterEditText.getWindowToken(), 0 );
			
			SharedPreferences myPref = this.getActivity().getSharedPreferences( SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			prefsEditor.putString( SHARE_PREF_KEY_ADDRESS, addressEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_CROSS_STREET, crossStreetEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_CITY, cityEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_STATE, stateEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_POST_CODE, postCodeEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_PHONE, phoneEditText.getText().toString() );
			prefsEditor.putString( SHARE_PREF_KEY_TWITTER, twitterEditText.getText().toString() );
	        prefsEditor.commit();
			
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_ADD_NEW_STORE_ADDRESS_MAP );
		}
	}

}
