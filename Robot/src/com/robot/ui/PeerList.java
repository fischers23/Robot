package com.robot.ui;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robot.R;

public class PeerList extends Fragment implements PeerListListener{

	private View mContentView;
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_wifi_list, container, false);
		
		
		
		
		return mContentView;
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {

		peers.clear();
        peers.addAll(peerList.getDeviceList());
        if (peers.size() == 0) {
            Log.d("PeerList", "No devices found");
            return;
        }

//        ListView lv = (ListView) mContentView.findViewById(R.id.peers);
        
        
	}
}
