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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.robot.connection.SEND_FILE";
	public static final String EXTRAS_SEND_TEXT = "send_text";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

	public TransferService(String name) {
		super(name);
	}

	public TransferService() {
		super("TransferService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		// check the intent if it is the right one. If so extract the IP and
		// port of the file server. Then open a connection an send the message
		// that is delivered in the intent.
		Log.d("TransferService", "handle intent");
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			// read message, IP and port from intent
			String text = intent.getExtras().getString(EXTRAS_SEND_TEXT);
			String host = intent.getExtras().getString(
					EXTRAS_GROUP_OWNER_ADDRESS);
			Socket socket = new Socket();
			int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

			try {
				// open connection
				Log.d("TransferService", "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)),
						SOCKET_TIMEOUT);

				Log.d("TransferService",
						"Client socket - " + socket.isConnected());
				// send message
				OutputStream stream = socket.getOutputStream();
				stream.write(text.getBytes());
			} catch (IOException e) {
				Log.e("TransferService", e.getMessage());
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							// Give up
							e.printStackTrace();
						}
					}
				}
			}

		}

	}

}
