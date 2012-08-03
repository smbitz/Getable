package com.codegears.getable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codegears.getable.ui.MyItemizedOverlay;
import com.codegears.getable.ui.MyOverLay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MapScene extends MapActivity {
	
	public static final String PUT_MAP_EXTRA = "PUT_MAP_EXTRA";
	private static final int DEFAULT_ZOOM_NUM = 19;
	//private static final int DEFAULT_ZOOM_NUM = 30;
	private static final Double DEFAULT_LNG = 13.865043;
	private static final Double DEFAULT_LAT = 100.603166;
	
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private Double targetLatitude;
	private Double targetLongitude;
	private GeoPoint targetPoint;
	private GeoPoint currentPoint;
	private Double currentLat,targetLat;
	private Double currentLng,targetLng;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView( R.layout.mapscene );
		
		mapView = (MapView) findViewById( R.id.mapSceneMapView );
		mapController = mapView.getController();
		
		mapController.setZoom( DEFAULT_ZOOM_NUM );
		
		if( checkEnableGPS() ){
			targetLatitude = Double.valueOf( getIntent().getStringArrayExtra( PUT_MAP_EXTRA )[0] );
			targetLongitude = Double.valueOf(  getIntent().getStringArrayExtra( PUT_MAP_EXTRA )[1] );
			
			// Finding Current Location
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
					currentLat = location.getLatitude() * 1E6;
					currentLng = location.getLongitude() * 1E6;
					currentPoint = new GeoPoint(currentLat.intValue(), currentLng.intValue());
					
					targetLat = targetLatitude * 1E6;
					targetLng = targetLongitude * 1E6;
					targetPoint = new GeoPoint(targetLat.intValue(), targetLng.intValue());
					
					mapView.getOverlays().clear();
					
					List<Overlay> mapOverlays = mapView.getOverlays();
					Drawable targetDrawable = MapScene.this.getResources().getDrawable( R.drawable.map_pin );
					MyItemizedOverlay targetItemOverLay = new MyItemizedOverlay( targetDrawable );
					OverlayItem overlayitemTarget = new OverlayItem(targetPoint, "", "");
					targetItemOverLay.addOverlay( overlayitemTarget );
					mapOverlays.add( targetItemOverLay );
					
					MapScene.this.DrawPath( currentPoint, targetPoint, Color.GREEN, mapView );
					
					Drawable currentDrawable = MapScene.this.getResources().getDrawable( R.drawable.map_current_location );
					MyItemizedOverlay currentItemOverlay = new MyItemizedOverlay( currentDrawable );
					OverlayItem overlayitem = new OverlayItem(currentPoint, "", "");
					currentItemOverlay.addOverlay( overlayitem );
					mapOverlays.add( currentItemOverlay );
					
					//mapController.animateTo( currentPoint );
					
					int maxLat = Math.max(currentPoint.getLatitudeE6(), targetPoint.getLatitudeE6());
				    int minLat = Math.min(currentPoint.getLatitudeE6(), targetPoint.getLatitudeE6());
				    int maxLon = Math.max(currentPoint.getLongitudeE6(), targetPoint.getLongitudeE6());
				    int minLon = Math.min(currentPoint.getLongitudeE6(), targetPoint.getLongitudeE6());
					
				    double fitFactor = 1.5;
				    mapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor), (int)(Math.abs(maxLon - minLon) * fitFactor));
				    mapController.animateTo(new GeoPoint( (maxLat + minLat)/2, 
				    (maxLon + minLon)/2 )); 
				    
					locationManager.removeUpdates( this );
				}
			};
			
			Location location = null;
			if( locationGPS == null ){
				location = locationNetwork;
				locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, newListener);
			}else{
				location = locationGPS;
				locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, newListener);
			}
			
			//Check when location null.
			if( location == null ){
				currentLat = DEFAULT_LAT * 1E6;
				currentLng = DEFAULT_LNG * 1E6;
			}else{
				currentLat = location.getLatitude() * 1E6;
				currentLng = location.getLongitude() * 1E6;
			}
			
			currentPoint = new GeoPoint(currentLat.intValue(), currentLng.intValue());
			
			targetLat = targetLatitude * 1E6;
			targetLng = targetLongitude * 1E6;
			targetPoint = new GeoPoint(targetLat.intValue(), targetLng.intValue());
			
			List<Overlay> mapOverlays = mapView.getOverlays();
			Drawable targetDrawable = this.getResources().getDrawable( R.drawable.map_pin );
			MyItemizedOverlay targetItemOverLay = new MyItemizedOverlay( targetDrawable );
			OverlayItem overlayitemTarget = new OverlayItem(targetPoint, "", "");
			targetItemOverLay.addOverlay( overlayitemTarget );
			mapOverlays.add( targetItemOverLay );
			
			this.DrawPath( currentPoint, targetPoint, Color.GREEN, mapView );
			
			Drawable currentDrawable = this.getResources().getDrawable( R.drawable.map_current_location );
			MyItemizedOverlay currentItemOverlay = new MyItemizedOverlay( currentDrawable );
			OverlayItem overlayitem = new OverlayItem(currentPoint, "", "");
			currentItemOverlay.addOverlay( overlayitem );
			mapOverlays.add( currentItemOverlay );
			
			mapController.animateTo( currentPoint  );
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void DrawPath(GeoPoint src,GeoPoint dest, int color, MapView mMapView01)
	{
		// connect to map web service
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");//from
		urlString.append( Double.toString((double)src.getLatitudeE6()/1.0E6 ));
		urlString.append(",");
		urlString.append( Double.toString((double)src.getLongitudeE6()/1.0E6 ));
		urlString.append("&daddr=");//to
		urlString.append( Double.toString((double)dest.getLatitudeE6()/1.0E6 ));
		urlString.append(",");
		urlString.append( Double.toString((double)dest.getLongitudeE6()/1.0E6 ));
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		Log.d("xxx","URL="+urlString.toString());
		// get the kml (XML) doc. And parse it to get the coordinates(direction route).
		Document doc = null;
		HttpURLConnection urlConnection= null;
		URL url = null;
		try
		{
			url = new URL(urlString.toString());
			urlConnection=(HttpURLConnection)url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
		
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
		
			if(doc.getElementsByTagName("GeometryCollection").getLength()>0)
			{
			//String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName();
			String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getFirstChild().getNodeValue() ;
			Log.d("xxx","path="+ path);
			String [] pairs = path.split(" ");
			String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude lngLat[1]=latitude lngLat[2]=height
			// src
			GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
			mMapView01.getOverlays().add(new MyOverLay(startGP,startGP,1));
			GeoPoint gp1;
			GeoPoint gp2 = startGP;
			for(int i=1;i<pairs.length;i++) // the last one would be crash
			{
			lngLat = pairs[i].split(",");
			gp1 = gp2;
			// watch out! For GeoPoint, first:latitude, second:longitude
			gp2 = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
			mMapView01.getOverlays().add(new MyOverLay(gp1,gp2,2,color));
			Log.d("xxx","pair:" + pairs[i]);
			}
				mMapView01.getOverlays().add(new MyOverLay(dest,dest, 3)); // use the default color
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
	}
	
	private Boolean checkEnableGPS(){
	  String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	  if( provider == null ){
    	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	   builder.setMessage("Please select wireless networks or GPS to get your location.")
    	          .setCancelable(false)
    	          .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int id) {
    	            	   Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
    	                   startActivity(intent);
    	                   MapScene.this.finish();
    	              }
    	          })
    	          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int id) {
    	            	   MapScene.this.finish();
    	              }
    	          });
    	   AlertDialog alert = builder.create();
    	   alert.show();
    	   return false;
       }else{
    	   return true;
       }
    }
	
}
