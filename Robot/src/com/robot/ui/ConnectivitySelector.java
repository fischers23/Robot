package com.robot.ui;

import java.io.IOException;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;
import com.robot.R;
import com.robot.connection.ArduinoCommands;
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

	BluetoothBroadcastReceiver bcr = null;
	private final IntentFilter intentFilter = new IntentFilter();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_connectivity_selector, container, false);
		mContext = getActivity();

		// register BlueTooth intent filter to get nofified as BT is connected
		// or disconnected
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
					cHandler = new CHBluetooth(getActivity(), "Arduino");

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
					cHandler = new CHBluetooth(getActivity(), "Arduino");

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

	// here we handle the bluetoot connection
	// this is called by the menu in the fragment_control_unit
	public void connectBT(boolean connect) {
		// connect
		if (!bcr.isBTconnected() && connect) {
			Thread connectionThread = new Thread(new Runnable() {
				public void run() {
					try {
						// open connection in a thread to avoid ui freezes
						cHandler.establishConnection();
					} catch (IOException e) {
						e.printStackTrace();
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
				e.printStackTrace();
			}
		}
	}

	public void setDestinationLocation(LatLng location) {
		Log.d("CoordinatePicker", location.toString());
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister the intent filters together with the broadcastreceiver
		getActivity().unregisterReceiver(bcr);
	}

	@Override
	public void onResume() {
		super.onResume();
		// register intent filters
		getActivity().registerReceiver(bcr, intentFilter);

		// close existing BT connections
		connectBT(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if (cHandler != null) {
				// close connection on exit
				cHandler.closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
