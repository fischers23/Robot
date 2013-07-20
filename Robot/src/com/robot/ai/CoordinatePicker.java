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

public class CoordinatePicker extends Fragment {
	private View v;
	private MapView m;
	private GoogleMap map;
	LatLng ENGLISCHER_GARTEN = new LatLng(48.1298926770173, 11.583151817321777);

	OnLocationSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnLocationSelectedListener {
        public void onLocationSelected(LatLng point);
    }
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// inflate and return the layout
		View v = inflater.inflate(R.layout.fragment_map, container, false);
		m = (MapView) v.findViewById(R.id.mapView);
		m.onCreate(savedInstanceState);

		// get the map object
		map = m.getMap();
		if (map != null) {
			
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
			map.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
			
			// make button to current location available
			map.setMyLocationEnabled(true);
			
			// set onclick listener to grab a position
			map.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {
					// get click point
					mCallback.onLocationSelected(point);
				}
			});

		} else
			Log.e("CoordinatePicker", "map is null");
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		m.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		m.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		m.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		m.onLowMemory();
	}

}
