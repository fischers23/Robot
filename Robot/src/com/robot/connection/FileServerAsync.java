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
package com.robot.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

/**
 * This class opens a single socket server that relays everything that is sent
 * to it over wifi to the attached buetooth device
 * 
 */
public class FileServerAsync extends AsyncTask<Void, Void, String> {

	EditText msg;
	Activity act;
	private ConnectionHandlerInterface cHandler;
	private boolean listen = true;

	public FileServerAsync(Activity act) {
		this.act = act;
		// if not already done instantiate the BT connection handler
		if (cHandler == null) {
			cHandler = new CHBluetooth(act);
			try {
				cHandler.establishConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.d("FileServer", "Server created");

	}

	@Override
	protected String doInBackground(Void... arg0) {

		try {

			// listen as long as we explicitly want to stop
			while (listen) {
				// open server socket and bind it to port 8988
				ServerSocket serverSocket = new ServerSocket(8988);
				Log.d("FileServer", "Server: Socket opened");
				// wait for connection
				Socket client = serverSocket.accept();
				Log.d("FileServer", "Server: connection done");
				// read string von input stream
				InputStream inputstream = client.getInputStream();
				java.util.Scanner s = new java.util.Scanner(inputstream)
						.useDelimiter("\\A");
				String text = s.hasNext() ? s.next() : "";
				serverSocket.close();
				if (text != "") {
					Log.d("FileServer", text);
					// relay the received strings to the connected bluetooth
					// device
					cHandler.sendData(text);
				}

			}
			return "";
		} catch (IOException e) {
			Log.e("FileServer", e.getMessage());
			return "";
		}
	}

	public void stop() {
		try {
			// close bluetooth connection and set flag to stop listening to wifi
			// connections
			cHandler.closeConnection();
			listen = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
