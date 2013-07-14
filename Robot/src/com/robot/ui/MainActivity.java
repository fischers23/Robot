package com.robot.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.robot.R;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// open the welcome screen
		setContentView(R.layout.main_activity);

		// fill the welcome screen with the connectivity selection fragment
		if (savedInstanceState == null) {
			Fragment consel = new ConnectivitySelector();
			getSupportFragmentManager().beginTransaction().add(R.id.mainFragment, consel).commit();
		}

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_control, menu);
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	// Communication with the Arduino Car by bluetooth
	// called from the layout directly
	public void openController(View v) {

		// open the controll screen fragment
		if (findViewById(R.id.mainFragment) != null) {
			ControlUnits cu = new ControlUnits();
			getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, cu).addToBackStack("cu").commit();
		}

	}

}
