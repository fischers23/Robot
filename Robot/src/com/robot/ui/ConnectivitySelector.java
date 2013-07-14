package com.robot.ui;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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

	View mContentView;
	Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_connectivity_selector, container, false);
		mContext = getActivity();

		// This section handles the Bluetooth Button
		Button connectBTButton = (Button) mContentView.findViewById(R.id.connect_bluetooth);
		connectBTButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// open the control screen fragment
				cu = new ControlUnits();
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, (Fragment) cu).addToBackStack("cu").commit();
			}
		});

		// This section handles the Wifi Button
		Button connectWifiButton = (Button) mContentView.findViewById(R.id.connect_wifidirect);
		connectWifiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// open the control screen fragment
			}
		});
		
		
		return mContentView;
	}

	// proxy for the gyro sensor toggle
	public void enableGyro() {
		cu.enableGyro();
	}

	// here we handle the bluetoot connection
	// this is called by the menu in the fragment_control_unit
	public void connectBT(boolean BTconnected) {
		// if not already done instantiate the BT connection handler
		if (cHandler == null)
			cHandler = new CHBluetooth(this, "Arduino");

		// create the driver class
		driver = new ArduinoCommands(cHandler);
		cu.setCommands(driver);

		// toggle connect
		if (!BTconnected) {
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
		} else {
			try {
				// if we are already connected -> disconnect
				cHandler.closeConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
