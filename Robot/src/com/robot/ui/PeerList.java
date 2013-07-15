package com.robot.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.robot.R;

public class PeerList extends ListFragment implements PeerListListener{

	private View mContentView;
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private WifiP2pManager mManager;
	private Channel mChannel;
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        this.setListAdapter(new PeerListAdapter(getActivity(), R.layout.peer_list_item, peers));

    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// make known that we want to change the menu with this activity
		setHasOptionsMenu(true);
		
		
		mContentView = inflater.inflate(R.layout.fragment_wifi_list, container, false);
		mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(getActivity(), getActivity().getMainLooper(), null);
		
		mManager.discoverPeers(mChannel,
				new WifiP2pManager.ActionListener() {
					@Override
					public void onSuccess() {
						Log.d("PeerList", "Peer discovery successful");
					}

					@Override
					public void onFailure(int reasonCode) {
						Log.d("PeerList", "Peer discovery failed");
					}
				});
		
		return mContentView;
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
		
		Log.d("PeerList", "got some Peers");
		peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((PeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d("PeerList", "No devices found");
            return;
        }
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// make our menu buttons visible
		menu.findItem(R.id.atn_scan_for_wd_peers).setVisible(true);
	}
	
	
	
	/**
	 * Array adapter for ListFragment that maintains WifiP2pDevice list.
	 */
	private class PeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

	    private List<WifiP2pDevice> items;

	    /**
	     * @param context
	     * @param textViewResourceId
	     * @param objects
	     */
	    public PeerListAdapter(Context context, int textViewResourceId,
	            List<WifiP2pDevice> objects) {
	        super(context, textViewResourceId, objects);
	        items = objects;

	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
	                    Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.peer_list_item, null);
	        }
	        WifiP2pDevice device = items.get(position);
	        if (device != null) {
	            TextView top = (TextView) v.findViewById(R.id.device_name);
	            TextView bottom = (TextView) v.findViewById(R.id.device_details);
	            if (top != null) {
	                top.setText(device.deviceName);
	            }
	            if (bottom != null) {
//	                bottom.setText(getDeviceStatus(device.status));
	            }
	        }

	        return v;

	    }
	}
	
    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);

        PeerDetail pd = new PeerDetail();
        pd.setDevice(device);
        getFragmentManager().beginTransaction().replace(R.id.mainFragment, pd).addToBackStack("pd").commit();
        
    }
    
    
    public void discoverPeers(){
    	mManager.discoverPeers(mChannel,
				new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.d("PeerList", "Peer discovery successful");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.d("PeerList", "Peer discovery failed");
			}
		});
    }

}



