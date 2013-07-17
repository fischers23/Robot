package com.robot.ai;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robot.R;

public class CoordinatePicker extends Fragment {
	private View v;
	private GoogleMap map;
	  static final LatLng HAMBURG = new LatLng(53.558, 9.927);

	// mapView
	MapView m;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflat and return the layout
		View v = inflater.inflate(R.layout.fragment_map, container, false);
		m = (MapView) v.findViewById(R.id.mapView);
		m.onCreate(savedInstanceState);

		map = m.getMap();
		
		Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
		        .title("Hamburg"));
		

	    
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
