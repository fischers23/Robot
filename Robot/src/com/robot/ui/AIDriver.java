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
	Location loc = null;

	// the arduino command set
	ArduinoCommands driver = null;

	Bitmap arrow;
	Bitmap shadow;
	Bitmap newBitmap;

	View mContentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContentView = inflater.inflate(R.layout.fragment_ai, container, false);

		arrow = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_arrow);
		shadow = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_shadow);

		navi = new Navigator(getActivity());

		Button mapOpen = (Button) mContentView.findViewById(R.id.map_open);
		mapOpen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// initialize the Map with the Coordiate Listener
				copi = new CoordinatePicker();

				// start the map fragment
				getFragmentManager().beginTransaction()
						.replace(R.id.mainFragment, copi, "copi")
						.addToBackStack("copi").commit();

			}
		});

		return mContentView;
	}

	public void setCommands(ArduinoCommands ac) {
		driver = ac;
	}

	/**
	 * Draws an arrow pointing towards the destination if such a location is
	 * already set.
	 */
	public void drawBearing() {

		if (loc != null) {
			float angle = navi.getBearing(loc);
			drawArrow(angle);
		}

	}

	
	/**
	 * This function draws the arrow that points towards the destination.
	 * The resulting image is composed of two single images.
	 * 
	 * @param angle angle to rotate the arrow image
	 */
	public void drawArrow(float angle) {

		Matrix matrix = new Matrix();
		matrix.postRotate(angle, arrow.getWidth() / 2, arrow.getHeight() / 2);

		Bitmap finished = Bitmap.createBitmap(arrow.getWidth(),
				arrow.getHeight(), Bitmap.Config.ARGB_8888);

		// Bitmap finished = Bitmap.createBitmap(arrow);
		Canvas c = new Canvas(finished);
		c.drawBitmap(arrow, matrix, null);
		c.drawBitmap(shadow, new Matrix(), null);

		ImageView v = (ImageView) getActivity().findViewById(R.id.compass);
		v.setImageBitmap(finished);
	}

	public void onResume() {
		super.onResume();
		navi.init(this);
		if (loc != null)
			drawBearing();
	}

	public void onPause() {
		super.onPause();
		navi.stop();
	}

	
	/**
	 * Called from the map view to get the position of the desired destination
	 *  
	 * @param location Latitude and Longitude of selected location
	 */
	public void setDestinationLocation(LatLng location) {
		Log.d("CoordinatePicker", location.toString());
		loc = navi.getPosition();
		loc.setLatitude(location.latitude);
		loc.setLongitude(location.longitude);
	}

}
