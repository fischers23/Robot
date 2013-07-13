package com.robot.ui;

import com.robot.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// open the welcome screen
		setContentView(R.layout.main_activity);

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

	// Communication with the Arduino Car by bluetooth
	// called from the layout directly
	public void openController(View v) {

		// open the controll screen
		if (findViewById(R.id.mainFragment) != null) {

			
			
			ControlUnits cu = new ControlUnits();
			getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, cu).addToBackStack("cu").commit();
		}

	}

	protected void onDestroy() {
		super.onDestroy();
	}

}
