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

public class ConnectionBroadcastReceiver extends BroadcastReceiver {
	BluetoothAdapter mBluetoothAdapter;
	String deviceName;
	MainActivity mActivity;
	
	private WifiP2pManager mManager;
	private Channel mChannel;

	public ConnectionBroadcastReceiver(MainActivity activity) {
		mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// BluetoothDevice device =
		// intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			buttonsAvailable(View.VISIBLE);
			mActivity.btConnected = true;
			Toast.makeText(context, "Device is now connected", Toast.LENGTH_LONG).show();
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
			buttonsAvailable(View.INVISIBLE);
			mActivity.btConnected = false;
			// Toast.makeText(mContext, device.getName() +
			// " Device is about to disconnect", Toast.LENGTH_LONG).show();
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			buttonsAvailable(View.INVISIBLE);
			// Toast.makeText(mContext, device.getName() +
			// " Device has disconnected", Toast.LENGTH_LONG).show();
			mActivity.btConnected = false;
		} else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct is enabled
            } else {
                // Wi-Fi Direct is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	if (mManager != null) {
                mManager.requestPeers(mChannel, mActivity);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        	if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP

                
                mManager.requestConnectionInfo(mChannel, mActivity);
            } else {
                // It's a disconnect
               // mActivity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

	}

	private void buttonsAvailable(int i) {
		if (i == View.VISIBLE || i == View.INVISIBLE) {
			mActivity.findViewById(R.id.forward).setVisibility(i);
			mActivity.findViewById(R.id.back).setVisibility(i);
			mActivity.findViewById(R.id.left).setVisibility(i);
			mActivity.findViewById(R.id.right).setVisibility(i);
		}
	}

}