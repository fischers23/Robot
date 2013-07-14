package com.robot.ui;

import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.robot.R;

public class MainActivity extends FragmentActivity {

	GlobalBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// open the welcome screen
		setContentView(R.layout.main_activity);

		// fill the welcome screen with the connectivity selection fragment
		if (savedInstanceState == null) {
			Fragment consel = new ConnectivitySelector();
			getSupportFragmentManager().beginTransaction().add(R.id.mainFragment, consel).commit();
		}

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_control, menu);
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	// Communication with the Arduino Car by bluetooth
	// called from the layout directly
	public void openController(View v) {

		// register BlueTooth intent filter to get nofified as BT is connected
		// or disconnected
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		


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
		// create the broadcast receiver
		bcr = new GlobalBroadcastReceiver(this);
		// register intent filters
		registerReceiver(bcr, intentFilter);
	}
	
	

}
