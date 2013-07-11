package com.robot.control;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainControl extends FragmentActivity {

	BluetoothBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();
	public boolean btConnected = false;
	ControlUnits cu = new ControlUnits();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);

		setContentView(R.layout.welcomescreen);

		
		

	}

	
	public void openController(View v) {
		setContentView(R.layout.activity_main_control);
		if(findViewById(R.id.mainFragment)!=null) {
			
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
			cu.connectBT(btConnected);
			return true;
		case R.id.atn_gyro:
			cu.enableGyro();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onResume() {
		super.onResume();
		bcr = new BluetoothBroadcastReceiver(this);
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
