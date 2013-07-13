package com.robot.ui;

import com.robot.R;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity {

	BroadcastReceiverBT bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();
	ControlUnits cu = new ControlUnits();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// register BlueTooth intent filter to get nofified as BT is connected or disconnected
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		
		
		// open the welcome screen
		setContentView(R.layout.welcomescreen);

	}

	// Communication with the Arduino Car by bluetooth
	// called from the layout directly
	public void openController(View v) {
		

		
		// open the controll screen
		setContentView(R.layout.activity_main_control);
		if (findViewById(R.id.mainFragment) != null) {
			getSupportFragmentManager().beginTransaction().add(R.id.mainFragment, cu).commit();
		}

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
			cu.connectBT();
			return true;
		case R.id.atn_gyro:
			// enable the gyro steering
			cu.enableGyro();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onResume() {
		super.onResume();
		// create and register the bluetooth broadcast receiver
		bcr = new BroadcastReceiverBT(cu);
		registerReceiver(bcr, intentFilter);
	}

	protected void onPause() {
		super.onPause();
		unregisterReceiver(bcr);
	}

	protected void onDestroy() {
		super.onDestroy();
	}

}
