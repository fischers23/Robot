package com.robot.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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

	boolean fragActive;
	
	boolean threadRunning = false;
	private Vibrator myVib;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContentView = inflater.inflate(R.layout.fragment_ai, container, false);

		// set menu
		setHasOptionsMenu(true);
		
		// init vibrate
		myVib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

		// init bitmaps
		arrow = BitmapFactory.decodeResource(getResources(), R.drawable.button_arrow);
		shadow = BitmapFactory.decodeResource(getResources(), R.drawable.button_shadow);

		// create navigator
		navi = new Navigator(getActivity());

		// initialize the Map with the Coordiate Listener
		copi = new CoordinatePicker();

		return mContentView;
	}

	/**
	 * possibility to set to control instance this way the Control unit can be
	 * kept independent of the underlying implementation (wifi/bluetooth)
	 * 
	 * @param ac
	 */
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

		// Make rotation matrix
		Matrix matrix = new Matrix();
		matrix.postRotate(angle, arrow.getWidth() / 2, arrow.getHeight() / 2);

		// create a new empty image
		Bitmap finished = Bitmap.createBitmap(arrow.getWidth(), arrow.getHeight(), Bitmap.Config.ARGB_8888);

		// draw the rotated image and the shadow above
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
		// only show drive if a destination is available
		if (loc != null) {
			menu.findItem(R.id.atn_start_drive).setVisible(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_open_map:

			// open the map fragment
			getFragmentManager().beginTransaction().replace(R.id.mainFragment, copi, "copi").addToBackStack("copi").commit();

			return true;
		case R.id.atn_start_drive:

			// start the ai drive
			startAIDrive();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onResume() {
		super.onResume();
		navi.init(this);
		fragActive = true;
		if (loc != null) {
			// remove hint
			getActivity().findViewById(R.id.map_hint).setVisibility(View.GONE);
			getActivity().findViewById(R.id.compass).setVisibility(View.VISIBLE);

			drawBearing();
		}
	}

	public void onPause() {
		super.onPause();
		navi.stop();
		driver.stop();
		fragActive = false;
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

	/**
	 * Brain of our car. This method tracks the position and orientation of the
	 * car and computes the drive commands to go towards the destination
	 */
	public void startAIDrive() {

		// avoid multiple threads
		if(threadRunning)
			return;
		
		threadRunning = true;
		
		Toast.makeText(getActivity(), "Autonomous drive activated. \nWait 20 seconds to start. \nPlease place the device on the car!", Toast.LENGTH_LONG).show();
		
		Thread t = new Thread(new Runnable() {

			public void run() {
				while (fragActive) {
					// wait 20sec to mount phone to car
					SystemClock.sleep(14000);
					// vibrate countdown
					long[] pattern = { 0, 100, 2000, 400, 2000, 1500};
					myVib.vibrate(pattern, -1);
					SystemClock.sleep(6000);
					
					
					// begin AI drive by turning towards the destination
					alignToDest();

					// as long as the destination is not reached and the
					// fragment is active drive to destination
					while (!destinationReached() && fragActive) {

						// if the destination is to much aside adjust direction
						if (Math.abs(deltaAngle()) > 30) {
							driver.stop();
							alignToDest();
						}
						Log.d("AIDriver", "Destination ho!");
						driver.forwardWithSpeed(255);
						SystemClock.sleep(1000);
					}

					// destination reached -> stop the car
					driver.stop();
					
					// reset boolean
					threadRunning = false;
				}
			}
			


			private boolean destinationReached() {

				// check if destination is in reach (5m)
				if (navi.getPosition().distanceTo(loc) <= 5) {
					Log.d("AIDriver", "DESTINATION REACHED!");
					return true;
				}
				return false;
			}

			private float deltaAngle() {
				float delta = navi.getAzimuth() - bearing;
				Log.d("AIDriver", "Delta: " + delta);
				return delta;
			}

			private void alignToDest() {

				Log.d("AIDriver", "Aligning to destination");
				while (Math.abs(deltaAngle()) > 30 && fragActive) {
					float az = navi.getAzimuth();
					float angle;
					if (az < bearing) {
						angle = (Math.abs(bearing - az));
					} else {
						angle = (360 - Math.abs(bearing - az));
					}
					Log.d("AI", ""+angle);
					if (angle <= 180) {
						Log.d("AI", "right");
						driver.rightWithServo(255);
					} else {
						Log.d("AI", "left");
						driver.leftWithServo(255);
					}
					SystemClock.sleep(1000);
				}
				driver.stop();
			}
		});
		t.start();

	}

}
