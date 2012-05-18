package com.codegears.getable;

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
import com.codegears.getable.ui.activity.ProductCommentLayout;
import com.codegears.getable.ui.activity.ProductDetailLayout;
import com.codegears.getable.ui.activity.ProductLikeLayout;
import com.codegears.getable.ui.activity.StoreMainLayout;
import com.codegears.getable.ui.activity.UserProfileLayout;
import com.codegears.getable.ui.activity.WishlistsGalleryLayout;
import com.codegears.getable.ui.tabbar.TabBar;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements BodyLayoutStackListener {
	
	public static final int REQUEST_GALLERY_FILTER = 1;
	public static final int REQUEST_WISHLISTS_FILTER = 2;
	public static final int REQUEST_PHOTO_OPTION_TO_MANAGE_COMMENT = 3;
	
	public static final int LAYOUTCHANGE_PRODUCTDETAIL = 1;
	public static final int LAYOUTCHANGE_USERPROFILE = 2;
	public static final int LAYOUTCHANGE_BADGE = 3;
	public static final int LAYOUTCHANGE_WISHLISTS_GALLERY = 4;
	public static final int LAYOUTCHANGE_BRAND_FEED = 5;
	public static final int LAYOUTCHANGE_STORE_MAIN = 6;
	public static final int LAYOUTCHANGE_PRODUCT_LIKE_USER_LIST = 7;
	public static final int LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST = 8;
	
	public static final int RESULT_GALLERY_FILTER_FINISH = 1;
	public static final int RESULT_WISHLISTS_FILTER_FINISH = 2;
	public static final int RESULT_PHOTO_OPTION_TO_MANAGE_COMMENT = 3;
	
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private Stack<View> layoutStack;
	private GalleryLayout galleryLayout;
	private MyFeedLayout myFeedLayout;
	private WishlistsGalleryLayout wishlistsGalleryLayout;
	private BrandFeedLayout brandFeedLayout;
	private StoreMainLayout storeMainLayout;
	private ProductLikeLayout productLikeLayout;
	private ProductCommentLayout productCommentLayout;
	private ProductDetailLayout productDetailLayout;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        layoutStack = new Stack<View>();
        tabBar = (TabBar) this.findViewById( R.id.TabBar );
        tabBar.setBackgroundColor( Color.WHITE );
        bodyLayout = (ViewGroup)this.findViewById( R.id.BodyLayout );
        tabBar.setBodyLayout( bodyLayout );
        
        //---- First Layout ----//
        ToggleButton b1 = new ToggleButton(this);
        b1.setTextOff( "" );
        b1.setTextOn( "" );
        b1.setBackgroundResource( R.drawable.gallery_button );
        b1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        galleryLayout = new GalleryLayout(this);
        galleryLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        galleryLayout.setBodyLayoutChangeListener( this );
        tabBar.addTab( b1, galleryLayout );
        
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
        ToggleButton b3 = new ToggleButton(this);
        b3.setTextOff( "" );
        b3.setTextOn( "" );
        b3.setBackgroundResource( R.drawable.share_button );
        b3.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        LinearLayout l3 = new LinearLayout(this);
        l3.setBackgroundColor( 0xFF00FFFF );
        TextView text3 = new TextView(this);
        text3.setText( "ShareView" );
        text3.setTextColor( 0xFFFFFFFF );
        l3.addView( text3 );
        tabBar.addTab( b3, l3 );
        
        //---- Fourth Layout ----//
        ToggleButton b4 = new ToggleButton(this);
        b4.setTextOff( "" );
        b4.setTextOn( "" );
        b4.setBackgroundResource( R.drawable.nearby_button );
        b4.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        LinearLayout l4 = new LinearLayout(this);
        l4.setBackgroundColor( 0xFF0000FF );
        TextView text4 = new TextView(this);
        text4.setText( "NearByView" );
        text4.setTextColor( 0xFFFFFFFF );
        l4.addView( text4 );
        tabBar.addTab( b4, l4 );
        
        //---- Fifth Layout ----//
        ToggleButton b5 = new ToggleButton(this);
        b5.setTextOff( "" );
        b5.setTextOn( "" );
        b5.setBackgroundResource( R.drawable.profile_button );
        b5.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        LinearLayout l5 = new LinearLayout(this);
        l5.setBackgroundColor( 0xFF00FF00 );
        TextView text5 = new TextView(this);
        text5.setText( "ProfileView" );
        text5.setTextColor( 0xFFFFFFFF );
        l5.addView( text5 );
        tabBar.addTab( b5, l5 );    
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
			storeMainLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( storeMainLayout );
			bodyLayout.requestLayout();
		}else if(requestId == LAYOUTCHANGE_PRODUCT_LIKE_USER_LIST){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productLikeLayout = new ProductLikeLayout( this );
			productLikeLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productLikeLayout );
			bodyLayout.requestLayout();
		}else if(requestId ==  LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			productCommentLayout = new ProductCommentLayout( this );
			productCommentLayout.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( productCommentLayout );
			bodyLayout.requestLayout();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!layoutStack.isEmpty()){
				bodyLayout.removeAllViews();
				bodyLayout.addView( layoutStack.pop() );
				bodyLayout.requestLayout();				
				return true;
			}
		}
		return super.onKeyDown( keyCode, event );
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_GALLERY_FILTER ){
			galleryLayout.refreshView( data );
		}else if( requestCode == REQUEST_WISHLISTS_FILTER ){
			wishlistsGalleryLayout.refreshView( data );
		}else if( requestCode == REQUEST_PHOTO_OPTION_TO_MANAGE_COMMENT && resultCode == RESULT_PHOTO_OPTION_TO_MANAGE_COMMENT ){
			String actId = data.getExtras().getString( ProductPhotoOptions.MANAGE_COMMENT_PUT_EXTRA_ACTIVITY_ID );
			SharedPreferences myPreferences = this.getSharedPreferences( ProductCommentLayout.SHARE_PREF_VALUE_PRODUCT_ID, this.MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPreferences.edit();
			prefsEditor.putString( ProductCommentLayout.SHARE_PREF_KEY_PRODUCT_ID, actId );
			prefsEditor.commit();
			this.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_PRODUCT_COMMENT_USER_LIST );
		}
	}
	
}