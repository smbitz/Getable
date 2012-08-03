package com.codegears.getable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codegears.getable.ui.activity.ProfileFindFriendsListLayout;

public class FindFriendActivity extends Activity implements OnClickListener {
	
	private LinearLayout findFriendFromContact;
	private LinearLayout findFriendFromFacebook;
	private LinearLayout findFriendFromTwitter;
	private LinearLayout findFriendFromSearch;
	private LinearLayout findFriendFromSuggest;
	private ImageButton backButton;
	private TextView findFriendContactText;
	private TextView findFriendFacebookText;
	private TextView findFriendTwitterText;
	private TextView findFriendSearchText;
	private TextView findFriendSuggestText;
	private Button skipButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.profilefindfriendslayout );
		
		findFriendFromContact = (LinearLayout) findViewById( R.id.profileFindFriendsFromContact );
		findFriendFromFacebook = (LinearLayout) findViewById( R.id.profileFindFriendsFromFacebook );
		findFriendFromTwitter = (LinearLayout) findViewById( R.id.profileFindFriendsFromTwitter );
		findFriendFromSearch = (LinearLayout) findViewById( R.id.profileFindFriendsFromSearch );
		findFriendFromSuggest = (LinearLayout) findViewById( R.id.profileFindFriendsFromSuggest );
		backButton = (ImageButton) findViewById( R.id.profileFindFriendsLayoutBackButton );
		skipButton = (Button) findViewById( R.id.profileFindFriendsLayoutSkipButton );
		findFriendContactText = (TextView) findViewById( R.id.profileFindFriendsFromContactText );
		findFriendFacebookText = (TextView) findViewById( R.id.profileFindFriendsFromFacebookText );
		findFriendTwitterText = (TextView) findViewById( R.id.profileFindFriendsFromTwitterText );
		findFriendSearchText = (TextView) findViewById( R.id.profileFindFriendsFromSearchText );
		findFriendSuggestText = (TextView) findViewById( R.id.profileFindFriendsFromSuggestText );
		
		//Set font
		findFriendContactText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		findFriendFacebookText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		findFriendTwitterText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		findFriendSearchText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		findFriendSuggestText.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH) );
		skipButton.setTypeface( Typeface.createFromAsset( this.getAssets(), MyApp.APP_FONT_PATH_2) );
		
		findFriendFromContact.setOnClickListener( this );
		findFriendFromFacebook.setOnClickListener( this );
		findFriendFromTwitter.setOnClickListener( this );
		findFriendFromSearch.setOnClickListener( this );
		findFriendFromSuggest.setOnClickListener( this );
		backButton.setOnClickListener( this );
		skipButton.setOnClickListener( this );
		
		backButton.setVisibility( View.INVISIBLE );
		skipButton.setVisibility( View.VISIBLE );
	}

	@Override
	public void onClick(View v) {
		if( v.equals( findFriendFromContact ) ){
			Intent intent = new Intent( this, FindFriendListActivity.class );
			intent.putExtra( FindFriendListActivity.PUT_EXTRA_FIND_FRIENDS_ID, ProfileFindFriendsListLayout.FIND_FROM_CONTACT );
			this.startActivity( intent );
		}else if( v.equals( findFriendFromFacebook ) ){
			Intent intent = new Intent( this, FindFriendListActivity.class );
			intent.putExtra( FindFriendListActivity.PUT_EXTRA_FIND_FRIENDS_ID, ProfileFindFriendsListLayout.FIND_FROM_FACEBOOK );
			this.startActivity( intent );
		}else if( v.equals( findFriendFromTwitter ) ){
			Intent intent = new Intent( this, FindFriendListActivity.class );
			intent.putExtra( FindFriendListActivity.PUT_EXTRA_FIND_FRIENDS_ID, ProfileFindFriendsListLayout.FIND_FROM_TWITTER );
			this.startActivity( intent );
		}else if( v.equals( findFriendFromSearch ) ){
			Intent intent = new Intent( this, FindFriendListActivity.class );
			intent.putExtra( FindFriendListActivity.PUT_EXTRA_FIND_FRIENDS_ID, ProfileFindFriendsListLayout.FIND_FROM_SEARCH );
			this.startActivity( intent );
		}else if( v.equals( findFriendFromSuggest ) ){
			Intent intent = new Intent( this, FindFriendListActivity.class );
			intent.putExtra( FindFriendListActivity.PUT_EXTRA_FIND_FRIENDS_ID, ProfileFindFriendsListLayout.FIND_FROM_SUGGEST );
			this.startActivity( intent );
		}else if( v.equals( skipButton ) ){
			Intent intent = new Intent( this, MainActivity.class );
			this.startActivity( intent );
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
