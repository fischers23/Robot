package com.robot.connection;

import android.app.IntentService;
import android.content.Intent;

public class TransferService extends IntentService {

	public TransferService(){
		super("TransferService");
	}
	
	public TransferService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
