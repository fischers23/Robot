package de.fischer.wifidirect;

import java.util.Collection;
import java.util.Iterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements PeerListListener,
		ConnectionInfoListener {

	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	Button search;
	Button connect;
	Button send;
	Collection<WifiP2pDevice> list;
	WifiP2pInfo info;

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

		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText text = (EditText) findViewById(R.id.sendTxt);
				send(text.getText().toString());
				
			}
		});

	}
	
	public void send(String text){
		// User has picked an image. Transfer it to group owner i.e peer
		// using
		// FileTransferService.

		Log.d("MainActivity", "Intent----------- " + text);
		Intent serviceIntent = new Intent(this,
				TransferService.class);
		serviceIntent.setAction(TransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(TransferService.EXTRAS_SEND_TEXT,
				text);
		serviceIntent.putExtra(
				TransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				info.groupOwnerAddress.getHostAddress());
		serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_PORT,
				8988);
		startService(serviceIntent);

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

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo arg0) {
		// TODO Auto-generated method stub
		info = arg0;
		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.
		if (info.groupFormed && info.isGroupOwner) {
			new FileServerAsync(this).execute();
		} else if (info.groupFormed) {
			// The other device acts as the client. In this case, we enable the
			// get file button.
			findViewById(R.id.send).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.connect).setVisibility(View.GONE);
	}
}
