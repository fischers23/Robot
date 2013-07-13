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

public class BroadcastReceiverBT extends BroadcastReceiver {
	BluetoothAdapter mBluetoothAdapter;
	String deviceName;
	ControlUnits mActivity;


	public BroadcastReceiverBT(ControlUnits activity) {
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
		}

	}

	private void buttonsAvailable(int i) {
		if (i == View.VISIBLE || i == View.INVISIBLE) {
			mActivity.getActivity().findViewById(R.id.forward).setVisibility(i);
			mActivity.getActivity().findViewById(R.id.back).setVisibility(i);
			mActivity.getActivity().findViewById(R.id.left).setVisibility(i);
			mActivity.getActivity().findViewById(R.id.right).setVisibility(i);
		}
	}

}