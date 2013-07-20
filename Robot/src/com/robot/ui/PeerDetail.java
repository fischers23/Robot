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
				                Log.d("PeerDetail","conecting...");
				            }

				            @Override
				            public void onFailure(int reason) {
				                Toast.makeText(getActivity(), "Connect failed. Retry.",
				                        Toast.LENGTH_SHORT).show();
				            }
				        });

					}
				});
		
		mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.GONE);
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
		
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				wifi = new CHWifiDirect(getActivity(), info);
				ac = new ArduinoCommands(wifi);
				cu = new ControlUnits();
				cu.setCommands(ac);
				//cu.buttonsAvailable(View.VISIBLE);
				getFragmentManager().beginTransaction().replace(R.id.mainFragment, cu)
				.addToBackStack("cu").commit();
				
			}
		});

		return mContentView;
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {

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

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
		mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.VISIBLE);
//		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
	}
	
	public void onResume(){
		super.onResume();
		intentFilter = new IntentFilter();
		// add necessary intent values to be matched for wifi direct
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		 getActivity().registerReceiver(wdr, intentFilter);
	}
	
	public void onPause(){
		super.onPause();
		getActivity().unregisterReceiver(wdr);
		if(fs != null)
			fs.stop();
	}
}


class WifiDetailReceiver extends BroadcastReceiver{

	private WifiP2pManager mManager;
	private Channel mChannel;
	private PeerDetail pl;
	
	public WifiDetailReceiver(WifiP2pManager manager, Channel channel, PeerDetail peerDetail){
		mManager = manager;
		mChannel = channel;
		pl = peerDetail;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			Log.d("WifiDietailReceiver","Got some info");
            if (mManager == null) {
                return;
            }
            Log.d("WifiDetailReceiver", "Manager != null");
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            Log.d("WifiDetailReceiver",(networkInfo.isConnected() == true) ? "true" : "false");
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP   
                mManager.requestConnectionInfo(mChannel, pl);
            } 
		}
	}
	
}