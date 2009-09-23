package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import utility.Trace;

/**
 * Represents a thread in which a socket will block while waiting for incoming connections.
 */
public class ServerSocketThread extends Thread
{
	private ServerSocket socket = null;
	private Map<String, PacketQueueThread> clientMapping = null;
	
	private volatile boolean exitThread = false;
	
	private static final int SOCKET_TIMEOUT = 200;

	/**
	 * Creates a new instance of the ServerSocketThread class.
	 * @param socket Server socket to listen on
	 */
	public ServerSocketThread(ServerSocket socket)
	{
		super("ServerSocketThread");
		this.socket = socket;
		
		try
		{
			socket.setSoTimeout(SOCKET_TIMEOUT);
		}
		catch (SocketException ex)
		{
			Trace.dprint("Unable to set socket accept timeout parameter to %d. Message: %s", SOCKET_TIMEOUT, ex.getMessage());
		}
		
		clientMapping = new HashMap<String, PacketQueueThread>();
	}

	/**
	 * Runs the server socket thread.
	 */
	public void run()
	{
		while (true)
		{
			try
			{
				// if socket.accept() does not return null, we have an incoming connection and must spawn a thread to manage it
				Socket accepted = socket.accept();
				if(accepted != null)
				{
					String hostName = accepted.getInetAddress().getHostName();
					Trace.dprint("Accepting connection from %s...", hostName);
					
					PacketQueueThread clientThread = new PacketQueueThread(accepted);
					clientMapping.put(hostName, clientThread);
					clientThread.start();
				}
			}
			catch (SocketTimeoutException ex)
			{
				// this is to be expected for most iterations of this loop, so we do nothing
			}
			catch (IOException ex)
			{
				Trace.dprint("Server socket thread crashed due to unhandled IOException. Message: ", ex.getMessage());
			}
			finally
			{
				// if exitThread is true, the main thread has requested a soft termination of this thread
				if(exitThread)
					break;
			}
		}
		
		// close client connections
		for(Entry<String, PacketQueueThread> entry : clientMapping.entrySet())
		{
			try
			{
				Trace.dprint("Stopping PacketQueueThread with id '%s'...", entry.getKey());
				entry.getValue().stopThread();
				entry.getValue().join();
			}
			catch (InterruptedException ex)
			{
				Trace.dprint("ServerSocketThread was interrupted while joining PacketQueueThread!");
			}
		}
		
		Trace.dprint("ServerSocketThread has exited. Joining...");
	}
	
	/**
	 * Sets a flag which will attempt to safely terminate this thread.
	 */
	public void stopThread()
	{
		exitThread = true;
	}
}