package de.fischer.wifidirect;

import java.util.Collection;
import java.util.Iterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements PeerListListener {

	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	Button search;
	Button connect;
	Collection<WifiP2pDevice> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mManager.discoverPeers(mChannel,
						new WifiP2pManager.ActionListener() {
							@Override
							public void onSuccess() {
								Log.d("Main", "Peer discovery successful");
							}

							@Override
							public void onFailure(int reasonCode) {
								Log.d("Main", "Peer discovery failed");
							}
						});
			}
		});

		connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Iterator<WifiP2pDevice> it = list.iterator();
				WifiP2pDevice device = null;
				device = it.next();
				if (device != null) {
					WifiP2pConfig config = new WifiP2pConfig();
					config.deviceAddress = device.deviceAddress;
					mManager.connect(mChannel, config, new ActionListener() {

						@Override
						public void onSuccess() {
							Log.d("Main", "Connection successful");

						}

						@Override
						public void onFailure(int reason) {
							Log.d("Main", "Connection faild");
						}
					});
				}

			}
		});
	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new BCReceiver(mManager, mChannel, this);
		registerReceiver(mReceiver, mIntentFilter);
	}

	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {

		list = peers.getDeviceList();

		findViewById(R.id.connect).setVisibility(View.VISIBLE);

	}
}
