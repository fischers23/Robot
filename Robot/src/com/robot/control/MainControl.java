package com.robot.control;

import java.io.IOException;

import android.app.Activity;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainControl extends Activity implements SensorEventListener {

	ConnectionHandlerInterface cHandler;
	Driver driver;
	Thread distanceThread;
	public TextView distanceLabel;

	TextView sensorLabel;
	SensorManager sensorManager = null;
	Sensor sensor;
	float mLastY;
	boolean gyroEnabled = false;
	
	BluetoothBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();
    public boolean btConnected = false;


    
	public void setDistance(int distance) {
		distanceLabel.setText(Integer.toString(distance));
	}
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
			
		cHandler = new ConnectionHandlerBluetooth(this, "Arduino");
		driver = new Driver(cHandler);

		setContentView(R.layout.activity_main_control);

		distanceLabel = (TextView) findViewById(R.id.textUltrasonicVlalue);

//		distanceThread = new Thread(new Runnable() {
//			public void run() {
//				while (true) {
//					try {
//						if (bcr.isConnected()) {
//							distanceValue = cHandler.getDistance();
//							Log.d("main", ""+distanceValue);
//							
//						}
//						
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		});
//		distanceThread.start();

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// sensorManager.registerListener(this, sensor,
		// sensorManager.SENSOR_DELAY_GAME);
		sensorLabel = (TextView) findViewById(R.id.rotationText);

		ImageView forward = (ImageView) findViewById(R.id.forward);
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

		ImageView back = (ImageView) findViewById(R.id.back);
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

		ImageView left = (ImageView) findViewById(R.id.left);
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

		ImageView right = (ImageView) findViewById(R.id.right);
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

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_connect:
            	if(!btConnected) {
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

	protected void onDestroy() {
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

	protected void onResume() {
		super.onResume();
		bcr = new BluetoothBroadcastReceiver(this);
		registerReceiver(bcr, intentFilter);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		unregisterReceiver(bcr);
		sensorManager.unregisterListener(this);
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



}
