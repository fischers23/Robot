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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
	float bearing;

	// the arduino command set
	ArduinoCommands driver = null;

	Bitmap arrow;
	Bitmap shadow;
	Bitmap newBitmap;

	View mContentView;
	MenuItem i;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContentView = inflater.inflate(R.layout.fragment_ai, container, false);

		setHasOptionsMenu(true);

		arrow = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_arrow);
		shadow = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_shadow);

		navi = new Navigator(getActivity());

		// initialize the Map with the Coordiate Listener
		copi = new CoordinatePicker();

		// click on the compass to start the autonomous drive
		ImageView iv = (ImageView) getActivity().findViewById(R.id.compass);
		iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startAIDrive();

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
			bearing = navi.getBearing(loc);
			// bearing is the angle on a north faced map
			// adjust the angle regarding the direction in which we look
			float az = navi.getAzimuth();
			if (az < bearing)
				drawArrow(Math.abs(bearing - az));
			else
				drawArrow(360 - Math.abs(bearing - az));
		}

	}

	/**
	 * This function draws the arrow that points towards the destination. The
	 * resulting image is composed of two single images.
	 * 
	 * @param angle
	 *            angle to rotate the arrow image
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.ai_menu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_open_map:

			// open the map fragment
			getFragmentManager().beginTransaction()
					.replace(R.id.mainFragment, copi, "copi")
					.addToBackStack("copi").commit();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onResume() {
		super.onResume();
		navi.init(this);
		if (loc != null) {
			// remove hint
			getActivity().findViewById(R.id.map_hint).setVisibility(View.GONE);
			getActivity().findViewById(R.id.compass)
					.setVisibility(View.VISIBLE);

			drawBearing();
		}
	}

	public void onPause() {
		super.onPause();
		navi.stop();
	}

	/**
	 * Called from the map view to get the position of the desired destination
	 * 
	 * @param location
	 *            Latitude and Longitude of selected location
	 */
	public void setDestinationLocation(LatLng location) {
		Log.d("CoordinatePicker", location.toString());
		loc = navi.getPosition();
		loc.setLatitude(location.latitude);
		loc.setLongitude(location.longitude);
	}

	public void startAIDrive() {

		alignToDest();

		while (!destinationReached()) {

			if (Math.abs(deltaAngle()) > 10f) {
				driver.stop();
				alignToDest();
			}
			driver.forward(true);
		}

	}

	/**
	 * 
	 * @return boolean
	 */
	private boolean destinationReached() {

		if (navi.getPosition().distanceTo(loc) <= 5) {
			return true;
		}
		return false;
	}

	private float deltaAngle() {
		return navi.getAzimuth() - bearing;
	}

	private void alignToDest() {

		while (Math.abs(deltaAngle()) > 10) {
			if ((navi.getAzimuth() - bearing) > 0)
				driver.left(true);
			else
				driver.right(true);
		}
		driver.stop();
	}

}
