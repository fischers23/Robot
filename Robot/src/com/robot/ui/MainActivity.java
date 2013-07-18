package com.robot.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.robot.R;
import com.robot.connection.TransferService;

public class MainActivity extends FragmentActivity {

	GlobalBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();
	ConnectivitySelector consel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//remover android status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
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
	
	public void setManager(WifiP2pManager manager){
		bcr.setManager(manager);
	}
	
	public void startTransferService(WifiP2pInfo info,String s){
		
		Intent serviceIntent = new Intent(this, TransferService.class);
		serviceIntent.setAction(TransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(TransferService.EXTRAS_SEND_TEXT, s);
		serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				info.groupOwnerAddress.getHostAddress());
		serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
		startService(serviceIntent);
	}

}
