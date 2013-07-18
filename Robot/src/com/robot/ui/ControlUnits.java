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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	// the speed control custom view
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
		Toast.makeText(getActivity(), "Press connect to start!", Toast.LENGTH_LONG).show();

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
					if(Math.abs(last_y-y) < 20)
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
							driver.backwardWithSpeed((int)speed);
						} else {
							driver.backwardWithSpeed(255);
						}
					}
				}
				if (action == MotionEvent.ACTION_UP) {
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
					if(Math.abs(last_x-x) < 20)
						return true;
					last_x = x;
					
					// get the ratio
					float ratio = 255f / (width / 2);

					if (x < width / 2) { // left
						float speed = (((width / 2) - x) * ratio);
						if (speed <= 255f) {
							 driver.leftWithSpeed((int)speed);
						} else {
							driver.driveSpeed(255);
						}
					} else { // right
						float speed = (x - (width / 2)) * ratio;
						if (speed <= 255f) {
							driver.rightWithSpeed((int)speed);
						} else {
							driver.driveSpeed(255);
						}
					}
				}
				if (action == MotionEvent.ACTION_UP) {
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

	public void setCommands(ArduinoCommands ac) {
		driver = ac;
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
		// TODO remove this
		buttonsAvailable(View.VISIBLE);
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
		if (!gyroEnabled)
			sensorLabel.setText("off");
		else
			sensorLabel.setText("on");
	}

	// toggle the visibility of the steering buttons as
	// we are connected/disconnected
	public void buttonsAvailable(int i) {
		if (i == View.VISIBLE || i == View.INVISIBLE) {
			getActivity().findViewById(R.id.my_speed).setVisibility(i);
			getActivity().findViewById(R.id.my_steer).setVisibility(i);

		}
	}

}
