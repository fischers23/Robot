package com.robot.ui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.robot.R;
import com.robot.ai.CoordinatePicker;
import com.robot.ai.Navigator;
import com.robot.connection.ArduinoCommands;

public class AIDriver extends Fragment implements CoordinatePicker.OnLocationSelectedListener {

	Navigator navi;

	// the arduino command set
	ArduinoCommands driver = null;
	
	Drawable[] layers;

	View mContentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContentView = inflater.inflate(R.layout.fragment_ai, container, false);

		Resources r = getResources();
		layers = new Drawable[2];
		layers[0] = r.getDrawable(R.drawable.button_arrow);
		layers[1] = r.getDrawable(R.drawable.button_shadow);
		
		
		navi = new Navigator(getActivity());

		Button mapOpen = (Button) mContentView.findViewById(R.id.map_open);
		mapOpen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// initialize the AI fragment
				CoordinatePicker copi = new CoordinatePicker();

				// start the map fragment
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, copi, "copi").addToBackStack("copi").commit();

			}
		});

		return mContentView;
	}


	
	
	public void refresh() {
		TextView tv = (TextView) mContentView.findViewById(R.id.azimuth);
		tv.setText("" + navi.getAzimuth());
//		tv = (TextView) mContentView.findViewById(R.id.pitch);
//		tv.setText("" + navi.getPitch());
//		tv = (TextView) mContentView.findViewById(R.id.roll);
//		tv.setText("" + navi.getRoll());
	}

	public void setCommands(ArduinoCommands ac) {
		driver = ac;
	}
	
	public void drawArrow(Double angle){
		
		
	}
	
	public void onResume(){
		super.onResume();
		navi.init(this);
	}
	
	public void onPause(){
		super.onPause();
		navi.stop();
	}


	@Override
	public void onLocationSelected(LatLng point) {
		// TODO Auto-generated method stub
		
	}


}
