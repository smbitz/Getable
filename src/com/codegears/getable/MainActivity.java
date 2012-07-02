package com.codegears.getable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Document;

import com.codegears.getable.ui.BadgeLayout;
import com.codegears.getable.ui.ProductNumComment;
import com.codegears.getable.ui.activity.BrandFeedLayout;
import com.codegears.getable.ui.activity.GalleryLayout;
import com.codegears.getable.ui.activity.MyFeedLayout;
import com.codegears.getable.ui.activity.NearbyLayout;
import com.codegears.getable.ui.activity.ProductCommentLayout;
import com.codegears.getable.ui.activity.ProductDetailLayout;
import com.codegears.getable.ui.activity.ProductLikeLayout;
import com.codegears.getable.ui.activity.ProductSetupSocialShareLayout;
import com.codegears.getable.ui.activity.ProductSocialShareLayout;
import com.codegears.getable.ui.activity.ProductSocialShareListLayout;
import com.codegears.getable.ui.activity.ProductWishlistLayout;
import com.codegears.getable.ui.activity.ProfileChangePasswordLayout;
import com.codegears.getable.ui.activity.ProfileChangeSettingsLayout;
import com.codegears.getable.ui.activity.ProfileEditProfile;
import com.codegears.getable.ui.activity.ProfileFindFriendsLayout;
import com.codegears.getable.ui.activity.ProfileFindFriendsListLayout;
import com.codegears.getable.ui.activity.ProfileLayout;
import com.codegears.getable.ui.activity.ProfileLayoutWebView;
import com.codegears.getable.ui.activity.ShareImageCropLayout;
import com.codegears.getable.ui.activity.ShareImageDetailLayout;
import com.codegears.getable.ui.activity.ShareProductSearchLayout;
import com.codegears.getable.ui.activity.StoreMainLayout;
import com.codegears.getable.ui.activity.UserProfileLayout;
import com.codegears.getable.ui.activity.WishlistsGalleryLayout;
import com.codegears.getable.ui.tabbar.TabBar;
import com.codegears.getable.ui.tabbar.TabBarListener;
import com.facebook.android.Facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements BodyLayoutStackListener, TabBarListener {
	
	public static final int REQUEST_GALLERY_FILTER = 1;
	public static final int REQUEST_WISHLISTS_FILTER = 2;
	public static final int REQUEST_PHOTO_OPTION = 3;
	public static final int REQUEST_NEARBY_FILTER = 4;
	private static final int REQUEST_SHARE_TAKE_IMAGE = 5;
	private static final int REQUEST_SHARE_CHOOSE_IMAGE = 6;
	public static final int REQUEST_INVITE_FRIENDS_SELECT_CONTACT = 7;
	public static final int REQUEST_USER_CAHNGE_IMAGE = 8;
	
	public static final int LAYOUTCHANGE_PRODUCTDETAIL = 1;
	public static final int LAYOUTCHANGE_USERPROFILE = 2;
	public static final int LAYOUTCHANGE_BADGE = 3;
	public static final int LAYOUTCHANGE_WISHLISTS_GALLERY = 4;
	public static final int LAYOUTCHANGE_BRAND_FEED = 5;
	public static final int LAYOUTCHANGE_STORE_MAIN = 6;
	public static final int LAYOUTCHANGE_PRODUCT_LIKE_USER_LIST = 7;
	public static final int LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST = 8;
	public static final int LAYOUTCHANGE_PRODUCT_WISHLIST = 9;
	public static final int LAYOUTCHANGE_SHARE_IMAGE_DETAIL = 10;
	public static final int LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE = 11;
	public static final int LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT = 12;
	public static final int LAYOUTCHANGE_FIND_FRIENDS = 13;
	public static final int LAYOUTCHANGE_EDIT_PROFILE = 14;
	public static final int LAYOUTCHANGE_PROFILE_LAYOUT_BACK = 15;
	public static final int LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT = 16;
	public static final int LAYOUTCHANGE_PROFILE_WEBVIEW = 17;
	public static final int LAYOUTCHANGE_PROFILE_SETTINGS = 18;
	public static final int LAYOUTCHANGE_PROFILE_SETTINGS_BACK = 19;
	public static final int LAYOUTCHANGE_PROFILE_SETTINGS_CHANGE_PASSWORD = 20;
	public static final int LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT = 21;
	public static final int LAYOUTCHANGE_SETUP_SHARE_SOCIAL_LAYOUT = 22;
	public static final int LAYOUTCHANGE_SHARE_SOCIAL_LAYOUT = 23;
	public static final int LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT_BACK = 24;
	
	public static final int RESULT_GALLERY_FILTER_FINISH = 1;
	public static final int RESULT_WISHLISTS_FILTER_FINISH = 2;
	public static final int RESULT_PHOTO_OPTION_TO_MANAGE_COMMENT = 3;
	public static final int RESULT_NEARBY_FILTER_FINISH = 4;
	public static final int RESULT_GALLERY_FILTER_BACK = 5;
	public static final int RESULT_NEARBY_FILTER_BACK = 6;
	public static final int RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH = 7;
	public static final int RESULT_PHOTO_OPTION_TO_SHARE_LIST_PRODUCT_LAYOUT = 8;
	
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private Stack<View> layoutStack;
	private GalleryLayout galleryLayout;
	private MyFeedLayout myFeedLayout;
	private NearbyLayout nearbyLayout;
	private ProfileLayout profileLayout;
	private WishlistsGalleryLayout wishlistsGalleryLayout;
	private BrandFeedLayout brandFeedLayout;
	private StoreMainLayout storeMainLayout;
	private ProductLikeLayout productLikeLayout;
	private ProductCommentLayout productCommentLayout;
	private ProductDetailLayout productDetailLayout;
	private ShareImageDetailLayout shareImageDetailLayout;
	private ShareProductSearchLayout productSearchLayout;
	private ProfileFindFriendsLayout profileFindFriendsLayout;
	private ProfileFindFriendsListLayout profileFindFriendsListLayout;
	private ProfileEditProfile profileEditProfile;
	private ProfileChangeSettingsLayout profileChangeSettingsLayout;
	private ProfileChangePasswordLayout profileChangePasswordLayout;
	private ProductSocialShareListLayout productSocialShareListLayout;
	private ProductSocialShareLayout productSetupSocialShareLayout;
	private MyApp app;
	private Facebook facebook;
	private ToggleButton galleryMenuButton;
	private ToggleButton shareProductMenuButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        layoutStack = new Stack<View>();
        tabBar = (TabBar) this.findViewById( R.id.TabBar );
        tabBar.setBackgroundColor( Color.WHITE );
        bodyLayout = (ViewGroup)this.findViewById( R.id.BodyLayout );
        tabBar.setBodyLayout( bodyLayout );
        tabBar.setTabBarListener( this );
        app = (MyApp) this.getApplication();
        facebook = app.getFacebook();
        
        //---- First Layout ----//
        galleryMenuButton = new ToggleButton(this);
        galleryMenuButton.setTextOff( "" );
        galleryMenuButton.setTextOn( "" );
        galleryMenuButton.setBackgroundResource( R.drawable.gallery_button );
        galleryMenuButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        galleryLayout = new GalleryLayout(this);
        galleryLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        galleryLayout.setBodyLayoutChangeListener( this );
        tabBar.addTab( galleryMenuButton, galleryLayout );
        
        //---- Second Layout ----//
        ToggleButton b2 = new ToggleButton(this);
        b2.setTextOff( "" );
        b2.setTextOn( "" );
        b2.setBackgroundResource( R.drawable.feed_button );
        b2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        myFeedLayout = new MyFeedLayout(this);
        myFeedLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        myFeedLayout.setBodyLayoutChangeListener( this );
        tabBar.addTab( b2, myFeedLayout );
        
        //---- Third Layout ----//
        shareProductMenuButton = new ToggleButton(this);
        shareProductMenuButton.setTextOff( "" );
        shareProductMenuButton.setTextOn( "" );
        shareProductMenuButton.setBackgroundResource( R.drawable.share_button );
        shareProductMenuButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        LinearLayout l3 = new LinearLayout(this);
        l3.inflate( this, R.layout.emptylayout, l3 );
        l3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        tabBar.addTab( shareProductMenuButton, galleryLayout );
        
        //---- Fourth Layout ----//
        ToggleButton b4 = new ToggleButton(this);
        b4.setTextOff( "" );
        b4.setTextOn( "" );
        b4.setBackgroundResource( R.drawable.nearby_button );
        b4.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        nearbyLayout = new NearbyLayout(this);
        nearbyLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        nearbyLayout.setBodyLayoutChangeListener( this );
        tabBar.addTab( b4, nearbyLayout );
        
        //---- Fifth Layout ----//
        ToggleButton b5 = new ToggleButton(this);
        b5.setTextOff( "" );
        b5.setTextOn( "" );
        b5.setBackgroundResource( R.drawable.profile_button );
        b5.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        profileLayout = new ProfileLayout(this);
        profileLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        profileLayout.setBodyLayoutChangeListener( this );
        tabBar.addTab( b5, profileLayout );
    }

	@Override
	public void onRequestBodyLayoutStack(int requestId) {
		if(requestId == LAYOUTCHANGE_PRODUCTDETAIL){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productDetailLayout = new ProductDetailLayout(this);
			productDetailLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			productDetailLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productDetailLayout );
			bodyLayout.requestLayout();
		} else if(requestId == LAYOUTCHANGE_USERPROFILE){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			UserProfileLayout view = new UserProfileLayout(this);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			view.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( view );
			bodyLayout.requestLayout();
		} else if(requestId == LAYOUTCHANGE_BADGE){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			BadgeLayout view = new BadgeLayout(this);
			bodyLayout.removeAllViews();
			bodyLayout.addView( view );
			bodyLayout.requestLayout();
		} else if(requestId == LAYOUTCHANGE_WISHLISTS_GALLERY){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			wishlistsGalleryLayout = new WishlistsGalleryLayout( this );
			wishlistsGalleryLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			wishlistsGalleryLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( wishlistsGalleryLayout );
			bodyLayout.requestLayout();
		} else if(requestId == LAYOUTCHANGE_BRAND_FEED){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			brandFeedLayout = new BrandFeedLayout( this );
			brandFeedLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( brandFeedLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_STORE_MAIN){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			storeMainLayout = new StoreMainLayout( this );
			storeMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			storeMainLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( storeMainLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PRODUCT_LIKE_USER_LIST){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productLikeLayout = new ProductLikeLayout( this );
			productLikeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			productLikeLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productLikeLayout );
			bodyLayout.requestLayout();
		}else if(requestId ==  LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productCommentLayout = new ProductCommentLayout( this );
			productCommentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			productCommentLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productCommentLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PRODUCT_WISHLIST){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			ProductWishlistLayout productWishlistLayout = new ProductWishlistLayout( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productWishlistLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SHARE_IMAGE_DETAIL){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			shareImageDetailLayout = new ShareImageDetailLayout( this );
			shareImageDetailLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( shareImageDetailLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SHARE_IMAGE_DETAIL_SEARCH_VALUE){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productSearchLayout = new ShareProductSearchLayout( this );
			productSearchLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			productSearchLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productSearchLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SHARE_IMAGE_DETAIL_WITH_RESULT){
			if(!layoutStack.isEmpty()){
				View view = layoutStack.pop();
				while( !(view.equals( shareImageDetailLayout )) ){
					view = layoutStack.pop();
				}
				
				bodyLayout.removeAllViews();
				bodyLayout.addView( view );
				bodyLayout.requestLayout();
			}
			shareImageDetailLayout.refreshView();
		}else if(requestId == LAYOUTCHANGE_FIND_FRIENDS){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			profileFindFriendsLayout = new ProfileFindFriendsLayout( this );
			profileFindFriendsLayout.setBodyLayoutChangeListener( this );
			profileFindFriendsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( profileFindFriendsLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_EDIT_PROFILE){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			profileEditProfile = new ProfileEditProfile( this );
			profileEditProfile.setBodyLayoutChangeListener( this );
			profileEditProfile.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( profileEditProfile );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PROFILE_LAYOUT_BACK){
			if(!layoutStack.isEmpty()){
				this.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						View view = layoutStack.pop();
						while( !(view.equals( profileLayout )) ){
							view = layoutStack.pop();
						}
						
						bodyLayout.removeAllViews();
						bodyLayout.addView( view );
						bodyLayout.requestLayout();
					}
				});
			}
			profileLayout.refreshView();
		}else if(requestId == LAYOUTCHANGE_FIND_FRIENDS_LIST_LAYOUT){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			profileFindFriendsListLayout = new ProfileFindFriendsListLayout( this );
			profileFindFriendsListLayout.setBodyLayoutChangeListener( this );
			profileFindFriendsListLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( profileFindFriendsListLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PROFILE_WEBVIEW){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			ProfileLayoutWebView profileLayoutWebView = new ProfileLayoutWebView( this );
			profileLayoutWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( profileLayoutWebView );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PROFILE_SETTINGS){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			profileChangeSettingsLayout = new ProfileChangeSettingsLayout( this );
			profileChangeSettingsLayout.setBodyLayoutChangeListener( this );
			profileChangeSettingsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( profileChangeSettingsLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PROFILE_SETTINGS_BACK){
			if(!layoutStack.isEmpty()){
				this.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						View view = layoutStack.pop();
						while( !(view.equals( profileChangeSettingsLayout )) ){
							view = layoutStack.pop();
						}
						
						bodyLayout.removeAllViews();
						bodyLayout.addView( view );
						bodyLayout.requestLayout();
					}
				});
			}
		}else if(requestId == LAYOUTCHANGE_PROFILE_SETTINGS_CHANGE_PASSWORD){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			profileChangePasswordLayout = new ProfileChangePasswordLayout( this );
			profileChangePasswordLayout.setBodyLayoutChangeListener( this );
			profileChangePasswordLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( profileChangePasswordLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productSocialShareListLayout = new ProductSocialShareListLayout( this );
			productSocialShareListLayout.setBodyLayoutChangeListener( this );
			productSocialShareListLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( productSocialShareListLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SETUP_SHARE_SOCIAL_LAYOUT){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			ProductSetupSocialShareLayout productSetupSocialShareLayout = new ProductSetupSocialShareLayout( this );
			productSetupSocialShareLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( productSetupSocialShareLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SHARE_SOCIAL_LAYOUT){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productSetupSocialShareLayout = new ProductSocialShareLayout( this );
			productSetupSocialShareLayout.setBodyLayoutChangeListener( this );
			productSetupSocialShareLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bodyLayout.removeAllViews();
			bodyLayout.addView( productSetupSocialShareLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT_BACK){
			if(!layoutStack.isEmpty()){
				this.runOnUiThread( new Runnable() {
					@Override
					public void run() {
						View view = layoutStack.pop();
						while( !(view.equals( productSocialShareListLayout )) ){
							view = layoutStack.pop();
						}
						
						bodyLayout.removeAllViews();
						bodyLayout.addView( view );
						bodyLayout.requestLayout();
					}
				});
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!layoutStack.isEmpty()){
				View view = layoutStack.pop();
				if( view.equals( productSocialShareListLayout ) ){
					productSocialShareListLayout.refreshView();
					bodyLayout.removeAllViews();
					bodyLayout.addView( view );
					bodyLayout.requestLayout();
				}else{
					bodyLayout.removeAllViews();
					bodyLayout.addView( view );
					bodyLayout.requestLayout();
				}
				return true;
			}else{
				return true;
			}
		}
		return super.onKeyDown( keyCode, event );
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_GALLERY_FILTER ){
			if( resultCode == RESULT_GALLERY_FILTER_FINISH ){
				galleryLayout.refreshView( data );
			}
		}else if( requestCode == REQUEST_WISHLISTS_FILTER ){
			if( resultCode == RESULT_WISHLISTS_FILTER_FINISH ){
				wishlistsGalleryLayout.refreshView( data );
			}
		}else if( requestCode == REQUEST_PHOTO_OPTION ){
			if( resultCode == RESULT_PHOTO_OPTION_TO_MANAGE_COMMENT ){
				String getExtra[] = data.getExtras().getStringArray( ProductPhotoOptions.MANAGE_COMMENT_PUT_EXTRA );
				String actId = getExtra[0];
				String userId = getExtra[1];
				SharedPreferences myPreferences = this.getSharedPreferences( ProductCommentLayout.SHARE_PREF_VALUE_PRODUCT_COMMENT_ACT_ID, this.MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_ACT_ID, actId );
				prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_USER_ID, userId );
				prefsEditor.commit();
				this.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST );
			}else if( resultCode == RESULT_PHOTO_OPTION_TO_SHARE_LIST_PRODUCT_LAYOUT ){
				String actId = data.getExtras().getString( ProductPhotoOptions.PUT_EXTRA_ACTIVITY_ID );
				SharedPreferences myPreferences = this.getSharedPreferences( ProductSocialShareListLayout.SHARE_PREF_VALUE_SHARE_PRODUCT_ACT_ID, this.MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPreferences.edit();
				prefsEditor.putString( ProductSocialShareListLayout.SHARE_PREF_KEY_ACT_ID, actId );
				prefsEditor.commit();
				this.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT );
			}
		}else if( requestCode == REQUEST_NEARBY_FILTER ){
			if( resultCode == RESULT_NEARBY_FILTER_FINISH ){
				nearbyLayout.refreshView( data );
			}
		}else if( requestCode == REQUEST_SHARE_TAKE_IMAGE ){
			if(resultCode == RESULT_OK){
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
				ShareImageCropLayout shareImageCropLayout = new ShareImageCropLayout( this );
				shareImageCropLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				shareImageCropLayout.setImage( photo );
				shareImageCropLayout.setBodyLayoutChangeListener( this );
				bodyLayout.removeAllViews();
				bodyLayout.addView( shareImageCropLayout );
				bodyLayout.requestLayout();
			}
		}else if( requestCode == REQUEST_SHARE_CHOOSE_IMAGE ){
			Bitmap bitmap = null;
			if(resultCode == RESULT_OK){
	            Uri selectedImage = data.getData();
	            try {
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
				ShareImageCropLayout shareImageCropLayout = new ShareImageCropLayout( this );
				shareImageCropLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				shareImageCropLayout.setImage( bitmap );
				shareImageCropLayout.setBodyLayoutChangeListener( this );
				bodyLayout.removeAllViews();
				bodyLayout.addView( shareImageCropLayout );
				bodyLayout.requestLayout();
			}
		}else if( requestCode == REQUEST_INVITE_FRIENDS_SELECT_CONTACT ){
			if( data != null ){
				ArrayList<String> phoneNumberArray = new ArrayList<String>();
				Uri contactData = data.getData();
		        Cursor c =  getContentResolver().query(contactData, null, null, null, null);
		        while (c.moveToNext()) {
			        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
			        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			        if ( Integer.parseInt( hasPhone ) > 0 ) {
			            Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
			            while (phones.moveToNext()) { 
			               String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
			               phoneNumberArray.add( phoneNumber );
			            } 
			            phones.close();
			        }
		        }
		        
		        final CharSequence[] items = phoneNumberArray.toArray( new CharSequence[phoneNumberArray.size()] );
		        
		        if( items.length > 1 ){
		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			        builder.setTitle("Pick a number");
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			    	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", items[item].toString(), null));
			    			intent.putExtra("sms_body", "Invite to getable!!");
			    			startActivity(intent);
			            }
			        });
			        AlertDialog alert = builder.create();
			        alert.show();
		        }else if( items.length == 1 ){
	    	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", items[0].toString(), null));
	    			intent.putExtra("sms_body", "Invite to getable!!");
	    			startActivity(intent);
		        }
		        
			}
		}else if( requestCode == REQUEST_USER_CAHNGE_IMAGE ){
			if( resultCode == RESULT_CHANGESETTING_CHANGE_IMAGE_FINISH ){
				profileLayout.refreshView();
			}
		}
		
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	public void finishChildView(){
		if(!layoutStack.isEmpty()){
			bodyLayout.removeAllViews();
			bodyLayout.addView( layoutStack.pop() );
			bodyLayout.requestLayout();				
		}
	}

	@Override
	public void onTabBarPerform(CompoundButton button) {
		if( button.equals( shareProductMenuButton ) ){
			galleryMenuButton.setChecked(true);
			
			final Dialog dialog = new Dialog( this );
			dialog.setContentView( R.layout.customdialogselectimage);
			
			Button imageFromCameraButton = (Button) dialog.findViewById( R.id.customDialogSelectImageFromCameraButton );
			Button imageFromGalleryButton = (Button) dialog.findViewById( R.id.customDialogSelectImageFromGalleryButton );
			Button cancelButton = (Button) dialog.findViewById( R.id.customDialogSelectImageCancelButton );
			
			imageFromCameraButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		            startActivityForResult(cameraIntent, REQUEST_SHARE_TAKE_IMAGE );
				}
			});
			
			imageFromGalleryButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					Intent galleryIntent = new Intent();
					galleryIntent.setType("image/*");
					galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult( Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_SHARE_CHOOSE_IMAGE );
				}
			});
			
			cancelButton.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.show();
		}
	}
	
}