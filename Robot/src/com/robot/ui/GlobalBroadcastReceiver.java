package com.robot.ui;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;

import com.robot.R;

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