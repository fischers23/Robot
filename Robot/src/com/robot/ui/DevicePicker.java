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

import java.util.Iterator;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class DevicePicker extends DialogFragment {

	// bluetooth variables
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mmDevice = null;
	
	// use shared preferences to remember the bt device name
	SharedPreferences settings;
    SharedPreferences.Editor editor;

    // list of bt devices
    String devices[];


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// set a title for the dialog
		builder.setTitle("Select Arduino device");

		// initialize the preferences
		settings = getActivity().getSharedPreferences("config", 0);
		editor = settings.edit();
		
		
		
		// initialize bt adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) 
			return builder.create();
		
		// find the Arduino in the Androids pairing list
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices == null)
			return builder.create();

		// create String array
		devices = new String[pairedDevices.size()];

		
		// get the list of bluetooth devices
		Iterator<BluetoothDevice> iter = pairedDevices.iterator();
		
		// iterate through the list
		int index = 0;
		while (iter.hasNext()) {
			devices[index] = iter.next().getName();
			Log.d("DevicePicker", devices[index]);
			index++;
		}

		// create the list
		builder.setItems(devices, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// set the value
				editor.putString("name", devices[arg1]);
				// and commit the edit
			    editor.commit();
			}
		});

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
