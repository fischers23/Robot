package de.fischer.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class BCReceiver extends BroadcastReceiver{

	private WifiP2pManager mManager;
	private Channel mChannel;
	private MainActivity mActivity;
	

	public BCReceiver(WifiP2pManager manager, Channel channel,
			MainActivity activity) {
		
		super();
		this.mManager = manager;
		this.mChannel = channel;
		this.mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct is enabled
            } else {
                // Wi-Fi Direct is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	if (mManager != null) {
                mManager.requestPeers(mChannel, mActivity);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

	}


}
