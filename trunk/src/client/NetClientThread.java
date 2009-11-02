package client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import game.*;
import utility.*;
import chat.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class NetClientThread extends NetworkThread
{
	private User associate;
	private EuchreApplet euchreApplet;

	/**
	 * Creates a new instance of the NetClientThread class wrapping the given socket.
	 * 
	 * @param client Socket connection to server
	 * @param applet 
	 */
	public NetClientThread(Socket client, EuchreApplet applet)
	{
		super(client, "NetClient");
		euchreApplet = applet;
	}
	
	/**
	 * Returns the User object associated with this thread, if any. Returns null if there is none.
	 * @return the User object associated with this thread, if any. Returns null if there is none.
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
		if(packet.getOpcode() == Opcode.Ping)
			onPing(packet);
		else if(packet.getOpcode() == Opcode.Quit)
			onQuit(packet);
		else if(packet.getOpcode() == Opcode.SendMessage)
			onSendMessage(packet);
		else if(packet.getOpcode() == Opcode.Auth)
			onAuth(packet);
		else if(packet.getOpcode() == Opcode.UpdateUsers)
			onUpdateUsers(packet);
		else if(packet.getOpcode() == Opcode.Rename)
			onRename(packet);
		else if(packet.getOpcode() == Opcode.CreatePlayer)
			onCreatePlayer(packet);
		else if(packet.getOpcode() == Opcode.UpdatePlayer)
			onUpdatePlayer(packet);
		else if(packet.getOpcode() == Opcode.RemovePlayer)
			onRemovePlayer(packet);
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
		else
			Trace.dprint("Received packet with unimplemented opcode '%s' - ignoring.", packet.getOpcode().toString());
	}

	/**
	 * Processes a Ping packet; sends a Pong to the server.
	 * 
	 * @param packet Packet to process
	 */
	private void onPing(Packet packet)
	{
		send(Opcode.Pong);
	}

	/**
	 * Acknowledges that the server has shut down for some reason and exits the client.
	 * 
	 * @param packet Packet to process
	 */
	private void onQuit(Packet packet)
	{
		euchreApplet.onServerExit((String)packet.getData());
	}

	/**
	 * Accepts and displays a chat message from the server.
	 * 
	 * @param packet Packet to process
	 */
	private void onSendMessage(Packet packet)
	{
		ChatObject chat = (ChatObject)packet.getData();
		euchreApplet.receiveMessage(chat);
	}

	/**
	 * Authenticates this client with a specified user object, after checking server-side to make sure it is valid.
	 * 
	 * @param packet Packet to process
	 */
	private void onAuth(Packet packet)
	{
		associate = (User)packet.getData();
		euchreApplet.setUp();
	}

	/**
	 * Processes an UpdateUsers packet (this comment is really lame :s)
	 * 
	 * @param packet Packet to process
	 */
	private void onUpdateUsers(Packet packet)
	{
		ArrayList<User> users = (ArrayList<User>) packet.getData();
		euchreApplet.addUserToWindow(users);
		euchreApplet.repaint();
	}

	/**
	 * Requests that the user enter another name, because the name desired was rejected by the server for some reason.
	 * 
	 * @param packet Packet to process
	 */
	private void onRename(Packet packet)
	{
		String message = (String)packet.getData();		
		String newName = null;
		
		while(newName == null || newName.equals("")  || !EuchreApplet.isAlphaNumeric(newName) || newName.length() > 12)
			newName = JOptionPane.showInputDialog(message);
		
		User tempUser = new User(newName);
		send(Opcode.Auth, tempUser);
		associate = tempUser;
	}

	/**
	 * Processes a CreatePlayer packet; creates a new player
	 * 
	 * @param packet Packet to process
	 */
	private void onCreatePlayer(Packet packet)
	{
		euchreApplet.addPlayer(new Player(packet, this));
	}

	/**
	 * Processes a RemovePlayer packet; removes a player
	 * 
	 * @param packet Packet to process
	 */
	private void onRemovePlayer(Packet packet)
	{
		List<Player> list = euchreApplet.getPlayerList();
		for(int i = 0; i < list.size(); ++i)
		{
			if(list.get(i).getGuid() == (Integer)packet.getData())
			{
				list.remove(i);
				break;
			}
		}
	}

	/**
	 * Processes an UpdatePlayer packet; updates an existing player
	 * 
	 * @param packet Packet to process
	 */
	private void onUpdatePlayer(Packet packet)
	{
		List<Player> list = euchreApplet.getPlayerList();
		for(Player player : list)
			player.updateData(packet);
	}
	
	
	/**
	 * Processes a requestBid packet.
	 * 
	 * @param packet a String containing the suit of the flipped card
	 */
	private void onRequestBid(Packet packet)
	{
		String suit = (String)packet.getData();
		if(suit.toLowerCase().charAt(0) == 'h')
			suit = "hearts";
		else if(suit.toLowerCase().charAt(0) == 'd')
			suit = "diamonds";
		else if(suit.toLowerCase().charAt(0) == 'c')
			suit = "clubs";
		else if(suit.toLowerCase().charAt(0) == 's')
			suit = "spades";
		JOptionPane.showOptionDialog(euchreApplet, "Do you want " + suit + " to be trump?", "Bidding", JOptionPane.YES_NO_OPTION, 
									 JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.NO_OPTION);		
	}
	
	/**
	 * Processes a requestAlternateBid packet
	 * 
	 * @param packet a String containing the suit of the flipped card
	 */
	private void onRequestAlternateBid(Packet packet)
	{
		String suit = (String)packet.getData();
		
		String[] suits = new String[3];
		int i = 0;
		if(suit.toLowerCase().charAt(0) != 'd')
			suits[i++] = "diamonds";
		if(suit.toLowerCase().charAt(0) != 'h')
			suits[i++] = "hearts";
		if(suit.toLowerCase().charAt(0) != 'c')
			suits[i++] = "clubs";
		if(suit.toLowerCase().charAt(0) != 's')
			suits[i++] = "spades";
				
		JOptionPane.showOptionDialog(euchreApplet, "Would you like to name trump?", "Bidding", JOptionPane.YES_NO_OPTION, 
				 JOptionPane.QUESTION_MESSAGE, null,suits, JOptionPane.NO_OPTION);
	}
	
	/**
	 * Processes a dealerDiscard packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onDealerDiscard(Packet packet)
	{
		//TODO: implement this		
	}
		
	/**
	 * Processes a goingAlone packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onGoingAlone(Packet packet)
	{
		int option;
		option = JOptionPane.showOptionDialog(euchreApplet, "Would you like to go alone?", "Going Alone?", JOptionPane.YES_NO_OPTION, 
				 JOptionPane.QUESTION_MESSAGE, null,null, JOptionPane.NO_OPTION);
		
		if(option == JOptionPane.YES_OPTION)
			send(Opcode.goingAlone, new Boolean(true));
		else if(option == JOptionPane.NO_OPTION)
			send(Opcode.goingAlone, new Boolean(false));
		else
		{
			send(Opcode.goingAlone, new Boolean(false));
			Trace.dprint("Did not receive answer from user, defaulting to 'Not going alone'");
		}
	}
	
	/**
	 * Processes a throwCard packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onThrowCard(Packet packet)
	{
		//TODO: implement this
	}
}
