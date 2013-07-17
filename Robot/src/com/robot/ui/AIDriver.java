package com.robot.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.robot.R;
import com.robot.ai.Navigator;

public class AIDriver extends Fragment  {

	Navigator navi;
	View mContentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentView = inflater.inflate(R.layout.fragment_ai, container, false);
		
		
		navi  = new Navigator(getActivity());
		navi.init();
		
		Button btn = (Button) mContentView.findViewById(R.id.refresh);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh();
				
			}
		});
		
		return mContentView;

		
	}
	
	public void refresh(){
		TextView tv = (TextView) mContentView.findViewById(R.id.azimuth);
		tv.setText(""+navi.getAzimuth());
		tv = (TextView) mContentView.findViewById(R.id.pitch);
		tv.setText(""+navi.getPitch());
		tv = (TextView) mContentView.findViewById(R.id.roll);
		tv.setText(""+navi.getRoll());
	}
	
	
}
