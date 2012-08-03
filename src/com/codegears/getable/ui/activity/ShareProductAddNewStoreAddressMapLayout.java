package com.codegears.getable.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.MyBalloonItemizedOverlay;
import com.codegears.getable.util.GetCurrentLocation;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

import android.view.View.OnClickListener;

public class ShareProductAddNewStoreAddressMapLayout extends AbstractViewLayout implements OnClickListener {
	
	private static final int DEFAULT_ZOOM_NUM = 19;
	
	private ImageButton backButton;
	private Button doneButton;
	private LinearLayout mapViewLayout;
	private BodyLayoutStackListener listener;
	private MapView myMapView;
	private MapController myMapController;
	private TextView howDoesText;
	private TextView youCanZoomText;
	private Double currentLat;
	private Double currentLong;
	private String provider;
	private String newStoreName;
	private MyLocationOverlay me;
	private GetCurrentLocation getCurrentLocation;
	
	private LocationManager myLocationManager;
	private LocationListener myLocationListener;
	private SitesOverlay myBalloonItemizedOverlay;
	
	private Double defaultLat = GetCurrentLocation.DEFAULT_LAT_VALUE;
	private Double defaultLong = GetCurrentLocation.DEFAULT_LNG_VALUE;

	private GeoPoint defaultGeoPint = new GeoPoint(defaultLat.intValue(), defaultLong.intValue());
	
