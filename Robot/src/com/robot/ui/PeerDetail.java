package com.robot.ui;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.robot.R;
import com.robot.connection.FileServerAsync;

public class PeerDetail extends Fragment implements ConnectionInfoListener {

	private WifiP2pDevice device;
	private View mContentView;
	private WifiP2pManager mManager;
	private Channel mChannel;

	public void setDevice(WifiP2pDevice target) {

		device = target;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_wifi_detail,
				container, false);

		mContentView.findViewById(R.id.btn_connect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						WifiP2pConfig config = new WifiP2pConfig();
						config.deviceAddress = device.deviceAddress;
						config.wps.setup = WpsInfo.PBC;
						
						mManager.connect(mChannel, config, new ActionListener() {

				            @Override
				            public void onSuccess() {
				                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
				            }

				            @Override
				            public void onFailure(int reason) {
				                Toast.makeText(getActivity(), "Connect failed. Retry.",
				                        Toast.LENGTH_SHORT).show();
				            }
				        });

					}
				});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
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
				});

		return mContentView;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {

		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText("Am I th Groupowner? "
				+ ((info.isGroupOwner == true) ? "YES" : "NO"));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - "
				+ info.groupOwnerAddress.getHostAddress());

		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.
		if (info.groupFormed && info.isGroupOwner) {
			new FileServerAsync(getActivity()).execute();
		} else if (info.groupFormed) {
			// The other device acts as the client. In this case, we enable the
			// get file button.
			mContentView.findViewById(R.id.btn_start_client).setVisibility(
					View.VISIBLE);
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);

	}
}