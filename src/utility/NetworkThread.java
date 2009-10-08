package utility;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread implements a generic queue-based client/server communications system.
 */
public abstract class NetworkThread extends Thread
{
	private Socket socket = null;
	private Queue<Packet> inbound = null;
	private Queue<Packet> outbound = null;
	private DataInputStream reader = null;
	private DataOutputStream writer = null;

	private volatile boolean exitThread = false;

	// number of milliseconds to wait between management thread ticks
	private static final int THREAD_TICK_MS = 100;

	/**
	 * Creates a new instance of the PacketQueueThread using the given client connection.
	 * 
	 * @param client A socket bound to the client to communicate with.
	 */
	public NetworkThread(Socket client, String name)
	{
		super("NetworkThread_" + name);
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

	/**
	 * This event is raised by the thread managing the socket whenever there are new packets available to be processed.
	 */
	protected abstract void processPacket(Packet packet);

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
						if(reader.available() >= Packet.HEADER_SIZE)
						{
							int byteIn = -1;
							ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

							while(reader.available() > 0)
							{
								byteIn = reader.read();
								dataStream.write(byteIn);
							}

							Packet inPacket = new Packet(dataStream.toByteArray());
							Trace.nprint("NetworkThread << %s [%d]", inPacket.getOpcode().toString(), dataStream.size() - Packet.HEADER_SIZE);
							inbound.add(inPacket);
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

					// process packets on the queue
					if(!inbound.isEmpty())
						processPacket(inbound.remove());

					try
					{
						// if outbound is not empty, we can flatten a packet and send it on its merry way
						if(!outbound.isEmpty())
						{
							Packet outPacket = outbound.remove();
							byte[] flattened = outPacket.flatten();

							Trace.nprint("NetworkThread >> %s [%d]", outPacket.getOpcode().toString(), flattened.length - Packet.HEADER_SIZE);
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

				sleep(THREAD_TICK_MS);
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
	 * 
	 * @param packet Packet to send
	 */
	public synchronized void send(Packet packet)
	{
		outbound.add(packet);
	}

	/**
	 * Enqueues a new packet to be sent to the server.
	 * 
	 * @param opcode Opcode to assign to the new packet
	 */
	public synchronized void send(Opcode opcode)
	{
		outbound.add(new Packet(opcode));
	}

	/**
	 * Enqueues a packet to be sent to the server.
	 * 
	 * @param opcode Opcode to assign to the new packet
	 * @param datum A serializable object to attach to the packet
	 */
	public synchronized void send(Opcode opcode, Serializable datum)
	{
		outbound.add(new Packet(opcode, datum));
	}

	/**
	 * Causes this NetworkThread to pause execution for a specified amount of time.
	 * 
	 * @param milliseconds Number of milliseconds to sleep for
	 */
	protected void sleep(int milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException ex)
		{
			Trace.dprint("NetworkThread was interrupted while sleeping!");
		}
	}

	/**
	 * Returns true if the server has sent a packet
	 * 
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
