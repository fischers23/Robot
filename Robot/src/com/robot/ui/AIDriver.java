package com.robot.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robot.R;

public class AIDriver extends Fragment  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View mContentView = inflater.inflate(R.layout.fragment_ai, container, false);
		
		return mContentView;

	}
	
	
}
