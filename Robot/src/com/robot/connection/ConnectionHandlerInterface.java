package com.robot.connection;

import java.io.IOException;

public interface ConnectionHandlerInterface {

	public void sendData(String s);

	public void closeConnection() throws IOException;

	public void establishConnection() throws IOException;

}
