package com.robot.connection;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This boradcastreceiver is used to get informed about bluetooth events. It
 * especially filters messages that indicate a connected or disconnected
 * bluetooth device.
 * 
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	private boolean btConnected = false;

	public BluetoothBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		// Bluetooth broadcast receiver
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			btConnected = true;
			// Toast.makeText(context, "Device is now connected",
			// Toast.LENGTH_LONG).show();
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
			btConnected = false;
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			btConnected = false;
		}

	}

	public boolean isBTconnected() {
		return btConnected;
	}

}