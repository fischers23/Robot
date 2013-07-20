package com.robot.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.robot.R;

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
			buttonsAvailable(View.VISIBLE);
			btConnected = true;
			// Toast.makeText(context, "Device is now connected",
			// Toast.LENGTH_LONG).show();
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
			buttonsAvailable(View.INVISIBLE);
			btConnected = false;
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			buttonsAvailable(View.INVISIBLE);
			btConnected = false;
		}

	}

	// toggle the visibility of the steering buttons as
	// we are connected/disconnected
	public void buttonsAvailable(int i) {
//		Log.d("BluetoothBroadcastReceiver", "1");
//		if (mActivity.getFragmentManager().findFragmentByTag("cu") != null) {
//			Log.d("BluetoothBroadcastReceiver", "2");
//			if (mActivity.getFragmentManager().findFragmentByTag("cu").isVisible()) {
//				Log.d("BluetoothBroadcastReceiver", "3");
//				if (i == View.VISIBLE || i == View.INVISIBLE) {
//					mActivity.findViewById(R.id.my_speed).setVisibility(i);
//					mActivity.findViewById(R.id.my_steer).setVisibility(i);
//				}
//			}
//		}
	}

	public boolean isBTconnected() {
		return btConnected;
	}

}