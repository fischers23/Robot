package com.robot.connection;

import java.io.IOException;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import com.robot.ui.MainActivity;

public class CHWifiDirect implements ConnectionHandlerInterface {

	private WifiP2pInfo info;
	private MainActivity act;
	
	public CHWifiDirect(Activity activity, WifiP2pInfo inf){
		act = (MainActivity) activity;
		info=inf;
	}
	
	@Override
	public void sendData(String s) {

		Log.d("WIFIHandler","making Intent");
		Log.d("WIFIHandler","send String: " + s);
		act.startTransferService(info, s);

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
