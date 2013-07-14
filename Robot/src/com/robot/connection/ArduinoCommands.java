package com.robot.connection;



public class ArduinoCommands {

	ConnectionHandlerInterface sc;
	String[] send = new String[] { "000", "000", "0", "0", "0", "0" };

	public ArduinoCommands(ConnectionHandlerInterface cHandler) {
		this.sc = cHandler;
	}

	public void driveSpeed(int i) {
		if(i>=0 && i <= 255) {
			String speed =Integer.toString(i);
			while (speed.length() < 3) { 
				speed = "0" + speed; 
			} 
			send[0] = speed;
		}
		send();
	}
	
	
	public void steerSpeed(int i) {
		if(i>=0 && i <= 255) {
			String speed =Integer.toString(i);
			while (speed.length() < 3) { 
				speed = "0" + speed; 
			} 
			send[1] = speed;
		}
		send();
	}
	
	
	public void forward(boolean state) {
		if (state == true)
			send[2] = "1";
		else
			send[2] = "0";
		send();
	}

	public void backward(boolean state) {
		if (state == true)
			send[3] = "1";
		else
			send[3] = "0";
		send();
	}

	public void left(boolean state) {
		if (state == true)
			send[4] = "1";
		else
			send[4] = "0";
		send();
	}

	public void right(boolean state) {
		if (state == true)
			send[5] = "1";
		else
			send[5] = "0";
		send();
	}

	public void send() {
		try {
			//TODO: sc.sendData(send[0] + send[1] + send[2] + send[3] + send[4] + send[5] + "?");
			sc.sendData("255" + send[1] + send[2] + send[3] + send[4] + send[5] + "?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
