package com.codegears.getable.util;

import java.text.DecimalFormat;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.FloatMath;

import com.google.android.maps.GeoPoint;

public class GetCurrentLocation {
	
	private static Double DEFAULT_LAT_VALUE = 13.808655;
	private static Double DEFAULT_LNG_VALUE = 100.620929;
	
	private GeoPoint currentPoint;
	private LocationManager locationManager;
	private Double currentLat;
	private Double currentLng;
	private DecimalFormat changeNumberFormat;
	private Location locationChoose;
	
	public GetCurrentLocation( Context context ){
		changeNumberFormat = new DecimalFormat("0.000000");
		
		// Finding Current Location
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location locationGPS = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
		Location locationNetwork = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
		
		LocationListener newListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				currentLat = location.getLatitude();
				currentLng = location.getLongitude();
				currentPoint = new GeoPoint(currentLat.intValue(), currentLng.intValue());
				locationManager.removeUpdates( this );
			}
		};
		
		if( locationGPS != null ){
			locationChoose = locationGPS;
			locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, newListener);
			currentLat = locationChoose.getLatitude();
			currentLng = locationChoose.getLongitude();
		}else if( locationNetwork != null ){
			locationChoose = locationNetwork;
			locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, newListener);
			currentLat = locationChoose.getLatitude();
			currentLng = locationChoose.getLongitude();
		}else {
			currentLat = DEFAULT_LAT_VALUE;
			currentLng = DEFAULT_LNG_VALUE;
		}
		
		currentPoint = new GeoPoint(currentLat.intValue(), currentLng.intValue());
	}
	
	public String getCurrentLat(){
		return String.valueOf( currentLat );
	}
	
	public String getCurrentLng(){
		return String.valueOf( currentLng );
	}
	
	/*public void getDistanceBetween( Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude ){
		locationChoose.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
	}*/
	
	public float gps2m(float lat_a, float lng_a, float lat_b, float lng_b) { 
		Location locationA = new Location("point A");

		locationA.setLatitude(lat_a);
		locationA.setLongitude(lng_a);

		Location locationB = new Location("point B");

		locationB.setLatitude(lat_b);
		locationB.setLongitude(lng_b);

		float distance = locationA.distanceTo(locationB);
		
		return distance;
	}
	
}
