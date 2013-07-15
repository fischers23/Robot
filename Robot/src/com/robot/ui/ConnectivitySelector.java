package com.robot.ui;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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
	
	boolean BTconnected;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_connectivity_selector, container, false);
		mContext = getActivity();

		// make sure there is no pending connection
//		if (cHandler != null && !BTconnected)
//			try {
//				cHandler.closeConnection();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		// This section handles the Bluetooth Button
		ImageButton connectBTButton = (ImageButton) mContentView.findViewById(R.id.connect_bluetooth);
		connectBTButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// if not already done instantiate the BT connection handler
				if (cHandler == null)
					cHandler = new CHBluetooth(getActivity(), "Arduino");

				// create the driver class
				cu = new ControlUnits();
				driver = new ArduinoCommands(cHandler);
				cu.setCommands(driver);

				// open the control screen fragment
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, cu).addToBackStack("cu").commit();
			}
		});

		// This section handles the Wifi Button
		ImageButton connectWifiButton = (ImageButton) mContentView.findViewById(R.id.connect_wifidirect);
		connectWifiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				pl = new PeerList();
				
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, pl, "pl").addToBackStack("pl").commit();
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

		this.BTconnected = BTconnected;
		
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
			cu.buttonsAvailable(View.VISIBLE);
		} else {
			try {
				// if we are already connected -> disconnect
				cHandler.closeConnection();
				cu.buttonsAvailable(View.INVISIBLE);
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
	
	public void searchForPeers(){
		pl.discoverPeers();
	}

}
