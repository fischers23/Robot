package com.robot.ui;

import com.robot.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.view.View;
import android.widget.Toast;

public class GlobalBroadcastReceiver extends BroadcastReceiver {
	BluetoothAdapter mBluetoothAdapter;
	String deviceName;
	ControlUnits mActivity;
	private WifiP2pManager mManager;
	private Channel mChannel;

	public GlobalBroadcastReceiver(ControlUnits activity) {
		mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		// Bluetooth broadcast receiver
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			buttonsAvailable(View.VISIBLE);
			mActivity.btConnected = true;
			Toast.makeText(context, "Device is now connected", Toast.LENGTH_LONG).show();
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
			buttonsAvailable(View.INVISIBLE);
			mActivity.btConnected = false;
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			buttonsAvailable(View.INVISIBLE);
			mActivity.btConnected = false;
		}

		// Wifi-Direct broadcast receiver
		// else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
		// {
		// int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
		// if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
		// // Wifi Direct is enabled
		// } else {
		// // Wi-Fi Direct is not enabled
		// }
		// } else if
		// (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
		// if (mManager != null) {
		// mActivity.setContentView(R.layout.activity_detect_peers);
		// mManager.requestPeers(mChannel, (PeerListListener)
		// mActivity.getFragmentManager().findFragmentById(R.id.peerList));
		// }
		// } else if
		// (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
		// // Respond to new connection or disconnections
		// if (mManager == null) {
		// return;
		// }
		//
		// NetworkInfo networkInfo = (NetworkInfo)
		// intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
		//
		// if (networkInfo.isConnected()) {
		//
		// // we are connected with the other device, request connection
		// // info to find group owner IP
		//
		// mManager.requestConnectionInfo(mChannel, mActivity);
		// } else {
		// // It's a disconnect
		// // mActivity.resetData();
		// }
		// } else if
		// (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
		// // Respond to this device's wifi state changing
		// }

	}

	// the broadcast receiver toggles the visibility of the steering buttons as
	// we are connected/disconnected
	private void buttonsAvailable(int i) {
		if (i == View.VISIBLE || i == View.INVISIBLE) {
			mActivity.getActivity().findViewById(R.id.forward).setVisibility(i);
			mActivity.getActivity().findViewById(R.id.back).setVisibility(i);
			mActivity.getActivity().findViewById(R.id.left).setVisibility(i);
			mActivity.getActivity().findViewById(R.id.right).setVisibility(i);
		}
	}

}