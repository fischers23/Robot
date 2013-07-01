package com.robot.control;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class MainControl extends Activity  {

	
	BluetoothBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();
    public boolean btConnected = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		
		
		setContentView(R.layout.activity_main_control);
		
		
		
		

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_control, menu);
        return true;
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
