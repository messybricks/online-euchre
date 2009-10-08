package server;

import java.io.*;
import java.net.*;
import utility.*;

/**
 * Handles network communications between clients for the Online Euchre Game
 * 
 */
public class EuchreServer
{
	// default port to listen for connections on if no specific port is given at command line
	private static final int DEFAULT_PORT = 36212;
	// waits this many milliseconds to ensure the clients receive this server's Quit packet upon disposal
	private static final int QUIT_SLEEP_MS = 200;

	/**
	 * Server application entry point.
	 * 
	 * @param args argument 1 is a port number to listen on. it is optional; port 36212 will be used if it is omitted.
	 */
	public static void main(String[] args)
	{
		// say hi
		System.out.println("EuchreServer v0.1");
		Trace.dprint("Initializing...");

		// the default listen port is 36212
		int port = DEFAULT_PORT;
		ServerSocket serverSocket = null;

		// the zeroth argument is the port

		if (args.length > 0)
		{
			try
			{
				port = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e)
			{
				Trace.dprint("givin port not a number; using default port %d",DEFAULT_PORT);
			}
		}
		else
			Trace.dprint("No port specified; using default port %d...", DEFAULT_PORT);

		// make sure the port is valid
		if (port < 1024 || port > 65534)
		{
			System.out.println(port + " is not a valid port to listen on. Please enter a number between 1024 and 65534.");
			System.exit(-1);
		}

		try
		{
			// the ip address '0.0.0.0' refers to a universal bind point. any address will be able to connect to it.
			// the 4 refers to the socket backlog, which is how many connections it will allow at any given time
			Trace.dprint("Opening server socket on port %d...", port);
			serverSocket = new ServerSocket(port, 4, Inet4Address.getByAddress(new byte[] { 0, 0, 0, 0 }));
		}
		catch (IOException e)
		{
			Trace.dprint("Could not listen on port: %d", port);
			Trace.dprint("Exception message: %s", e.getMessage());
			System.exit(-1);
		}

		// initialize the server socket thread
		Trace.dprint("Initializing ServerSocketThread...");
		ServerSocketThread thread = new ServerSocketThread(serverSocket);
		Trace.dprint("Starting ServerSocketThread...");
		thread.start();

		// implements a server management thread
		Trace.dprint("Starting management loop...");
		while(true)
		{
			String[] command = null;
			BufferedReader reader = null;

			System.out.print("[server] > ");

			try
			{
				reader = new BufferedReader(new InputStreamReader(System.in));
				command = reader.readLine().split(" ");
			}
			catch (IOException ex)
			{
				Trace.dprint("Unable to read from System.in. Message: %s", ex.getMessage());
			}

			if(command.length < 1)
				continue;

			if(command[0].equals("exit"))
				break;
			else
				System.out.println("Unknown command '" + command[0] + '\'');
		}

		try
		{
			Trace.dprint("Sending global '%s' packet...", Opcode.Quit.toString());
			thread.sendGlobal(Opcode.Quit);

			Thread.sleep(QUIT_SLEEP_MS);
		}
		catch (InterruptedException ex)
		{
			Trace.dprint("Main thread was interrupted while joining ServerSocketThread!");
		}
		finally
		{
			try
			{
				Trace.dprint("Stopping ServerSocketThread...");
				thread.stopThread();
				thread.join();
			}
			catch (InterruptedException ex)
			{
				Trace.dprint("Main thread was interrupted while joining ServerSocketThread!");
			}
			finally
			{
				try
				{
					// close the socket
					Trace.dprint("Closing server socket...");
					serverSocket.close();
				}
				catch (IOException e)
				{
					Trace.dprint("Could not close socket. Message: %s", e.getMessage());
				}
			}
		}

		Trace.dprint("Terminated.");
	}
}
