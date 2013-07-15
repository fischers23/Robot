package com.robot.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class FileServerAsync extends AsyncTask<Void, Void, String>{

	EditText msg;
	Activity act;

	public FileServerAsync(View textview){
		msg = (EditText)textview;
	}
	
	public FileServerAsync(Activity act){
		this.act = act;
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		
		try {
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d("FileServer", "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d("FileServer", "Server: connection done");
            
            InputStream inputstream = client.getInputStream();
            java.util.Scanner s = new java.util.Scanner(inputstream).useDelimiter("\\A");
            String text = s.hasNext() ? s.next() : "";
            serverSocket.close();
            if(text != ""){
            	Log.d("FileServer", text);
//            	msg = (EditText)act.findViewById(R.id.sendTxt);
//            	msg.setText(text);
            	//TODO: ArduinoCommands passthrough
            }
            return "";
        } catch (IOException e) {
            Log.e("FileServer", e.getMessage());
            return "";
        }
	}
}
