package com.robot.ai;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Navigator implements SensorEventListener, LocationListener {

	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;
	private Activity mActivity;
	private LocationManager locationManager;

	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;
	
	private double azimuth;
	private double pitch;
	private double roll;

	public Navigator(Activity act) {

		mActivity = act;

	}

	public void init() {

		//initialize the managers an request updates
		sensorManager = (SensorManager) mActivity
				.getSystemService(Context.SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];
		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];
	}

	public void stop() {
		//unregister the listeners
		sensorManager.unregisterListener(this, sensorAccelerometer);
		sensorManager.unregisterListener(this, sensorMagneticField);
		locationManager.removeUpdates(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//calculates the current azimuth, pitch and roll
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++) {
				valuesAccelerometer[i] = event.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++) {
				valuesMagneticField[i] = event.values[i];
			}
			break;
		}

		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				valuesAccelerometer, valuesMagneticField);

		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);

			azimuth = Math.toDegrees(matrixValues[0]);
			pitch = Math.toDegrees(matrixValues[1]);
			roll = Math.toDegrees(matrixValues[2]);

			// readingAzimuth.setText("Azimuth: " + String.valueOf(azimuth));
			// readingPitch.setText("Pitch: " + String.valueOf(pitch));
			// readingRoll.setText("Roll: " + String.valueOf(roll));
		}
	}

	public Location getPosition() {

		return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	public float getBearing(Location dest) {
		
		Location currentPos = getPosition();
		return currentPos.bearingTo(dest);
	}
	
	public double getAzimuth(){
		return azimuth;
	}
	
	public double getPitch(){
		return pitch;
	}
	
	public double getRoll(){
		return roll;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
