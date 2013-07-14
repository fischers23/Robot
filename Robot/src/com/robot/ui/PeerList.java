package com.robot.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.robot.R;

public class PeerList extends ListFragment implements PeerListListener{

	private View mContentView;
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new PeerListAdapter(getActivity(), R.layout.peer_list_item, peers));

    }
	
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
        ((PeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d("PeerList", "No devices found");
            return;
        }
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

}



