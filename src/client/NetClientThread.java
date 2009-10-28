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
}
