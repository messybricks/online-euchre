package server;

import java.net.*;

/**
 * Represents a thread in which a socket will block while waiting for incoming connections.
 */
public class ServerSocketThread extends Thread
{
	private Socket socket = null;

	public ServerSocketThread(Socket socket)
	{
		super("ServerSocketThread");
		this.socket = socket;
	}

	public void run()
	{
		
	}
}
