package com.robot.connection;

import java.io.IOException;


import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;

public class CHWifiDirect implements ConnectionHandlerInterface {

	private WifiP2pInfo info;
	private Activity act;
	
	public CHWifiDirect(Activity activity, WifiP2pInfo inf){
		act = activity;
		info=inf;
	}
	
	@Override
	public void sendData(String s) throws IOException {

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
		// TODO Auto-generated method stub

	}

	@Override
	public void establishConnection() throws IOException {
		// TODO Auto-generated method stub

	}

}
