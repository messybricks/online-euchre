package server;

import java.net.*;
import utility.*;
import chat.*;
import game.*;

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
	private Player myPlayer = null;
	private EuchreEngine stateMachine = null;

	private static int uid = 0;

	private ChatManager chatMan;
	private UserManager userMan;
	private ServerSocketThread globalThread;

	/**
	 * Creates a new instance of the PacketQueueThread class wrapping the given socket.
	 * 
	 * @param client Socket connected to client
	 */
	public PacketQueueThread(Socket client, ChatManager chatManager, UserManager userManager, ServerSocketThread global)
	{
		super(client, "PacketQueue_" + ++uid);
		chatMan = chatManager;
		userMan = userManager;
		globalThread = global;
	}

	/**
	 * Gets the Player associated with this thread.
	 */
	public Player getPlayer()
	{
		return myPlayer;
	}

	/**
	 * Returns the last time this socket sent a Ping packet.
	 * 
	 * @return The last time this socket sent a Ping packet
	 */
	public long getLastPing()
	{
		return lastPing;
	}

	/**
	 * Returns the last time this socket received a Pong packet.
	 * 
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
	 * 
	 * @return Millisecond delay in communication between client and server
	 */
	public int getLatency()
	{
		return latency;
	}

	/**
	 * Gets a flag indicating whether this client has intentionally disconnected.
	 * 
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
	 * 
	 * @return True if this thread has a verified associated User object; false otherwise
	 */
	public boolean isAuthenticated()
	{
		return verified;
	}

	/**
	 * Returns the User object associated with this thread.
	 * 
	 * @return User object associated with this thread
	 */
	public User getUser()
	{
		return associate;
	}
	
	/**
	 * Associates a EuchreEngine state machine with this thread (meaning that its player has joined the game.)
	 * 
	 * @param engine State machine to associate
	 */
	public void setStateMachine(EuchreEngine engine)
	{
		stateMachine = engine;
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
		else if(packet.getOpcode() == Opcode.UpdatePlayer)
			onUpdatePlayer(packet);
		else if(packet.getOpcode() == Opcode.requestBid)
			onRequestBid(packet);
		else if(packet.getOpcode() == Opcode.requestAlternateBid)
			onRequestAlternateBid(packet);
		else if(packet.getOpcode() == Opcode.dealerDiscard)
			onDealerDiscard(packet);
		else if(packet.getOpcode() == Opcode.goingAlone)
			onGoingAlone(packet);
		else if(packet.getOpcode() == Opcode.throwCard)
			onThrowCard(packet);
		else if(stateMachine != null)
			stateMachine.forwardPacket(myPlayer, packet.getOpcode(), packet.getData());
		else
			Trace.dprint("Received packet with unimplemented opcode '%s' from player not in game - ignoring.", packet.getOpcode().toString());
	}

	/**
	 * Processes a Pong packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onPong(Packet packet)
	{
		lastPong = System.currentTimeMillis();
		latency = (int)(lastPong - lastPing);
	}

	/**
	 * Processes an Auth packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onAuth(Packet packet)
	{
		if(verified)
			Trace.dprint("Received '%s' packet from verified user '%s'; ignoring.", packet.getOpcode().toString(), associate.getUsername());
		else
		{
			User user = (User)packet.getData();

			// if the name is already in use, ask the client to enter a new one. otherwise, auth them
			if(userMan.contains(user.getUsername()))
				send(Opcode.Rename, "This name is already in use.");
			else
			{
				associate = user;
				userMan.add(associate);
				
				myPlayer = new Player(user, globalThread);
				send(Opcode.Auth, user);
			}
		}
	}

	/**
	 * Processes a Pong packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onQuit(Packet packet)
	{
		User temp = (User)packet.getData();
		if(temp != null)
			userMan.remove(temp);
		quit = true;
	}

	/**
	 * Processes a SendMessage packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onSendMessage(Packet packet)
	{
		ChatObject object = (ChatObject)packet.getData();
		chatMan.send(object);
		Trace.dprint("User '%s' says: %s", object.getSource().getUsername(), object.getMessage());
	}

	/**
	 * Updates this thread's associated Player object.
	 * @param packet Packet containing data describing changes to the Player
	 */
	private void onUpdatePlayer(Packet packet)
	{
		myPlayer.updateData(packet);
		globalThread.send(Opcode.UpdatePlayer, packet.getData());
	}
	
	/**
	 * Processes a requestBid packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onRequestBid(Packet packet)
	{
		stateMachine.receiveBid((String)packet.getData());

	}
	
	/**
	 * Processes a requestAlternateBid packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onRequestAlternateBid(Packet packet)
	{
		stateMachine.receiveBid((String)packet.getData());
	}
	
	/**
	 * Processes a dealerDiscard packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onDealerDiscard(Packet packet)
	{
		stateMachine.goingAlone();
	}
		
	/**
	 * Processes a goingAlone packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onGoingAlone(Packet packet)
	{
		Boolean answer = (Boolean)packet.getData();
		
		stateMachine.setGoingAlone(answer);
	}
	
	/**
	 * Processes a throwCard packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onThrowCard(Packet packet)
	{
		Card thrown = (Card)packet.getData();
		stateMachine.receiveCard(thrown);
	}
}
