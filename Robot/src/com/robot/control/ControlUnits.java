package com.robot.control;


import java.io.IOException;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ControlUnits extends Fragment implements SensorEventListener {

	ConnectionHandlerInterface cHandler;
	Driver driver;
	Thread distanceThread;
	public TextView distanceLabel;

	boolean gyroEnabled = false;
	TextView sensorLabel;
	SensorManager sensorManager = null;
	Sensor sensor;
	float mLastY;
	
	View mContentView; 
	Context mContext;


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_control_unit, null);


		
		
		cHandler = new ConnectionHandlerBluetooth(this, "Arduino");
		driver = new Driver(cHandler);

		mContext = container.getContext();
		sensorManager = (SensorManager) mContext.getSystemService( Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// sensorManager.registerListener(this, sensor,
		// sensorManager.SENSOR_DELAY_GAME);
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

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_control_unit, container, false);

	}



	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

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

			// test
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_connect:
//            	if(!btConnected) {
            	if(true) {
            		Thread connectionThread = new Thread(new Runnable() {
					public void run() {
						// open connection
						try {
							cHandler.establishConnection();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				connectionThread.start();
            	} else { 
            		try {
    					cHandler.closeConnection();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}   
            	}
                return true;
            case R.id.atn_gyro:
            	gyroEnabled = !gyroEnabled;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
	public void onDestroy() {
		super.onDestroy();
		try {
			if (cHandler != null) {
				cHandler.closeConnection();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

}
