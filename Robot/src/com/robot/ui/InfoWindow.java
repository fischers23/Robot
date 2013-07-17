package com.robot.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.robot.R;

public class InfoWindow implements InfoWindowAdapter{

	
	
		  LayoutInflater inflater=null;

		  InfoWindow(LayoutInflater inflater) {
		    this.inflater=inflater;
		  }

		  @Override
		  public View getInfoWindow(Marker marker) {
		    return(null);
		  }

		  @Override
		  public View getInfoContents(Marker marker) {
		    View popup=inflater.inflate(R.layout.popup, null);

		    TextView tv=(TextView)popup.findViewById(R.id.title);

		    tv.setText(marker.getTitle());
		    tv=(TextView)popup.findViewById(R.id.snippet);
		    tv.setText(marker.getSnippet());

		    return(popup);
		  }
		  
		  
}
