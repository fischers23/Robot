package com.robot.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robot.R;
import com.robot.ai.Navigator;
import com.robot.connection.ArduinoCommands;

public class AIDriver extends Fragment  {

	Navigator navi;
	
	// the arduino command set
	ArduinoCommands driver = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View mContentView = inflater.inflate(R.layout.fragment_ai, container, false);
				
		navi  = new Navigator(getActivity());
		
		
		
		return mContentView;
	}
	
	
	
	
	
}