	public ShareProductAddNewStoreAddressMapLayout(Activity activity, MapView setMapView) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareproductaddnewstoreaddressmaplayout, this );
		
		//Get new store name
		SharedPreferences myPrefNewStoreName = this.getActivity().getSharedPreferences( ShareProductAddNewStoreLayout.SHARE_PREF_NEW_STORE_NAME, this.getActivity().MODE_PRIVATE );
		newStoreName = myPrefNewStoreName.getString( ShareProductAddNewStoreLayout.SHARE_PREF_KEY_NEW_STORES_NAME, "" );
		
		backButton = (ImageButton) findViewById( R.id.shareProductAddNewStoreAddressMapLayoutBackButton );
		doneButton = (Button) findViewById( R.id.shareProductAddNewStoreAddressMapLayoutDoneButton );
		mapViewLayout = (LinearLayout) findViewById( R.id.shareProductAddNewStoreAddressMapLayoutMapViewLayout );
		howDoesText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressMapLayoutHowDoesText );
		youCanZoomText = (TextView) findViewById( R.id.shareProductAddNewStoreAddressMapLayoutYouCanZoomText );
		myMapView = setMapView;
		getCurrentLocation = new GetCurrentLocation( this.getContext() );
		
		mapViewLayout.addView( myMapView );
		
		doneButton.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		howDoesText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		youCanZoomText.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH ) );
		
		doneButton.setOnClickListener( this );
		backButton.setOnClickListener( this );
		
		currentLat = Double.valueOf( getCurrentLocation.getCurrentLat() ) * 1E6;
		currentLong = Double.valueOf( getCurrentLocation.getCurrentLng() ) * 1E6;
		
		setCurrentLocation();
	}
	
	private void setCurrentLocation() {
		//myMapView = (MapView) findViewById(R.id.rescueMapView);
		myMapView.setTraffic(true);
		// /myMapView.setBuiltInZoomControls(true);

		myMapController = myMapView.getController();
		myMapController.setZoom( DEFAULT_ZOOM_NUM ); // Fixed Zoom Level

		myLocationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
		myLocationListener = new MyLocationListener();

		/*try {
			Criteria criteria = new Criteria();
			provider = myLocationManager.getBestProvider(criteria, false);
			
			Location location = myLocationManager
					.getLastKnownLocation(provider);

			myLocationManager.requestLocationUpdates(provider, 0, 0,
					myLocationListener);

			Location lastLocation = myLocationManager
					.getLastKnownLocation(provider);

			if (lastLocation != null) {
				System.out.println("Debug1!!");
				int lastLocLat = (int) (lastLocation.getLatitude() * 1000000);
				int lastLocLong = (int) (lastLocation.getLongitude() * 1000000);
				// Get the current location in start-up
				GeoPoint initGeoPoint = new GeoPoint(lastLocLat, lastLocLong);
				CenterLocatio(initGeoPoint);
				System.out.println("Debug2!!");
			} else {
				System.out.println("Debug3!!");
				AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
				builder.setTitle("Location Services Disabled")
						.setMessage(
								"The location service on the device are disabled, your location is not able to be found, please call AXA uding the button below to be rescued")
						.setCancelable(false)
						.setPositiveButton("Drop pin",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// do things
										CenterLocatio(defaultGeoPint);
									}
								});
				builder.create().show();
				System.out.println("Debug4!!");
			}
			System.out.println("Debug5!!");
		} catch (Exception e) {
			// TODO: handle exception

			Context context = this.getActivity().getApplicationContext();
			String toasttext = e.getMessage();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, toasttext, duration);
			toast.show();

		}*/
		
		// Get the current location in start-up
		GeoPoint initGeoPoint = new GeoPoint( currentLat.intValue(), currentLong.intValue() );
		CenterLocatio(initGeoPoint);

	}
	
	private void CenterLocatio(GeoPoint centerGeoPoint) {
		if( myBalloonItemizedOverlay != null ){
			myBalloonItemizedOverlay.hideAllBalloons();
		}
		myMapView.getOverlays().clear();
		myMapController.animateTo(centerGeoPoint);

		Drawable marker = getResources().getDrawable(R.drawable.map_pin);

		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		//int lat = centerGeoPoint.getLatitudeE6();
		//int lon = centerGeoPoint.getLongitudeE6();

		currentLat = Double.valueOf( centerGeoPoint.getLatitudeE6() / 1E6 ) * 1E6;
		currentLong = Double.valueOf( centerGeoPoint.getLongitudeE6() / 1E6 ) * 1E6;

		/*ArrayList<Double> passing = new ArrayList<Double>();
		passing.add(currentLat);
		passing.add(currentLong);

		Context context = this.getActivity().getApplicationContext();
		ReverseGeocodeLookupTask task = new ReverseGeocodeLookupTask();
		task.applicationContext = context;
		task.activityContext = rescue.this;
		task.execute(passing);*/
		
		addMapBalloon();
		
		/*me = new MyLocationOverlay(this.getContext(), myMapView);
		myMapView.getOverlays().add(me);*/
		
		//addMapBalloon();

	};
	
	private class SitesOverlay extends BalloonItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private OverlayItem inDrag = null;
		private ImageView dragImage = null;
		private int xDragImageOffset = 0;
		private int yDragImageOffset = 0;
		private int xDragTouchOffset = 0;
		private int yDragTouchOffset = 0;

		public SitesOverlay(Drawable marker, int lat, int longitude, MapView setMapView) {
			super(boundCenter(marker), setMapView);
			this.marker = marker;

			dragImage = (ImageView) findViewById(R.id.shareProductAddNewStoreAddressMapLayoutDragImage);
			xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
			yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();

			/*items.add(new OverlayItem(getPoint(lat, longitude), "UN",
					"United Nations"));*/

			populate();
		}
		
		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
		    populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);

			boundCenterBottom(marker);
		}

		@Override
		public int size() {
			return (items.size());
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			if( myBalloonItemizedOverlay != null ){
				myBalloonItemizedOverlay.hideAllBalloons();
			}
			
			final int action = event.getAction();
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			boolean result = false;

			if (action == MotionEvent.ACTION_DOWN) {
				for (OverlayItem item : items) {
					Point p = new Point(0, 0);

					myMapView.getProjection().toPixels(item.getPoint(), p);

					if (hitTest(item, marker, x - p.x, y - p.y)) {
						result = true;
						inDrag = item;
						items.remove(inDrag);
						populate();

						xDragTouchOffset = 0;
						yDragTouchOffset = 0;

						setDragImagePosition(p.x, p.y);
						dragImage.setVisibility(View.VISIBLE);

						xDragTouchOffset = x - p.x;
						yDragTouchOffset = y - p.y;

						break;
					}
				}
			} else if (action == MotionEvent.ACTION_MOVE && inDrag != null) {
				setDragImagePosition(x, y);
				result = true;
			} else if (action == MotionEvent.ACTION_UP && inDrag != null) {
				dragImage.setVisibility(View.GONE);

				GeoPoint pt = myMapView.getProjection().fromPixels(
						x - xDragTouchOffset, y - yDragTouchOffset);

				String title = inDrag.getTitle();
				OverlayItem toDrop = new OverlayItem(pt, title,
						inDrag.getSnippet());
				
				myBalloonItemizedOverlay.setFocus( toDrop );

				items.add(toDrop);
				populate();

				currentLat = Double.valueOf( pt.getLatitudeE6() / 1E6 ) * 1E6;
				currentLong = Double.valueOf( pt.getLongitudeE6() / 1E6 ) * 1E6;

				/*ArrayList<Double> passing = new ArrayList<Double>();
				passing.add(lat);
				passing.add(lon);

				Context context = getApplicationContext();
				ReverseGeocodeLookupTask task = new ReverseGeocodeLookupTask();
				task.applicationContext = context;
				task.activityContext = rescue.this;
				task.execute(passing);*/

				// CenterLocatio(pt);

				inDrag = null;
				result = true;
			}

			return (result || super.onTouchEvent(event, mapView));
		}

		private void setDragImagePosition(int x, int y) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage
					.getLayoutParams();

			lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y
					- yDragImageOffset - yDragTouchOffset, 0, 0);
			dragImage.setLayoutParams(lp);
		}
	}
	
	private GeoPoint getPoint(int lat, int lon) {
		return (new GeoPoint(lat, lon));
	}
	
	private void addMapBalloon(){
		GeoPoint targetPoint = new GeoPoint( currentLat.intValue(), currentLong.intValue() );
		
		myBalloonItemizedOverlay = new SitesOverlay( this.getActivity().getResources().getDrawable( R.drawable.map_pin ), currentLat.intValue(), currentLong.intValue(), myMapView);
		OverlayItem overlayItem = new OverlayItem( targetPoint, newStoreName, null );
		myBalloonItemizedOverlay.setBalloonBottomOffset(70);
		myBalloonItemizedOverlay.addOverlay( overlayItem );
		myBalloonItemizedOverlay.setFocus( overlayItem );
		List<Overlay> mapOverlays = myMapView.getOverlays();
		mapOverlays.add( myBalloonItemizedOverlay );
		
		myMapController.animateTo( targetPoint );
	}
	
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location argLocation) {
			// TODO Auto-generated method stub
			GeoPoint myGeoPoint = new GeoPoint(
					(int) (argLocation.getLatitude() * 1000000),
					(int) (argLocation.getLongitude() * 1000000));

			// CenterLocatio(myGeoPoint);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		mapViewLayout.addView( myMapView );
		addMapBalloon();
	}

	@Override
	public void onClick(View v) {
		if( v.equals( doneButton ) ){
			SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareProductAddNewStoreAddressLayout.SHARE_PREF_VALUE_ADD_STORE, this.getActivity().MODE_PRIVATE );
			SharedPreferences.Editor prefsEditor = myPref.edit();
			prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LAT, String.valueOf( currentLat ) );
			prefsEditor.putString( ShareProductAddNewStoreAddressLayout.SHARE_PREF_KEY_MAP_LONG, String.valueOf( currentLong ) );
			prefsEditor.commit();
			
			removeMapViewFromLayout();
			
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_ADD_NEW_STORE_BACK );
		}else if( v.equals( backButton ) ){
			listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_BACK_BUTTON );
		}
	}
	
	public void removeMapViewFromLayout(){
		myBalloonItemizedOverlay.hideAllBalloons();
		myMapView.getOverlays().clear();
		mapViewLayout.removeAllViews();
	}

}
