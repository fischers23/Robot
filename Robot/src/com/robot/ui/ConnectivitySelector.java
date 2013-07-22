/*******************************************************************************
 * Copyright 2013 Schulz and Fischer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.robot.ui;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.robot.R;
import com.robot.connection.ArduinoCommands;
import com.robot.connection.BluetoothBroadcastReceiver;
import com.robot.connection.CHBluetooth;
import com.robot.connection.ConnectionHandlerInterface;

public class ConnectivitySelector extends Fragment {

	// create the connection handler object for bluetooth
	ConnectionHandlerInterface cHandler = null;

	// this is the handler for arduino commands
	ArduinoCommands driver;

	// the controller ui
	ControlUnits cu;
	PeerList pl;

	View mContentView;
	Context mContext;

	// variables for the broadcastreceiver
	BluetoothBroadcastReceiver bcr = null;
	private IntentFilter intentFilter;
	boolean keepBTbcr = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_connectivity_selector, container, false);
		mContext = getActivity();

		// register menu
		setHasOptionsMenu(true);
		
		
		//
		SharedPreferences settings = getActivity().getSharedPreferences("config", 0);
		String deviceName = settings.getString("name", "not_set");
		Log.d("ConnectivitySector",deviceName);
		if(deviceName.equals("not_set")) {
			// instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// chain together various setter methods to set the dialog characteristics
			builder.setMessage("Do you want to select one now?")
			       .setTitle("No Arduino selected");
			
			// Add the buttons
			builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               //show dialog
			        	   showDevicePicker();
			           }
			       });
			builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User cancelled the dialog
			        	   // do nothing
			           }
			       });

			// 3. Get the AlertDialog from create()
			AlertDialog dialog = builder.create();
			dialog.show();
		}
			
		// register BlueTooth intent filter to get nofified as BT is connected
		// or disconnected
		intentFilter = new IntentFilter();
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);

		// create the broadcast receiver
		bcr = new BluetoothBroadcastReceiver(getActivity());

		// This section handles the Bluetooth Button
		ImageButton connectBTButton = (ImageButton) mContentView.findViewById(R.id.connect_bluetooth);
		connectBTButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// initialize the fragment
				cu = new ControlUnits();

				// if not already done instantiate the BT connection handler
				if (cHandler == null)
					cHandler = new CHBluetooth(getActivity());

				// create the driver class
				driver = new ArduinoCommands(cHandler);
				cu.setCommands(driver);

				// connect BT
				connectBT(true);

				// open the control screen fragment
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, cu, "cu").addToBackStack("cu").commit();
			}
		});

		// This section handles the Wifi Button
		ImageButton connectWifiButton = (ImageButton) mContentView.findViewById(R.id.connect_wifidirect);
		connectWifiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// initialize the fragment
				pl = new PeerList();

				getFragmentManager().beginTransaction().replace(R.id.mainFragment, pl, "pl").addToBackStack("pl").commit();
			}
		});

		// This section handles the GPS Button
		ImageButton connectAI = (ImageButton) mContentView.findViewById(R.id.connect_ai);
		connectAI.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// initialize the AI fragment
				AIDriver aid = new AIDriver();

				// if not already done instantiate the BT connection handler
				if (cHandler == null)
					cHandler = new CHBluetooth(getActivity());

				// create the driver class
				driver = new ArduinoCommands(cHandler);
				aid.setCommands(driver);

				// connect BT
				connectBT(true);

				getFragmentManager().beginTransaction().replace(R.id.mainFragment, aid, "aid").addToBackStack("aid").commit();
			}
		});

		return mContentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.connectivityselector, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_device_picker:

			// show dialog
			showDevicePicker();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	/**
	 * Shows a dialog to the user to select a paired Arduino device
	 */
	private void showDevicePicker() {
		// create instance of the fragment
		 DevicePicker dp = new DevicePicker();
		 
		// open the device list picker dialog
		 dp.show(getFragmentManager(), "NoticeDialogFragment");
	}
	
	/**
	 * turns the bluetooth connection on (true) or off (false) gets called by
	 * the menu in the fragment_control_unit
	 * 
	 * @param connect
	 */
	public void connectBT(boolean connect) {
		// connect
		if (!bcr.isBTconnected() && connect) {
			keepBTbcr = true;
			Thread connectionThread = new Thread(new Runnable() {
				public void run() {
					try {
						// open connection in a thread to avoid ui freezes
						cHandler.establishConnection();
					} catch (IOException e) {
						Log.e("ConnectivitySelector","Error with connecting to bluetooth.");
					}
				}
			});
			connectionThread.start();
		}
		// disconnect
		if (!connect) {
			try {
				// if we are already connected -> disconnect
				cHandler.closeConnection();
			} catch (Exception e) {
				Log.e("ConnectivitySelector","Error with disconnecting from bluetooth.");
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister the intent filters together with the broadcastreceiver
		if(!keepBTbcr)
			getActivity().unregisterReceiver(bcr);
	}

	@Override
	public void onResume() {
		super.onResume();
		// register intent filters
		getActivity().registerReceiver(bcr, intentFilter);

		// close existing BT connections
		keepBTbcr = false;
		connectBT(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if (cHandler != null) {
				// close connection on exit
				connectBT(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
