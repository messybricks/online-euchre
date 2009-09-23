package server;

import java.io.*;
import java.net.*;
import java.util.*;
import utility.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class PacketQueueThread extends Thread
{
	private Socket socket = null;
	private Queue<Packet> inbound = null;
	private Queue<Packet> outbound = null;
	
	private volatile boolean exitThread = false;
	
	/**
	 * Creates a new instance of the PacketQueueThread using the given client connection.
	 * @param client A socket bound to the client to communicate with.
	 */
	public PacketQueueThread(Socket client)
	{
		super("PacketQueueThread");
		socket = client;
		
		inbound = new LinkedList<Packet>();
		outbound = new LinkedList<Packet>();
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				synchronized(this)
				{
					
				}
			}
			finally
			{
				// if exitThread is true, the parent thread has requested a soft termination of this thread
				if(exitThread)
					break;
			}
		}
		
		try
		{
			socket.close();
		}
		catch (IOException ex)
		{
			Trace.dprint("Unable to close client socket in PacketQueueThread.");
		}
	}
	
	/**
	 * Enqueues a packet to be sent to this client.
	 * @param packet Packet to send
	 */
	public synchronized void send(Packet packet)
	{
		outbound.add(packet);
	}
	
	/**
	 * Receives the next packet sent by this PacketQueueThread's client. This method returns null if there are no inbound packets.
	 * @return Next packet sent by client or null if no such packet exists
	 */
	public synchronized Packet receive()
	{
		if(inbound.isEmpty())
			return null;
		else
			return inbound.remove();
	}
	
	/**
	 * Returns true if this client has sent a packet
	 * @return True if this client has sent a packet
	 */
	public synchronized boolean hasInbound()
	{
		return !inbound.isEmpty();
	}
	
	/**
	 * Sets a flag which will attempt to safely terminate this thread.
	 */
	public void stopThread()
	{
		exitThread = true;
	}
}
