package client;

import java.net.*;
import java.io.*;
import utility.*;

/**
 * Establishes a connection with the Euchre server.
 */
public class EuchreNetClient
{
	private Socket socket = null;
	private boolean valid = false;
	private NetClientThread thread = null;
	
	/**
	 * Creates a new instance of the EuchreNetClient by initializing a socket with the given address and port.
	 * @param address Address to connect to
	 * @param port Port to connect via
	 */
	public EuchreNetClient(String address, int port)
	{
		try
		{
			socket = new Socket(address, port);
			thread = new NetClientThread(socket);
			valid = true;
		}
		catch (IOException ex)
		{
			Trace.dprint("Unable to open socket on address %s and port %d. Message: %s", address, port, ex.getMessage());
			valid = false;
		}
	}
	
	/**
	 * Checks the state of this client and starts the socket thread if everything is in order.
	 * @return True if the client has started successfully
	 */
	public boolean start()
	{
		if(isValid())
		{
			thread.start();
			Trace.dprint("NetClientThread started...");
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Disposes of this EuchreNetClient by closing the socket and releasing resources.
	 */
	public void dispose()
	{
		if(socket != null)
		{
			try
			{
				thread.stopThread();
				thread.join();
				Trace.dprint("NetClientThread stopped...");
			}
			catch(InterruptedException ex)
			{
				Trace.dprint("Client window thread interrupted while joining NetClientThread!");
			}
			finally
			{
				try
				{
					socket.close();
				}
				catch (IOException ex)
				{
					Trace.dprint("Unable to close client socket.");
				}
			}
		}
	}
	
	/**
	 * Gets a flag indicating the state of this EuchreNetClient.
	 * @return True if client is usable, false if not
	 */
	public boolean isValid()
	{
		return valid;
	}
}
