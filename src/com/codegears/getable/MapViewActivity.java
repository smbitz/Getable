package com.codegears.getable;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MapViewActivity extends MapActivity {
	
	private MapView mapView;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView( R.layout.mapview );
		
		mapView = (MapView) findViewById( R.id.mapViewMap );
		mapView.setBuiltInZoomControls(false);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public MapView getMapView(){
		return mapView;
	}
	
}
