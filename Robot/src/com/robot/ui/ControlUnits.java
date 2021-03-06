/*******************************************************************************
 * Copyright 2013 Schulz and Fischer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.robot.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robot.R;
import com.robot.connection.ArduinoCommands;

public class ControlUnits extends Fragment implements SensorEventListener {

	// the arduino command set
	ArduinoCommands driver = null;

	// gyro sensor and label
	boolean gyroEnabled = false;
	TextView sensorLabel;
	SensorManager sensorManager = null;
	Sensor sensor;
	float mLastY;
	boolean isStraight = true;
	
	// speed control custom view
	ImageView speed_view;
	ImageView steer_view;
	float last_y = 0;
	float last_x = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View mContentView = inflater.inflate(R.layout.fragment_control_unit, container, false);

		// make known that we want to change the menu with this activity
		setHasOptionsMenu(true);

		// gyro stuff
		sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		sensorLabel = (TextView) mContentView.findViewById(R.id.rotationText);

		// forward/backward driving
		speed_view = (ImageView) mContentView.findViewById(R.id.my_speed);
		speed_view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = MotionEventCompat.getActionMasked(event);

				if ((action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN) && action != MotionEvent.ACTION_OUTSIDE) {
					// get the height of our view
					int height = speed_view.getHeight();
					float y = event.getY();

					// do not send to often
					if (Math.abs(last_y - y) < 20)
						return true;
					last_y = y;

					// get the ratio
					float ratio = 255f / (height / 2);

					if (y < height / 2) { // forward
						float speed = (((height / 2) - y) * ratio);
						if (speed <= 255f) {
							driver.forwardWithSpeed((int) speed);
						} else {
							driver.forwardWithSpeed(255);
						}
					} else { // backward
						float speed = (y - (height / 2)) * ratio;
						if (speed <= 255f) {
							driver.backwardWithSpeed((int) speed);
						} else {
							driver.backwardWithSpeed(255);
						}
					}
				}
				if (action == MotionEvent.ACTION_UP) {
					// stop on finger release
					last_y = 0;
					driver.forward(false);
					driver.backward(false);
				}
				return true;
			}
		});

		// left/right steering
		steer_view = (ImageView) mContentView.findViewById(R.id.my_steer);
		steer_view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = MotionEventCompat.getActionMasked(event);

				if ((action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN) && action != MotionEvent.ACTION_OUTSIDE) {
					// get the width of our view
					int width = steer_view.getWidth();

					// do not send to often
					float x = event.getX();
					if (Math.abs(last_x - x) < 20)
						return true;
					last_x = x;

					// get the ratio
					float ratio = 255f / (width / 2);

					if (x < width / 2) { // left
						float speed = (((width / 2) - x) * ratio);
						if (speed <= 255f) {
							driver.leftWithSpeed((int) speed);
						} else {
							driver.driveSpeed(255);
						}
					} else { // right
						float speed = (x - (width / 2)) * ratio;
						if (speed <= 255f) {
							driver.rightWithSpeed((int) speed);
						} else {
							driver.driveSpeed(255);
						}
					}
				}
				if (action == MotionEvent.ACTION_UP) {
					// stop on finger release
					last_x = 0;
					driver.left(false);
					driver.right(false);
				}
				return true;

			}
		});

		return mContentView;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.controlunits, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_gyro:
			// enable the gyro steering
			enableGyro();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// nothing to do here.. :)
	}

	/**
	 *  possibility to set to control instance
	 *  this way the control unit can be kept independent
	 *  of the underlying implementation (wifi/bluetooth)
	 * @param ac
	 */
	public void setCommands(ArduinoCommands ac) {
		driver = ac;
	}

	// this part handles the steering via the phones internal gyro sensors
	@Override
	public void onSensorChanged(SensorEvent event) {


		float noise = (float) 0.1;
		float y = event.values[1];
		float deltaY = Math.abs(mLastY - y);
		if (deltaY > noise && gyroEnabled) {
			mLastY = y;
			float value = 255 * (y / 10);
			sensorLabel.setText(Float.toString(Math.round(value)));

			if (value < -80) { // left
				isStraight = false;
				driver.steerSpeed(Math.abs((int) value));
				driver.left(true);
			} else if (value > 80) { // right
				isStraight = false;
				driver.steerSpeed(Math.abs((int) value));
				driver.right(true);
			} else if (value > -79 && value < 79 && !isStraight) { // straight
				isStraight = true;
				driver.left(false);
				driver.right(false);
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// register the gyro sensor
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister gyro steering sensors
		sensorManager.unregisterListener(this);
	}

	/**
	 * gyro sensor toggle control
	 */
	public void enableGyro() {
		gyroEnabled = !gyroEnabled;
		if (!gyroEnabled)
			sensorLabel.setText("off");
		else
			sensorLabel.setText("on");
	}

}
