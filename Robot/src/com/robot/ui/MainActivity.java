package com.robot.ui;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.robot.R;

public class MainActivity extends FragmentActivity {

	GlobalBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();
	ConnectivitySelector consel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create the broadcast receiver
		bcr = new GlobalBroadcastReceiver(this);
		
		// open the welcome screen
		setContentView(R.layout.main_activity);

		// fill the welcome screen with the connectivity selection fragment
		if (savedInstanceState == null) {
			consel = new ConnectivitySelector();
			getSupportFragmentManager().beginTransaction().add(R.id.mainFragment, consel).commit();
		}

		// register BlueTooth intent filter to get nofified as BT is connected
		// or disconnected
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		
        // add necessary intent values to be matched for wifi direct
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_connect:
			// connect/disconnect via bluetooth
			// Log.d("main", ""+bcr.isBTconnected());
			consel.connectBT(bcr.isBTconnected());
			return true;
		case R.id.atn_scan_for_wd_peers:
			// connect/disconnect via wifi direct
			consel.searchForPeers();
			return true;
		case R.id.atn_gyro:
			// enable the gyro steering
			// TODO: set this value somewhere
			consel.enableGyro();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister the intent filters together with the broadcastreceiver
		unregisterReceiver(bcr);
	}

	@Override
	public void onResume() {
		super.onResume();
		// register intent filters
		registerReceiver(bcr, intentFilter);
	}

	protected void onDestroy() {
		super.onDestroy();
	}

}
