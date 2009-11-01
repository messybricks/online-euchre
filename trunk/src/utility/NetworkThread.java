package utility;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread implements a generic queue-based client/server communications system.
 */
public abstract class NetworkThread extends Thread implements TransactionThread
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
				try
				{
					// if the reader has bytes available to be read, we can convert them to a packet and add to inbound
					if(reader.available() >= Packet.HEADER_SIZE)
					{
						//int byteIn = -1;
						ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

						/*while(reader.available() > 0)
						{
							byteIn = reader.read();
							dataStream.write(byteIn);
						}*/
						
						for(byte i = 0; i < Packet.HEADER_SIZE; ++i)
							dataStream.write(reader.read());

						int bytesRead = 0;
						int dataSize = Packet.getDataSizeFromHeader(dataStream.toByteArray());
						for(; bytesRead < dataSize && reader.available() > 0; ++bytesRead)
							dataStream.write(reader.read());
						
						if(bytesRead < dataSize)
						{
							Trace.dprint("### WARNING: Received a partial packet! Received %d bytes from a packet of size %d. Will wait 40ms for additional data...", dataSize + Packet.HEADER_SIZE, bytesRead + Packet.HEADER_SIZE);
							sleep(40);
							for(; bytesRead < dataSize && reader.available() > 0; ++bytesRead)
								dataStream.write(reader.read());
							if(bytesRead < dataSize)
								Trace.dprint("### ERROR: Unable to read packet from network stream. Received %d bytes of %d.", bytesRead + Packet.HEADER_SIZE, dataSize + Packet.HEADER_SIZE);
						}

						Packet inPacket = new Packet(dataStream.toByteArray());
						Trace.nprint("NetworkThread << %s [%d]", inPacket.getOpcode().toString(), dataStream.size() - Packet.HEADER_SIZE);
						
						synchronized(inbound)
						{
							inbound.add(inPacket);
						}
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
				synchronized(inbound)
				{
					if(!inbound.isEmpty())
						processPacket(inbound.remove());
				}

				try
				{
					// if outbound is not empty, we can flatten a packet and send it on its merry way
					synchronized(outbound)
					{
						if(!outbound.isEmpty())
						{
							Packet outPacket = outbound.remove();
							byte[] flattened = outPacket.flatten();
	
							Trace.nprint("NetworkThread >> %s [%d]", outPacket.getOpcode().toString(), flattened.length - Packet.HEADER_SIZE);
							writer.write(flattened);
						}
					}
				}
				catch (IOException ex)
				{
					Trace.dprint("Unable to write packet to output stream in NetworkThread. Message: %s", ex.getMessage());
				}
			}
			catch (Exception uhoh)
			{
				System.err.println(String.format("### ERROR in NetworkThread; unhandled exception of type %s during processing loop execution. Message: %s\nStack trace:\n", uhoh.getClass().getName(), uhoh.getMessage()));
				uhoh.printStackTrace(System.err);
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
	public void send(Packet packet)
	{
		synchronized(outbound)
		{
			outbound.add(packet);
		}
	}

	/**
	 * Enqueues a new packet to be sent to the server.
	 * 
	 * @param opcode Opcode to assign to the new packet
	 */
	public void send(Opcode opcode)
	{
		synchronized(outbound)
		{
			outbound.add(new Packet(opcode));
		}
	}

	/**
	 * Enqueues a packet to be sent to the server.
	 * 
	 * @param opcode Opcode to assign to the new packet
	 * @param datum A serializable object to attach to the packet
	 */
	public void send(Opcode opcode, Serializable datum)
	{
		synchronized(outbound)
		{
			outbound.add(new Packet(opcode, datum));
		}
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
	public boolean hasInbound()
	{
		synchronized(inbound)
		{
			return !inbound.isEmpty();
		}
	}

	/**
	 * Sets a flag which will attempt to safely terminate this thread.
	 */
	public void stopThread()
	{
		exitThread = true;
	}
}
