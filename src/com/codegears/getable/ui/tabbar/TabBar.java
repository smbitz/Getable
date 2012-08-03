package com.codegears.getable.ui.tabbar;

import java.util.HashMap;
import java.util.Map;

import com.codegears.getable.util.compoundbuttongroup.CompoundButtonGroup;
import com.codegears.getable.util.compoundbuttongroup.CompoundButtonGroupListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

public class TabBar extends LinearLayout implements CompoundButtonGroupListener {

	private CompoundButtonGroup buttonGroup;
	private TabBarListener listener;
	private Map<CompoundButton, ViewGroup> buttonViewMap;
	private ViewGroup bodyLayout;

	public TabBar( Context context, AttributeSet attrs ) {
		super( context, attrs );
		buttonGroup = new CompoundButtonGroup( true );
		buttonGroup.setCompoundButtonGroupListener( this );
		buttonViewMap = new HashMap<CompoundButton, ViewGroup>();
	}

	public void addTab( CompoundButton button, ViewGroup view ) {
		buttonViewMap.put( button, view );
		buttonGroup.addButton( button );
		if(buttonViewMap.size() == 1){
			performTab(button);
		}
		this.addView( button );
		this.invalidate();
	}
	
	public void setBodyLayout(ViewGroup bodyLayout){
		this.bodyLayout = bodyLayout;
	}

	public void setTabBarListener( TabBarListener listener ) {
		this.listener = listener;
	}
	
	public void performTab(CompoundButton button){
		ViewGroup view = buttonViewMap.get( button );
		if(bodyLayout != null){
			bodyLayout.removeAllViews();
			bodyLayout.addView( view );
			bodyLayout.requestLayout();
			if(listener != null){
				listener.onTabBarPerform(button);
			}
		}
	}

	@Override
	public void onButtonGroupClick( CompoundButton clicked ) {
		performTab(clicked);
	}
}