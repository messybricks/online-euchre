package server;

import java.net.*;
import utility.*;
import chat.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class PacketQueueThread extends NetworkThread
{
	private long lastPing = 0;
	private long lastPong = 0;
	private int latency = -1;
	private boolean quit = false;
	private boolean verified = false;
	private User associate = null;
	
	private static int uid = 0;
	
	private ChatManager chatMan;
	private UserManager userMan;
	
	// do not modify code here! anything you may want to change is found below, in packet processing
	
	/**
	 * Creates a new instance of the PacketQueueThread class wrapping the given socket.
	 * @param client Socket connected to client
	 */
	public PacketQueueThread(Socket client, ChatManager chatManager, UserManager userManager)
	{
		super(client, "PacketQueue_" + ++uid);
		chatMan = chatManager;
		userMan = userManager;
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
	 * Gets the latency (in milliseconds) of the connection to this client.
	 * @return Millisecond delay in communication between client and server
	 */
	public int getLatency()
	{
		return latency;
	}
	
	/**
	 * Gets a flag indicating whether this client has intentionally disconnected.
	 * @return True if the client has disconnected; false otherwise
	 */
	public boolean hasQuit()
	{
		return quit;
	}
	
	/**
	 * Verifies and sets as final this thread's user. After the user is verified, it cannot be changed.
	 */
	public void verify()
	{
		verified = true;
	}
	
	/**
	 * Returns true if this PacketQueueThread has been processed and has a verified associated user.
	 * @return True if this thread has a verified associated User object; false otherwise
	 */
	public boolean isAuthenticated()
	{
		return associate != null;
	}
	
	/**
	 * Returns the User object associated with this thread.
	 * @return User object associated with this thread
	 */
	public User getUser()
	{
		return associate;
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
		else if(packet.getOpcode() == Opcode.Auth)
			onAuth(packet);
		else if(packet.getOpcode() == Opcode.Quit)
			onQuit(packet);
		else if(packet.getOpcode() == Opcode.SendMessage)
			onSendMessage(packet);
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
		latency = (int)(lastPong - lastPing);
	}
	
	/**
	 * Processes an Auth packet.
	 * @param packet Packet to process
	 */
	private void onAuth(Packet packet)
	{
		if(verified)
			Trace.dprint("Received '%s' packet from verified user '%s'; ignoring.", packet.getOpcode().toString(), associate.getUsername());
		else
		{
			associate = (User)packet.getData();
			userMan.add(associate);
		}
	}
	
	/**
	 * Processes a Pong packet.
	 * @param packet Packet to process
	 */
	private void onQuit(Packet packet)
	{
		User temp = (User)packet.getData();
		userMan.remove(temp);
		quit = true;
	}
	
	/**
	 * Processes a SendMessage packet.
	 * @param packet Packet to process
	 */
	private void onSendMessage(Packet packet)
	{
		ChatObject object = (ChatObject)packet.getData();
		chatMan.send(object);
		Trace.dprint("User '%s' says: %s", object.getSource().getUsername(), object.getMessage());
	}
}
