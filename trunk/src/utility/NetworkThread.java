package utility;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread implements a generic queue-based client/server communications system.
 */
public class NetworkThread extends Thread
{
	private Socket socket = null;
	private Queue<Packet> inbound = null;
	private Queue<Packet> outbound = null;
	private DataInputStream reader = null;
	private DataOutputStream writer = null;
	
	private volatile boolean exitThread = false;
	
	/**
	 * Creates a new instance of the PacketQueueThread using the given client connection.
	 * @param client A socket bound to the client to communicate with.
	 */
	public NetworkThread(Socket client)
	{
		super("NetworkThread");
		socket = client;
		
		inbound = new LinkedList<Packet>();
		outbound = new LinkedList<Packet>();
		
		try
		{
			reader = new DataInputStream(socket.getInputStream());
			writer = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException ex)
		{
			Trace.dprint("Unable to attach socket stream to NetworkThread. Message: %s", ex.getMessage());
		}
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				synchronized(this)
				{
					try
					{
						// if the reader has bytes available to be read, we can convert them to a packet and add to inbound
						if(reader.available() > 0)
						{
							int byteIn = 0;
							ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
							
							while((byteIn = reader.read()) != -1)
								dataStream.write(byteIn);
							
							inbound.add(new Packet(dataStream.toByteArray()));
						}
					}
					catch (IOException ex)
					{
						Trace.dprint("Unable to read data from socket input stream. Message: %s", ex.getMessage());
					}
					catch (InvalidPacketException ex)
					{
						Trace.dprint("Received a corrupt packet from input stream on NetworkThread. Message: %s", ex.getMessage());
					}
					
					try
					{
						// if outbound is not empty, we can flatten a packet and send it on its merry way
						if(!outbound.isEmpty())
						{
							byte[] flattened = outbound.remove().flatten();
							writer.write(flattened);
						}
					}
					catch (IOException ex)
					{
						Trace.dprint("Unable to write packet to output stream in NetworkThread. Message: %s", ex.getMessage());
					}
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
			writer.close();
			reader.close();
			socket.close();
		}
		catch (IOException ex)
		{
			Trace.dprint("Unable to close client socket in NetworkThread.");
		}
	}
	
	/**
	 * Enqueues a packet to be sent to the server.
	 * @param packet Packet to send
	 */
	public synchronized void send(Packet packet)
	{
		outbound.add(packet);
	}
	
	/**
	 * Receives the next packet sent by this NetworkThread's server. This method returns null if there are no inbound packets.
	 * @return Next packet sent by server or null if no such packet exists
	 */
	public synchronized Packet receive()
	{
		if(inbound.isEmpty())
			return null;
		else
			return inbound.remove();
	}
	
	/**
	 * Returns true if the server has sent a packet
	 * @return True if the server has sent a packet
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
