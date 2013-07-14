package com.robot.ui;

import java.io.IOException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.IntentFilter;

import com.robot.R;
import com.robot.connection.CHBluetooth;
import com.robot.connection.ConnectionHandlerInterface;
import com.robot.connection.ArduinoCommands;

public class ControlUnits extends Fragment implements SensorEventListener {


	public ArduinoCommands driver;

	boolean gyroEnabled = false;
	TextView sensorLabel;
	SensorManager sensorManager = null;
	Sensor sensor;
	float mLastY;

	View mContentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_control_unit, container, false);

		// make known that we want to change the menu with this activity
		setHasOptionsMenu(true);

		// gyro stuff
		sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		sensorLabel = (TextView) mContentView.findViewById(R.id.rotationText);

		ImageView forward = (ImageView) mContentView.findViewById(R.id.forward);
		forward.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					driver.driveSpeed(255);
					driver.forward(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					driver.forward(false);
				}
				return true;
			}
		});

		ImageView back = (ImageView) mContentView.findViewById(R.id.back);
		back.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					driver.driveSpeed(255);
					driver.backward(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					driver.backward(false);
				}
				return true;
			}
		});

		ImageView left = (ImageView) mContentView.findViewById(R.id.left);
		left.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					driver.steerSpeed(255);
					driver.left(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					driver.left(false);
				}
				return true;
			}
		});

		ImageView right = (ImageView) mContentView.findViewById(R.id.right);
		right.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					driver.steerSpeed(255);
					driver.right(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					driver.right(false);
				}
				return true;
			}
		});

		return mContentView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// make our menu buttons visible
		menu.findItem(R.id.atn_connect).setVisible(true);
		menu.findItem(R.id.atn_gyro).setVisible(true);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	// this part handles the steering via the phones internal gyro sensors
	@Override
	public void onSensorChanged(SensorEvent event) {
		boolean isStraight = true;

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
	public void onDestroy() {
		super.onDestroy();
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

	// This class toggles the gyro sensor
	public void enableGyro() {
		gyroEnabled = !gyroEnabled;
	}

}
