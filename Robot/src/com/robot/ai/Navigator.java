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
import android.support.v4.app.Fragment;

import com.robot.ui.AIDriver;

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

	private float azimuth;

	private Fragment frag;

	public Navigator(Activity act) {

		mActivity = act;
		frag = null;

	}

	/**
	 * 
	 * This function initializes the sensor and location managers and requests
	 * updates from these services.
	 * 
	 * @param f
	 *            handle to calling fragment
	 */
	public void init(Fragment f) {

		frag = f;
		// initialize the managers an request updates
		sensorManager = (SensorManager) mActivity
				.getSystemService(Context.SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, sensorMagneticField,
				SensorManager.SENSOR_DELAY_GAME);

		locationManager = (LocationManager) mActivity
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];
		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];
	}

	/**
	 * Call this function to unregister all listeners to prefent errors
	 */
	public void stop() {
		// unregister the listeners
		sensorManager.unregisterListener(this, sensorAccelerometer);
		sensorManager.unregisterListener(this, sensorMagneticField);
		locationManager.removeUpdates(this);
		frag = null;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * This callback function is used to determine the azimuth of the phone. The
	 * azimuth is the rotation regarding the z-axis.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {

		// calculates the current azimuth, pitch and roll
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			valuesAccelerometer = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			valuesMagneticField = event.values.clone();
			break;
		}

		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				valuesAccelerometer, valuesMagneticField);

		if (success) {
			float[] remappedR = new float[9];
			SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X,
					SensorManager.AXIS_Z, remappedR);
			SensorManager.getOrientation(matrixR, matrixValues);

			azimuth = (float) Math.round(Math.toDegrees(matrixValues[0]));
			azimuth += 90; // correction due to landscape orientation
			azimuth = (azimuth + 360) % 360;

			// readingAzimuth.setText("Azimuth: " + String.valueOf(azimuth));
			// readingPitch.setText("Pitch: " + String.valueOf(pitch));
			// readingRoll.setText("Roll: " + String.valueOf(roll));
		}
	}

	/**
	 * This function determines the last know position and returns it in a
	 * Location object
	 * 
	 * @return current Location
	 */
	public Location getPosition() {

		return locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 
	 * This function determines the angle between the true north and the desired
	 * destination
	 * 
	 * @param dest
	 *            destination position
	 * @return the angle to destination
	 */
	public float getBearing(Location dest) {

		Location currentPos = getPosition();
		return currentPos.bearingTo(dest);
	}

	public float getAzimuth() {
		return azimuth;
	}

	/**
	 * This callback method updates the bearing to the destination an causes the
	 * arrow to redraw (if an destination is set)
	 */
	@Override
	public void onLocationChanged(Location location) {

		if (frag != null)
			((AIDriver) frag).drawBearing();
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
