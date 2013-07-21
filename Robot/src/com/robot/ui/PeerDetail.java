package com.robot.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.robot.R;
import com.robot.connection.ArduinoCommands;
import com.robot.connection.CHWifiDirect;
import com.robot.connection.FileServerAsync;

public class PeerDetail extends Fragment implements ConnectionInfoListener {

	private WifiP2pDevice device;
	private View mContentView;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private IntentFilter intentFilter = new IntentFilter();
	private WifiDetailReceiver wdr;
	private WifiP2pInfo info;
	private CHWifiDirect wifi;
	private ArduinoCommands ac;
	private ControlUnits cu;
	private FileServerAsync fs = null;
	private boolean connected = false;

	public void init(WifiP2pDevice target, WifiP2pManager man, Channel chan) {

		device = target;
		mManager = man;
		mChannel = chan;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_wifi_detail,
				container, false);

		wdr = new WifiDetailReceiver(mManager, mChannel, this);

		mContentView.findViewById(R.id.btn_start_client).setVisibility(
				View.GONE);
		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						// if a connection is established this button is shown.
						// If it is pushed it makes sure that the control units
						// fragment is shown an the commands are sent via wifi
						wifi = new CHWifiDirect(getActivity(), info);
						ac = new ArduinoCommands(wifi);
						cu = new ControlUnits();
						cu.setCommands(ac);
						getFragmentManager().beginTransaction()
								.replace(R.id.mainFragment, cu)
								.addToBackStack("cu").commit();

					}
				});

		return mContentView;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {

		connected = true;
		getActivity().invalidateOptionsMenu();
		this.info = info;
		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText("Am I the Groupowner? "
				+ ((info.isGroupOwner == true) ? "YES" : "NO"));

		Button b = (Button) mContentView.findViewById(R.id.btn_start_client);
		b.setVisibility((info.isGroupOwner == true) ? View.GONE : View.VISIBLE);

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - "
				+ info.groupOwnerAddress.getHostAddress());

		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.
		if (info.groupFormed && info.isGroupOwner) {
			fs = new FileServerAsync(getActivity());
			fs.execute();
		}

	}

	public void onResume() {
		super.onResume();
		intentFilter = new IntentFilter();
		// add necessary intent values to be matched for wifi direct and
		// register the receiver
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		getActivity().registerReceiver(wdr, intentFilter);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.wifi_detail_menu, menu);
		if(connected){
			menu.findItem(R.id.atn_wifi_connect).setVisible(false);
			menu.findItem(R.id.atn_wifi_disconnect).setVisible(true);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atn_wifi_connect:
			// connect via wifi direct
			wifiConnect();
			return true;
		case R.id.atn_wifi_disconnect:
			// disconnect via wifi direct
			wifiDisconnect();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void wifiConnect() {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;

		// connect to device
		mManager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				Log.d("PeerDetail", "conecting...");
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(getActivity(), "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void wifiDisconnect() {
		// disconnect from device
		mManager.removeGroup(mChannel, new ActionListener() {

			@Override
			public void onFailure(int reasonCode) {
				Log.d("PeerDetail", "Disconnect failed. Reason :" + reasonCode);

			}

			@Override
			public void onSuccess() {

			}

		});
	}

	public void onPause() {
		// unregister the receiver and stop running file server to prevent error
		// messages
		super.onPause();
		getActivity().unregisterReceiver(wdr);
		if (fs != null)
			fs.stop();
	}
}

/**
 * Broadcastreceiver that handles only the wifi direct intents. Especially those
 * regarding the connection handling
 * 
 */
class WifiDetailReceiver extends BroadcastReceiver {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private PeerDetail pl;

	public WifiDetailReceiver(WifiP2pManager manager, Channel channel,
			PeerDetail peerDetail) {
		mManager = manager;
		mChannel = channel;
		pl = peerDetail;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			Log.d("WifiDietailReceiver", "Got some info");
			if (mManager == null) {
				return;
			}
			Log.d("WifiDetailReceiver", "Manager != null");
			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			Log.d("WifiDetailReceiver",
					(networkInfo.isConnected() == true) ? "true" : "false");
			if (networkInfo.isConnected()) {
				// we are connected with the other device, request connection
				// info to find group owner IP
				mManager.requestConnectionInfo(mChannel, pl);
			}
		}
	}

}