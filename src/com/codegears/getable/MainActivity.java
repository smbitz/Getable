package com.codegears.getable;

import java.util.Stack;

import com.codegears.getable.ui.BadgeLayout;
import com.codegears.getable.ui.GalleryLayout;
import com.codegears.getable.ui.ProductDetailLayout;
import com.codegears.getable.ui.UserProfileLayout;
import com.codegears.getable.ui.tabbar.TabBar;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements BodyLayoutStackListener{
  
		private TabBar tabBar;
		private ViewGroup bodyLayout;
		private Stack<View> layoutStack;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        layoutStack = new Stack<View>();
        tabBar = (TabBar)this.findViewById( R.id.TabBar );
        bodyLayout = (ViewGroup)this.findViewById( R.id.BodyLayout );
        tabBar.setBodyLayout( bodyLayout );
        //---- First Layout ----//
        ToggleButton b1 = new ToggleButton(this);
        b1.setTextOff( "Gallery" );
        b1.setTextOn( "Gallery" );
        GalleryLayout l1 = new GalleryLayout(this);
        l1.setBodyLayoutChangeListener( this );
        l1.setBackgroundColor( 0xFFFF0000 );
        TextView text = new TextView(this);
        text.setText( "GalleryView" );
        text.setTextColor( 0xFFFFFFFF );
        l1.addView( text );
        tabBar.addTab( b1, l1 );
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
			if(requestId == GalleryLayout.LAYOUTCHANGE_PRODUCTDETAIL){
				layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
				ProductDetailLayout view = new ProductDetailLayout(this);
				view.setBodyLayoutChangeListener( this );
				bodyLayout.removeAllViews();
				bodyLayout.addView( view );
				bodyLayout.requestLayout();
			} else if(requestId == ProductDetailLayout.LAYOUTCHANGE_USERPROFILE){
				layoutStack.push( bodyLayout.getChildAt( 0 ) );		//store bodylayout in stack
				UserProfileLayout view = new UserProfileLayout(this);
				view.setBodyLayoutChangeListener( this );
				bodyLayout.removeAllViews();
				bodyLayout.addView( view );
				bodyLayout.requestLayout();
			} else if(requestId == UserProfileLayout.LAYOUTCHANGE_BADGE){
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
}