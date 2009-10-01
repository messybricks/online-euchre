package client;

import java.net.*;
import java.io.*;
import utility.*;
import chat.*;

/**
 * Establishes a connection with the Euchre server.
 */
public class EuchreNetClient
{
	private User associate = null;
	private Socket socket = null;
	private boolean valid = false;
	private NetClientThread thread = null;
	private EuchreApplet euchreApplet;
	
	// waits this many milliseconds to ensure the server receives this client's Quit packet upon disposal
	private static final int QUIT_SLEEP_MS = 200;
	
	/**
	 * Creates a new instance of the EuchreNetClient by initializing a socket with the given address and port.
	 * @param address Address to connect to
	 * @param port Port to connect via
	 */
	public EuchreNetClient(String address, int port, EuchreApplet applet)
	{
		euchreApplet = applet;
		try
		{
			socket = new Socket(address, port);
			thread = new NetClientThread(socket, applet);
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
			// send a quit packet to the server
			thread.send(Opcode.Quit);
			
			// sleep for a short time to give the network thread time to send the quit packet
			try
			{
				Thread.sleep(QUIT_SLEEP_MS);
			}
			catch (InterruptedException ex)
			{
				Trace.dprint("Main thread was interrupted while waiting to send Quit packet!");
			}
			
			// stop the network thread
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
				// close the socket
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
	
	/**
	 * Sends a chat message to every client connected to the same server as this NetClient.
	 * @param from ID representing this client's user
	 * @param message A string to be sent
	 */
	public void sendGlobalChatMessage(String message)
	{
		if(associate == null)
			Trace.dprint("Cannot send a message before the client has authenticated.");
		else
			thread.send(Opcode.SendMessage, new ChatObject(associate, null, message));
	}
	
	/**
	 * Assigns a User object to this client. This method will do nothing if this client is already authenticated.
	 * @param me User object to associate with this client
	 */
	public void authenticate(User me)
	{
		if(associate == null)
		{
			associate = me;
			thread.send(Opcode.Auth, me);
		}
		else
			Trace.dprint("Client '%s' tried to authenticate twice. Ignoring.", associate.getUsername());
	}
}
