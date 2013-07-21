package com.robot.connection;

import android.util.Log;

/*
 * Handler that offers simple commands and translates them to a bitstring that is 
 * readable by the arduino devices.
 * The class is as modular as possible to support different types of vehicles.
 */
public class ArduinoCommands {

	// handler for the commands on different hardware (wifi/bt)
	ConnectionHandlerInterface cHandler;

	// initialize the send string
	String[] send = new String[] { "000", "000", "0", "0", "0", "0" };

	public ArduinoCommands(ConnectionHandlerInterface cHandler) {
		this.cHandler = cHandler;
	}

	/**
	 * Set a specific speed for driving.
	 * 
	 * @param i
	 *            the speed
	 */
	public void driveSpeed(int i) {
		if (i >= 0 && i <= 255) {
			String speed = Integer.toString(i);
			while (speed.length() < 3) {
				speed = "0" + speed;
			}
			send[0] = speed;
		}
		send();
	}

	/**
	 * Set a specific speed or angle for steering.
	 * 
	 * @param i
	 *            the speed/angle
	 */
	public void steerSpeed(int i) {
		if (i >= 0 && i <= 255) {
			String speed = Integer.toString(i);
			while (speed.length() < 3) {
				speed = "0" + speed;
			}
			send[1] = speed;
		}
		send();
	}

	/**
	 * Set/Unset the forward bit true is set false is not set.
	 * 
	 * @param state
	 */
	public void forward(boolean state) {
		if (state == true)
			send[2] = "1";
		else
			send[2] = "0";
		send();
	}

	/**
	 * Set/Unset the backward bit true is set false is not set.
	 * 
	 * @param state
	 */
	public void backward(boolean state) {
		if (state == true)
			send[3] = "1";
		else
			send[3] = "0";
		send();
	}

	/**
	 * Set/Unset the left bit true is set false is not set.
	 * 
	 * @param state
	 */
	public void left(boolean state) {
		if (state == true)
			send[4] = "1";
		else
			send[4] = "0";
		send();
	}

	/**
	 * Set/Unset the right bit true is set false is not set.
	 * 
	 * @param state
	 */
	public void right(boolean state) {
		if (state == true)
			send[5] = "1";
		else
			send[5] = "0";
		send();
	}

	/**
	 * Drive forward and simultaneously do unset the backward bit.
	 * 
	 * @param i
	 *            the driving speed
	 */
	public void forwardWithSpeed(int i) {
		send[2] = "1"; // forward
		send[3] = "0"; // not backward
		String speed = Integer.toString(i);
		while (speed.length() < 3) {
			speed = "0" + speed;
		}
		send[0] = speed;
		send();
	}

	/**
	 * Drive backward and simultaneously do unset the forward bit.
	 * 
	 * @param i
	 *            the driving speed
	 */
	public void backwardWithSpeed(int i) {
		send[2] = "0"; // not forward
		send[3] = "1"; // backward
		String speed = Integer.toString(i);
		while (speed.length() < 3) {
			speed = "0" + speed;
		}
		send[0] = speed;
		send();
	}

	/**
	 * Drive left and simultaneously do unset the right bit. This is only
	 * applicable for vehicles that are able to rotate on the spot
	 * 
	 * @param i
	 *            the rotate speed
	 */
	public void leftWithSpeed(int i) {
		send[4] = "1"; // left
		send[5] = "0"; // not right
		String speed = Integer.toString(i);
		while (speed.length() < 3) {
			speed = "0" + speed;
		}
		send[1] = speed;
		send();
	}

	/**
	 * Drive right and simultaneously do unset the left bit. This is only
	 * applicable for vehicles that are able to rotate on the spot.
	 * 
	 * @param i
	 *            the rotate speed
	 */
	public void rightWithSpeed(int i) {
		send[4] = "0"; // not left
		send[5] = "1"; // right
		String speed = Integer.toString(i);
		while (speed.length() < 3) {
			speed = "0" + speed;
		}
		send[1] = speed;
		send();
	}

	/**
	 * Drive left and simultaneously do unset the right bit and additionally
	 * drive forward. This is only applicable for vehicles that are equipped
	 * with a servo motor.
	 * 
	 * @param i
	 *            the drive speed
	 */
	public void leftWithServo(int i) {

		send[2] = "1"; // forward
		send[4] = "1"; // left
		send[5] = "0"; // not right
		String speed = Integer.toString(i);
		while (speed.length() < 3) {
			speed = "0" + speed;
		}
		send[1] = "255";
		send[0] = speed;
		send();
	}

	/**
	 * Drive right and simultaneously do unset the left bit and additionally
	 * drive forward. This is only applicable for vehicles that are equipped
	 * with a servo motor.
	 * 
	 * @param i
	 *            the drive speed
	 */
	public void rightWithServo(int i) {

		send[2] = "1"; // forward
		send[4] = "0"; // not left
		send[5] = "1"; // right
		String speed = Integer.toString(i);
		while (speed.length() < 3) {
			speed = "0" + speed;
		}
		send[1] = "255";
		send[0] = speed;
		send();
	}

	/**
	 * Set stop on all channels
	 */
	public void stop() {
		send[0] = "000";
		send[1] = "000";
		send[2] = "0";
		send[3] = "0";
		send[4] = "0";
		send[5] = "0";
		send();
	}

	/**
	 * Send the bit string
	 */
	public void send() {
		cHandler.sendData(send[0] + send[1] + send[2] + send[3] + send[4] + send[5] + "?");
		// Log.d("ArduinoCommand", "" + send[0] + send[1] + send[2] + send[3] +
		// send[4] + send[5]);
	}

}
