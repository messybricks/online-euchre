package server;

import java.net.*;
import utility.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class PacketQueueThread extends NetworkThread
{
	private long lastPing = 0;
	private long lastPong = 0;
	private boolean quit = false;
	
	private static int uid = 0;
	
	// do not modify code here! anything you may want to change is found below, in packet processing
	
	/**
	 * Creates a new instance of the PacketQueueThread class wrapping the given socket.
	 * @param client Socket connected to client
	 */
	public PacketQueueThread(Socket client)
	{
		super(client, "PacketQueue_" + ++uid);
	}

	/**
	 * Returns the last time this socket sent a Ping packet.
	 * @return The last time this socket sent a Ping packet
	 */
	public long getLastPing()
	{
		return lastPing;
	}
	
	/**
	 * Returns the last time this socket received a Pong packet.
	 * @return The last time this socket received a Pong packet
	 */
	public long getLastPong()
	{
		return lastPong;
	}
	
	/**
	 * Sends a ping packet to the associated client.
	 */
	public void ping()
	{
		send(Opcode.Ping);
		lastPing = System.currentTimeMillis();
	}
	
	/**
	 * Gets a flag indicating whether this client has intentionally disconnected.
	 * @return True if the client has disconnected; false otherwise
	 */
	public boolean hasQuit()
	{
		return quit;
	}
	
	// processing packets starts here. you can change code below this line as you see fit.
	//  please try to maintain the same style i have created here. for each packet you
	//  implement, add an "else if" statement in processPacket, which points directly to
	//  a function named "onOpcode" where the Opcode is the opcode of the packet you're
	//  processing. this will help keep the packet processing clean and uniform.
	//   - bert
	
	/**
	 * Event which is raised when the network thread receives a packet. Routes the packet to an appropriate processor function.
	 */
	protected void processPacket(Packet packet)
	{
		if(packet.getOpcode() == Opcode.Pong)
			onPong(packet);
		else if(packet.getOpcode() == Opcode.Quit)
			onQuit(packet);
		else
			Trace.dprint("Received packet with unimplemented opcode '%s' - ignoring.", packet.getOpcode().toString());
	}
	
	/**
	 * Processes a Pong packet.
	 * @param packet Packet to process
	 */
	private void onPong(Packet packet)
	{
		lastPong = System.currentTimeMillis();
	}
	
	/**
	 * Processes a Pong packet.
	 * @param packet Packet to process
	 */
	private void onQuit(Packet packet)
	{
		quit = true;
	}
}
