package com.robot.control;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestFragment extends Fragment {

	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_control_unit, container, false);
	    return view;
	  }


	
	
}
