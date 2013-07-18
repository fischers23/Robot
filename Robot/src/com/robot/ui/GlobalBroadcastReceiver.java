package com.robot.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GlobalBroadcastReceiver extends BroadcastReceiver {


	Activity mActivity;

	private boolean btConnected = false;

	public GlobalBroadcastReceiver(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		// Bluetooth broadcast receiver
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			// buttonsAvailable(View.VISIBLE);
			btConnected = true;
			// Toast.makeText(context, "Device is now connected",
			// Toast.LENGTH_LONG).show();
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
				.equals(action)) {
			// buttonsAvailable(View.INVISIBLE);
			btConnected = false;
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			// buttonsAvailable(View.INVISIBLE);
			btConnected = false;
		}

		
	}

	public boolean isBTconnected() {
		return btConnected;
	}
	


}