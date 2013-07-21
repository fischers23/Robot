package com.robot.ai;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.robot.R;
import com.robot.ui.AIDriver;

public class CoordinatePicker extends Fragment {
	private View mView;
	private MapView mMapView;
	private GoogleMap mMap;

  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// inflate and return the layout
		mView = inflater.inflate(R.layout.fragment_map, container, false);
		mMapView = (MapView) mView.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);

		// get the map object
		mMap = mMapView.getMap();
		if (mMap != null) {
			
			// get current position
			LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_LOW);
			String bbb = lm.getBestProvider(criteria, true);
			Location myLocation = lm.getLastKnownLocation(bbb);
			LatLng location = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			Log.d("CoordinatePicker", location.toString());

			try {
				// initialize map interaction
				MapsInitializer.initialize(getActivity());
			} catch (GooglePlayServicesNotAvailableException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// go to current position
			CameraPosition mCameraPosition = new CameraPosition.Builder().target(location).bearing(0).tilt(30).zoom(15f).build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
			
			// make button to current location available
			mMap.setMyLocationEnabled(true);
			
			// set onclick listener to grab a position
			mMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {
					// send point to AI Driver
					AIDriver fragment = (AIDriver) getFragmentManager().findFragmentByTag("aid");
					fragment.setDestinationLocation(point);

					// close map
					closeFragment();
				}
			});

		} else
			Log.e("CoordinatePicker", "map is null");
		return mView;
	}

	/**
	 * go back to previous fragment
	 */
	private void closeFragment() {
		getActivity().getSupportFragmentManager().popBackStack();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}
	


}
