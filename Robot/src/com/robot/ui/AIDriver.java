package com.robot.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.robot.R;
import com.robot.ai.CoordinatePicker;
import com.robot.ai.Navigator;
import com.robot.connection.ArduinoCommands;

public class AIDriver extends Fragment {

	Navigator navi;
	CoordinatePicker copi;

	// the arduino command set
	ArduinoCommands driver = null;
	

	
	Bitmap arrow;
	Bitmap shadow;
	Bitmap newBitmap;

	View mContentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContentView = inflater.inflate(R.layout.fragment_ai, container, false);

		arrow = BitmapFactory.decodeResource(getResources(), R.drawable.button_arrow);
		shadow = BitmapFactory.decodeResource(getResources(), R.drawable.button_shadow);
		
		
		
		navi = new Navigator(getActivity());

		Button mapOpen = (Button) mContentView.findViewById(R.id.map_open);
		mapOpen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// initialize the Map with the Coordiate Listener
				copi = new CoordinatePicker();

				// start the map fragment
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, copi, "copi").addToBackStack("copi").commit();

			}
		});

		return mContentView;
	}


	public void setCommands(ArduinoCommands ac) {
		driver = ac;
	}
	
	public void drawBearing(Location dest){
		
		double angle = navi.getBearing(dest)+90;
		drawArrow(angle);
	}
	
	public void drawArrow(Double angle){
		
		

		Matrix matrix = new Matrix();
		matrix.postRotate(Float.valueOf(""+angle), arrow.getWidth()/2, arrow.getHeight()/2);
        
		
		Bitmap finished = Bitmap.createBitmap(arrow.getWidth(), arrow.getHeight(), Bitmap.Config.ARGB_8888);
		
//		Bitmap finished = Bitmap.createBitmap(arrow);
		Canvas c = new Canvas(finished);
		c.drawBitmap(arrow, matrix, null);
		c.drawBitmap(shadow, new Matrix(), null);
		
		ImageView v = (ImageView) getActivity().findViewById(R.id.compass);
		v.setImageBitmap(finished);
	}
	
	public void onResume(){
		super.onResume();
		navi.init(this);
	}
	
	public void onPause(){
		super.onPause();
		navi.stop();
	}


	public void setDestinationLocation(LatLng location) {
		Log.d("CoordinatePicker", location.toString());
		Location loc = navi.getPosition();
		loc.setLatitude(location.latitude);
		loc.setLongitude(location.longitude);
		drawBearing(loc);
	}
	
}
