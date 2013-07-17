package com.robot.ai;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robot.R;

public class CoordinatePicker extends Fragment {
	private View v;
	private MapView m;
	private GoogleMap map;
	LatLng ENGLISCHER_GARTEN = new LatLng(48.1298926770173, 11.583151817321777);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// inflate and return the layout
		View v = inflater.inflate(R.layout.fragment_map, container, false);
		m = (MapView) v.findViewById(R.id.mapView);
		m.onCreate(savedInstanceState);

		map = m.getMap();

		// LocationManager lm = (LocationManager)
		// getActivity().getSystemService(Context.LOCATION_SERVICE);
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_LOW);
		// String bbb = lm.getBestProvider(criteria, true);
		// Location myLocation = lm.getLastKnownLocation(bbb);
		// CameraUpdate camPos = CameraUpdateFactory.newLatLng(new
		// LatLng(myLocation.getLatitude(), myLocation.getLongitude()));

		if (map != null) {
//			map.addMarker(new MarkerOptions().position(ENGLISCHER_GARTEN).title("Englischer Garten"));
//			CameraPosition mCameraPosition = new CameraPosition.Builder().target(ENGLISCHER_GARTEN).bearing(0).tilt(30).build();
//			map.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
			map.setMyLocationEnabled(true);
//			map.setOnMapClickListener(listener)
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
