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

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

public class CHWifiDirect implements ConnectionHandlerInterface {

	private WifiP2pInfo info;
	private Activity act;

	public CHWifiDirect(Activity activity, WifiP2pInfo inf) {
		act = activity;
		info = inf;
	}

	/**
	 * Creates an intent service that contains the message and die information
	 * of the file server
	 */
	@Override
	public void sendData(String s) {

		Log.d("WIFIHandler", "making Intent");
		Log.d("WIFIHandler", "send String: " + s);

		Intent serviceIntent = new Intent(act, TransferService.class);
		serviceIntent.setAction(TransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(TransferService.EXTRAS_SEND_TEXT, s);
		serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				info.groupOwnerAddress.getHostAddress());
		serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
		act.startService(serviceIntent);

	}

	@Override
	public void closeConnection() throws IOException {
		// handling for disconnect in PeerDetail

	}

	@Override
	public void establishConnection() throws IOException {
		// handling for connection establishment in PeerDetail

	}

}
