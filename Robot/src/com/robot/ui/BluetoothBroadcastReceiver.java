package com.robot.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	Activity mActivity;

	private boolean btConnected = false;

	public BluetoothBroadcastReceiver(Activity activity) {
		mActivity = activity;
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