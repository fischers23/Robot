package com.robot.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import com.robot.ui.ConnectivitySelector;
import com.robot.ui.ControlUnits;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class CHBluetooth implements ConnectionHandlerInterface {

	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice = null;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	byte[] readBuffer;
	String deviceName;
	ConnectivitySelector mActivity;
	int readBufferPosition;

	public CHBluetooth(ConnectivitySelector activity, String deviceName) {
		mActivity = activity;
		this.deviceName = deviceName;
		enableBluetooth();
		findArduino();
	}

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

	public void findArduino() {
		// this case assumes we already found the Arduino
		if (mmDevice != null && mmDevice.getName().equals(deviceName))
			return;
		// find the Arduino in the Androids pairing list
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals(deviceName)) {
					mmDevice = device;
					Log.d("SendCommand", "Bluetooth Device " + mmDevice.getName() + " found in pairing list");
					return;
				}
			}
		}
		// error message in case we device is not paired
		Log.e("SendCommand", "Bluetooth Device " + deviceName + " not found");
	}

	public void establishConnection() throws IOException {
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard
		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		mmSocket.connect();
		mmOutputStream = mmSocket.getOutputStream();
		mmInputStream = mmSocket.getInputStream();
		Log.d("SendCommand", "Connected to device opened");
//		beginListenForData();
	}

	public void closeConnection() throws IOException {
		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();
		Log.d("SendCommand", "Connected to device closed");
	}

	
	public void sendData(String s) throws IOException {
		//TODO: if (bcr.isConnected())
			mmOutputStream.write(s.getBytes());
	}

//	void beginListenForData() {
//		final byte delimiter = 10; // This is the ASCII code for a newline
//									// character
//		readBufferPosition = 0;
//		readBuffer = new byte[1024];
//		
//	    // Handler gets created on the UI-thread
//	    Handler mHandler = mActivity.getWindow().getDecorView().getHandler();
//	    
//	    // This gets executed in a non-UI thread:
//	        mHandler.post(new Runnable() {
//	            @Override
//	            public void run() {
//	            	try {
//						int bytesAvailable = mmInputStream.available();
//						if (bytesAvailable > 0) {
//							byte[] packetBytes = new byte[bytesAvailable];
//							mmInputStream.read(packetBytes);
//							for (int i = 0; i < bytesAvailable; i++) {
//								byte b = packetBytes[i];
//								if (b == delimiter) {
//									byte[] encodedBytes = new byte[readBufferPosition];
//									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//									final String data = new String(encodedBytes, "US-ASCII");
//									readBufferPosition = 0;
//
//									float fl = Float.valueOf(data);
//									mActivity.distanceLabel.setText(Integer.toString((int)fl));
//
//									
//								} else {
//									readBuffer[readBufferPosition++] = b;
//								}
//							}
//						}
//					} catch (Exception ex) {}
//	            	
//	            }
//	        });
//	    
//
//	}

//	public int getDistance() {
//		return distance;
//	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity.getActivity());
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