package server;

import java.io.*;
import java.net.*;
import utility.*;

public class EuchreServer
{

	/**
	 * @param args argument 1 is a port number to listen on. it is optional; port 36212 will be used if it is omitted.
	 */
	public static void main(String[] args)
	{
		// the default listen port is 36212
		int port = 36212;
		ServerSocket serverSocket = null;

		// the first argument is the port (the zeroith is the program name)
		if (args.length >= 2)
			port = Integer.parseInt(args[1]);

		// make sure the port is valid
		if (port < 100 || port > 65534)
			System.out.println(port + " is not a valid port to listen on. Please enter a number between 100 and 65534.");

		try
		{
			// the ip address '0.0.0.0' refers to a universal bind point. any address will be able to connect to it.
			// the 4 refers to the socket backlog, which is how many connections it will allow at any given time
			serverSocket = new ServerSocket(port, 4, Inet4Address.getByAddress(new byte[] { 0, 0, 0, 0 }));
		} catch (IOException e)
		{
			Trace.dprint("Could not listen on port: %d", port);
			Trace.dprint("Exception message: %s", e.getMessage());
			System.exit(-1);
		}

		try
		{
			// close the socket
			serverSocket.close();
		} catch (IOException e)
		{
			Trace.dprint("Could not close socket. Message: %s", e.getMessage());
		}

	}

}
