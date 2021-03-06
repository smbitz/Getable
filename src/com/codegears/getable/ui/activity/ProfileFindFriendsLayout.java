package com.codegears.getable.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import android.view.View.OnClickListener;

public class ProfileFindFriendsLayout extends AbstractViewLayout implements OnClickListener {
	
	private BodyLayoutStackListener listener;
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
	
	public ProfileFindFriendsLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.profilefindfriendslayout, this );
		
		findFriendFromContact = (LinearLayout) findViewById( R.id.profileFindFriendsFromContact );
		findFriendFromFacebook = (LinearLayout) findViewById( R.id.profileFindFriendsFromFacebook );
		findFriendFromTwitter = (LinearLayout) findViewById( R.id.profileFindFriendsFromTwitter );
		findFriendFromSearch = (LinearLayout) findViewById( R.id.profileFindFriendsFromSearch );
		findFriendFromSuggest = (LinearLayout) findViewById( R.id.profileFindFriendsFromSuggest );
		backButton = (ImageButton) findViewById( R.id.profileFindFriendsLayoutBackButton );
		findFriendContactText = (TextView) findViewById( R.id.profileFindFriendsFromContactText );
		findFriendFacebookText = (TextView) findViewById( R.id.profileFindFriendsFromFacebookText );
		findFriendTwitterText = (TextView) findViewById( R.id.profileFindFriendsFromTwitterText );
		findFriendSearchText = (TextView) findViewById( R.id.profileFindFriendsFromSearchText );
		findFriendSuggestText = (TextView) findViewById( R.id.profileFindFriendsFromSuggestText );
		
		//Set font
		findFriendContactText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		findFriendFacebookText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		findFriendTwitterText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		findFriendSearchText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		findFriendSuggestText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
		
		findFriendFromContact.setOnClickListener( this );
		findFriendFromFacebook.setOnClickListener( this );
		findFriendFromTwitter.setOnClickListener( this );
		findFriendFromSearch.setOnClickListener( this );
		findFriendFromSuggest.setOnClickListener( this );
		backButton.setOnClickListener( this );
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
	public void onClick(View v) {
		if( v.equals( findFriendFromContact ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileFindFriendsListLayout.SHARE_PREF_FIND_FRIENDS_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putInt( ProfileFindFriendsListLayout.FIND_FRIENDS_FROM_VALUE_ID, ProfileFindFriendsListLayout.FIND_FROM_CONTACT );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT );
			}
		}else if( v.equals( findFriendFromFacebook ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileFindFriendsListLayout.SHARE_PREF_FIND_FRIENDS_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putInt( ProfileFindFriendsListLayout.FIND_FRIENDS_FROM_VALUE_ID, ProfileFindFriendsListLayout.FIND_FROM_FACEBOOK );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT );
			}
		}else if( v.equals( findFriendFromTwitter ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileFindFriendsListLayout.SHARE_PREF_FIND_FRIENDS_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putInt( ProfileFindFriendsListLayout.FIND_FRIENDS_FROM_VALUE_ID, ProfileFindFriendsListLayout.FIND_FROM_TWITTER );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT );
			}
		}else if( v.equals( findFriendFromSearch ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileFindFriendsListLayout.SHARE_PREF_FIND_FRIENDS_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putInt( ProfileFindFriendsListLayout.FIND_FRIENDS_FROM_VALUE_ID, ProfileFindFriendsListLayout.FIND_FROM_SEARCH );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT );
			}
		}else if( v.equals( findFriendFromSuggest ) ){
			if(listener != null){
				SharedPreferences myPreferences = this.getActivity().getSharedPreferences( ProfileFindFriendsListLayout.SHARE_PREF_FIND_FRIENDS_ID, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putInt( ProfileFindFriendsListLayout.FIND_FRIENDS_FROM_VALUE_ID, ProfileFindFriendsListLayout.FIND_FROM_SUGGEST );
				prefsEditor.commit();
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT );
			}
		}else if( v.equals( backButton ) ){
			if(listener != null){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
			}
		}
	}

}
