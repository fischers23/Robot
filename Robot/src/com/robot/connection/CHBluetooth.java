package com.robot.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

/**
 * This is the bluetooth connection implementation
 * 
 */
public class CHBluetooth implements ConnectionHandlerInterface {

	// bluetooth variables
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice = null;

	// Information to the arduino
	OutputStream mmOutputStream;

	// Information from the arduino (currently unused)
	// InputStream mmInputStream;
	// byte[] readBuffer;
	// int readBufferPosition;

	// Arduino blutooth dongle name
	String deviceName;

	Activity mActivity;

	public CHBluetooth(Activity activity, String deviceName) {
		mActivity = activity;
		this.deviceName = deviceName;
		enableBluetooth();
	}

	/**
	 * Make sure the user has turned bluetoot on. If not ask user to do so.
	 */
	public void enableBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e("SendCommand", "No bluetooth adapter available");
			showSettingsAlert();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			Log.d("SendCommand", "Bluetooth adapter enabled");
			mActivity.startActivity(enableBluetooth);
		}
		Log.d("SendCommand", "Bluetooth adapter ready");
	}

	/**
	 * Find the Arduino bluetooth dongle in the list of paired devices
	 * 
	 * @return
	 */
	public boolean findArduino() {
		// this case assumes we already found the Arduino
		if (mmDevice != null && mmDevice.getName().equals(deviceName))
			return true;
		// find the Arduino in the Androids pairing list
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// avoid null pointer exception
		if (pairedDevices == null)
			return false;
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals(deviceName)) {
					mmDevice = device;
					Log.d("SendCommand", "Bluetooth Device " + mmDevice.getName() + " found in pairing list");
					return true;
				}
			}
		}
		// error message in case we device is not paired
		Log.e("SendCommand", "Bluetooth Device " + deviceName + " not found");
		return false;
	}

	/**
	 * Establish the connection to the bt dongle.
	 */
	public void establishConnection() throws IOException {
		if (findArduino()) {
			// assign standard id
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			// start socket connection
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			// mmInputStream = mmSocket.getInputStream();
			Log.d("SendCommand", "Connected to device opened");
			// beginListenForData();
		}
	}

	/**
	 * Close the connection to the bt dongle.
	 */
	public void closeConnection() throws IOException {
		mmOutputStream.close();
		// mmInputStream.close();
		mmSocket.close();
		Log.d("SendCommand", "Connected to device closed");
	}

	/**
	 * Send data to the bt dongle
	 */
	public void sendData(String s) {
		try {
			mmOutputStream.write(s.getBytes());
		} catch (Exception e) {
			Log.e("CHBluetooth", "error in BT communication");
		}
	}

	/**
	 * Ask user to turn on bt and lead him to the dialogue
	 */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
		alertDialog.setTitle("BT settings");
		alertDialog.setMessage("BT is not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
				mActivity.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

}