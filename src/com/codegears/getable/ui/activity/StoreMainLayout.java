package com.codegears.getable.ui.activity;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

public class StoreMainLayout extends AbstractViewLayout {

	public static final String SHARE_PREF_VALUE_STORE_ID = "SHARE_PREF_VALUE_STORE_ID";
	public static final String SHARE_PREF_KEY_STORE_ID = "SHARE_PREF_KEY_STORE_ID";
	
	private BodyLayoutStackListener listener;
	private LinearLayout mapViewLayout;

	public StoreMainLayout(Activity activity) {
		super(activity);
		View.inflate(this.getContext(), R.layout.storemainlayout, this);
		
		mapViewLayout = (LinearLayout) findViewById( R.id.storeMainLayoutMapView );
	}

	@Override
	public void refreshView(Intent getData) {
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

}
