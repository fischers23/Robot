package com.robot.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.robot.ui.ControlUnits;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class FileServerAsync extends AsyncTask<Void, Void, String> {

	EditText msg;
	Activity act;
	private CHWifiDirect wd;
	private ConnectionHandlerInterface cHandler;


	public FileServerAsync(Activity act) {
		this.act = act;
		// if not already done instantiate the BT connection handler
		if (cHandler == null)
			cHandler = new CHBluetooth(act, "Arduino");

		// create the driver class

	}

	@Override
	protected String doInBackground(Void... arg0) {

		try {
			ServerSocket serverSocket = new ServerSocket(8988);
			Log.d("FileServer", "Server: Socket opened");
			Socket client = serverSocket.accept();
			Log.d("FileServer", "Server: connection done");

			InputStream inputstream = client.getInputStream();
			java.util.Scanner s = new java.util.Scanner(inputstream)
					.useDelimiter("\\A");
			String text = s.hasNext() ? s.next() : "";
			serverSocket.close();
			if (text != "") {
				Log.d("FileServer", text);
				// msg = (EditText)act.findViewById(R.id.sendTxt);
				// msg.setText(text);
				// TODO: ArduinoCommands passthrough
				cHandler.sendData(text);
			}
			return "";
		} catch (IOException e) {
			Log.e("FileServer", e.getMessage());
			return "";
		}
	}
}
