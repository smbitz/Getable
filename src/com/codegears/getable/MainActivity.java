package com.codegears.getable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Document;

import com.codegears.getable.ui.BadgeLayout;
import com.codegears.getable.ui.GalleryLayout;
import com.codegears.getable.ui.ProductDetailLayout;
import com.codegears.getable.ui.UserProfileLayout;
import com.codegears.getable.ui.tabbar.TabBar;
import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements BodyLayoutStackListener, NetworkThreadListener{
	
	public static final int REQUEST_GALLERY_FILTER = 1;
	public static final int LAYOUTCHANGE_PRODUCTDETAIL = 1;
	public static final int LAYOUTCHANGE_USERPROFILE = 2;
	public static final int LAYOUTCHANGE_BADGE = 3;
	
	private TabBar tabBar;
	private ViewGroup bodyLayout;
	private Stack<View> layoutStack;
	private GalleryLayout galleryLayout;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //------- Login
        Map< String, String > newMapData = new HashMap<String, String>();
        newMapData.put( "email", "benz_shimo@hotmail.com" );
        newMapData.put( "password", "codegears" );
        newMapData.put( "_a", "logIn" );
        String postData = NetworkUtil.createPostData( newMapData );
        NetworkThreadUtil.getRawData( "http://wongnaimedia.dyndns.org/getable/guest.json?_f=logIn", postData, this );
        
        layoutStack = new Stack<View>();
        tabBar = (TabBar)this.findViewById( R.id.TabBar );
        bodyLayout = (ViewGroup)this.findViewById( R.id.BodyLayout );
        tabBar.setBodyLayout( bodyLayout );
        
        //---- First Layout ----//
        ToggleButton b1 = new ToggleButton(this);
        b1.setTextOff( "Gallery" );
        b1.setTextOn( "Gallery" );
        galleryLayout = new GalleryLayout(this);
        galleryLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        galleryLayout.setBodyLayoutChangeListener( this );
        galleryLayout.setBackgroundColor( 0xFFFF0000 );
        tabBar.addTab( b1, galleryLayout );
        
        //---- Second Layout ----//
        ToggleButton b2 = new ToggleButton(this);
        LinearLayout l2 = new LinearLayout(this);
        l2.setBackgroundColor( 0xFFFF00FF );
        TextView text2 = new TextView(this);
        text2.setText( "FeedView" );
        text2.setTextColor( 0xFFFFFFFF );
        l2.addView( text2 );
        b2.setTextOff( "Feed" );
        b2.setTextOn( "Feed" );
        tabBar.addTab( b2, l2 );
        
        //---- Third Layout ----//
        ToggleButton b3 = new ToggleButton(this);
        LinearLayout l3 = new LinearLayout(this);
        l3.setBackgroundColor( 0xFF00FFFF );
        TextView text3 = new TextView(this);
        text3.setText( "ShareView" );
        text3.setTextColor( 0xFFFFFFFF );
        l3.addView( text3 );
        b3.setTextOff( "Share" );
        b3.setTextOn( "Share" );
        tabBar.addTab( b3, l3 );
        
        //---- Fourth Layout ----//
        ToggleButton b4 = new ToggleButton(this);
        LinearLayout l4 = new LinearLayout(this);
        l4.setBackgroundColor( 0xFF0000FF );
        TextView text4 = new TextView(this);
        text4.setText( "NearByView" );
        text4.setTextColor( 0xFFFFFFFF );
        l4.addView( text4 );
        b4.setTextOff( "Nearby" );
        b4.setTextOn( "Nearby" );
        tabBar.addTab( b4, l4 );
        
        //---- Fifth Layout ----//
        ToggleButton b5 = new ToggleButton(this);
        LinearLayout l5 = new LinearLayout(this);
        l5.setBackgroundColor( 0xFF00FF00 );
        TextView text5 = new TextView(this);
        text5.setText( "ProfileView" );
        text5.setTextColor( 0xFFFFFFFF );
        l5.addView( text5 );
        b5.setTextOff( "Profile" );
        b5.setTextOn( "Profile" );
        tabBar.addTab( b5, l5 );    
    }

	@Override
	public void onRequestBodyLayoutStack(int requestId) {
		if(requestId == LAYOUTCHANGE_PRODUCTDETAIL){
			layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
			ProductDetailLayout view = new ProductDetailLayout(this);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			view.setBodyLayoutChangeListener( this );
			bodyLayout.removeAllViews();
			bodyLayout.addView( view );
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
		}
	}

	@Override
	public void onNetworkDocSuccess( String urlString, Document document ) {
	}

	@Override
	public void onNetworkRawSuccess( String urlString, String result ) {
		List<String> lastCookie = NetworkUtil.lastCookie;
		System.out.println("cookie test : " + lastCookie.size());
		for (String cookie : lastCookie) {
			System.out.println("cookie : " + cookie);
		}
		NetworkThreadUtil.getRawDataWithCookie( "http://wongnaimedia.dyndns.org/getable/me/feed.json", null, lastCookie, new NetworkThreadListener(){
			@Override
			public void onNetworkDocSuccess( String urlString, Document document ) {
			}
			@Override
			public void onNetworkRawSuccess( String urlString, String result ) {
				System.out.println("feed result : " + result);
			}
			@Override
			public void onNetworkFail( String urlString ) {
			}} );
	}

	@Override
	public void onNetworkFail( String urlString ) {
	}
	
}