package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import chat.*;
import utility.*;

/**
 * Represents a thread in which a socket will block while waiting for incoming connections.
 */
public class ServerSocketThread extends Thread
{
	private ServerSocket socket = null;
	private Map<String, PacketQueueThread> clientMapping = null;

	private volatile boolean exitThread = false;

	// block server socket thread this many milliseconds waiting to accept a connection. acts as a thread tick timer
	private static final int SOCKET_TIMEOUT = 200;
	// ping clients every this many milliseconds to ensure connection validity
	private static final int PING_FREQUENCY = 15000;
	// client times out and is disconnected if a pong takes this many milliseconds
	private static final int PING_TIMEOUT = 3000;
	// wait this many milliseconds to kill a client thread peacefully
	private static final int THREAD_KILL_TIMEOUT = 600;

	private ChatManager chatManager;
	private UserManager userManager;

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

		chatManager = new ChatManager(this);
		userManager = new UserManager(this);

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
				// play ping-pong
				long time = System.currentTimeMillis();
				Set<String> invalidated = new HashSet<String>();

				synchronized(clientMapping)
				{
					for(Entry<String, PacketQueueThread> entry : clientMapping.entrySet())
					{
						// ping clients every so often
						if(time - entry.getValue().getLastPing() > PING_FREQUENCY)
						{
							entry.getValue().ping();
						}

						// check to see if they have disconnected due to network problems
						if(entry.getValue().getLastPong() < entry.getValue().getLastPing() && time - entry.getValue().getLastPing() > PING_TIMEOUT)
						{
							Trace.dprint("Client id '%s' has timed out.", entry.getKey());
							invalidated.add(entry.getKey());
							
							if(entry.getValue().isAuthenticated())
								userManager.remove(entry.getValue().getUser());
						}

						// check to see if they have disconnected of their own volition
						if(entry.getValue().hasQuit())
							invalidated.add(entry.getKey());
					}
				}

				// authenticate clients
				synchronized(clientMapping)
				{
					for(Entry<String, PacketQueueThread> entry : clientMapping.entrySet())
					{
						if(!entry.getValue().isAuthenticated())
						{
							if(entry.getValue().getUser() != null)
							{
								PacketQueueThread tempThread = entry.getValue();
								invalidated.add(entry.getKey());
								tempThread.verify();
								clientMapping.put(tempThread.getUser().getUsername(), tempThread);
							}
						}
					}
				}

				// remove invalidated connections
				synchronized(clientMapping)
				{
					for(String key : invalidated)
					{
						clientMapping.get(key).stopThread();
						try
						{
							clientMapping.get(key).join(THREAD_KILL_TIMEOUT);
						}
						catch (InterruptedException ex)
						{
							Trace.dprint("ServerSocketThread was interrupted while joining client thread! Message: %s", ex.getMessage());
						}
						clientMapping.remove(key);
					}
				}

				// if socket.accept() does not return null, we have an incoming connection and must spawn a thread to manage it
				Socket accepted = socket.accept();
				if(accepted != null)
				{
					String hostName = accepted.getInetAddress().getHostName();
					Trace.dprint("Accepting connection from '%s'...", hostName);

					PacketQueueThread clientThread = new PacketQueueThread(accepted, chatManager, userManager);
					synchronized(clientMapping)
					{
						clientMapping.put(hostName, clientThread);
					}
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
		synchronized(clientMapping)
		{
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

	/**
	 * Sends the given opcode to every client connected to this ServerSocketThread.
	 * @param opcode Opcode of packet to generate
	 */
	public void sendGlobal(Opcode opcode)
	{
		sendGlobal(opcode, null);
	}

	/**
	 * Sends the given opcode with an associated datum to every client connected to this ServerSocketThread.
	 * @param opcode Opcode of packet to generate
	 * @param datum An optional object to associate with the generated packet
	 */
	public void sendGlobal(Opcode opcode, Serializable datum)
	{
		Packet global = new Packet(opcode, datum);

		synchronized(clientMapping)
		{
			for(PacketQueueThread thread : clientMapping.values())
				thread.send(global);
		}
	}

	/**
	 * Sends the given opcode with an associated datum to a specified client.
	 * @param client Client to send the packet to
	 * @param opcode Opcode of packet to generate
	 */
	public void sendSpecified(String client, Opcode opcode)
	{
		sendSpecified(client, opcode, null);
	}

	/**
	 * Sends the given opcode with an associated datum to a specified client. If the client given does not exist, a
	 * warning is sent to dprint.
	 * @param client Client to send the packet to
	 * @param opcode Opcode of packet to generate
	 * @param datum An optional object to associate with the generated packet
	 */
	public void sendSpecified(String client, Opcode opcode, Serializable datum)
	{
		synchronized(clientMapping)
		{
			if(clientMapping.containsKey(client))
				clientMapping.get(client).send(opcode, datum);
			else
				Trace.dprint("WARNING: Attempted to send a packet with opcode '%s' to nonexistent client '%s'.", opcode.toString(), client);
		}
	}
}
