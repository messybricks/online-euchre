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
	private int state;
	private String suit;
	

	public static final int GOING_ALONE = 0;
	public static final int SET_TRUMP = 1;
	public static final int NAMING_TRUMP = 2;
	
	
	
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
		if(packet.getTargetGUID() == -1 || (euchreApplet.getPlayer() != null && packet.getTargetGUID() == euchreApplet.getPlayer().getGuid()))
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
			else if(packet.getOpcode() == Opcode.GameStarting)
				onGameStarting(packet);
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
			else if(packet.getOpcode() == Opcode.displayCard)
				onDisplayCard(packet);
			else
				Trace.dprint("Received packet with unimplemented opcode '%s' - ignoring.", packet.getOpcode().toString());
		}
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
	 * Signals the EuchreApplet that teams have been chosen and the game is about to begin.
	 * 
	 * @param packet Packet to process
	 */
	private void onGameStarting(Packet packet)
	{
		euchreApplet.onGameStarting();
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
			if(list.get(i).getGuid() == ((Integer)packet.getData()).intValue())
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
		this.suit = suit;
		String suit2 = "";
		if(suit.toLowerCase().charAt(0) == 'h')
			suit2 = "hearts";
		else if(suit.toLowerCase().charAt(0) == 'd')
			suit2 = "diamonds";
		else if(suit.toLowerCase().charAt(0) == 'c')
			suit2 = "clubs";
		else if(suit.toLowerCase().charAt(0) == 's')
			suit2 = "spades";
	//	JOptionPane.showOptionDialog(euchreApplet, "Do you want " + suit + " to be trump?", "Bidding", JOptionPane.YES_NO_OPTION, 
	//								 JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.NO_OPTION);	

		euchreApplet.displayYesNoMessage("Would you like " + suit2 + " to be trump?");
		
		state = SET_TRUMP;
		
/*		if(t == 1)
			send(Opcode.requestBid, suit);
		else
			send(Opcode.requestBid, "p");*/
	}
	
	/**
	 * Processes a requestAlternateBid packet
	 * 
	 * @param packet a String containing the suit of the flipped card
	 */
	private void onRequestAlternateBid(Packet packet)
	{
		/*String suit = (String)packet.getData();
		
		String[] suits = new String[3];
		int i = 0;
		if(suit.toLowerCase().charAt(0) != 'd')
			suits[i++] = "diamonds";
		if(suit.toLowerCase().charAt(0) != 'h')
			suits[i++] = "hearts";
		if(suit.toLowerCase().charAt(0) != 'c')
			suits[i++] = "clubs";
		if(suit.toLowerCase().charAt(0) != 's')
			suits[i++] = "spades";*/
		
		int trumpThatCannotBe = -1;
		
		switch (suit.toLowerCase().charAt(0))
		{
		case 'h':
			trumpThatCannotBe = 1;
			break;
		case 'c':
			trumpThatCannotBe = 2;
			break;
		case 'd':
			trumpThatCannotBe = 3;
			break;
		case 's':
			trumpThatCannotBe = 4;
			break;
		}
		
		euchreApplet.displayTrumpMessage("Would you like to name trump?", trumpThatCannotBe);
		
		state = NAMING_TRUMP;
//		JOptionPane.showOptionDialog(euchreApplet, "Would you like to name trump?", "Bidding", JOptionPane.YES_NO_OPTION, 
//				 JOptionPane.QUESTION_MESSAGE, null,suits, JOptionPane.NO_OPTION);

/*		int c, d, s, h;
		if(answer == 1)
		{
			euchreApplet.displayYesNoMessage("Would you like Clubs to be trump?");	
			if(c == 0)
			{
				euchreApplet.displayYesNoMessage("Would you like to name Diamonds as trump?");	
				if(d ==  0)
				{
					euchreApplet.displayYesNoMessage("Would you like to name Spades as trump?");	
					if(s == 0)
					{
						euchreApplet.displayYesNoMessage("Naming Hearts as trump. Confirm?");
						if(h == 1)
						{
							send(Opcode.requestAlternateBid, "h");
						}
						else
						{
							answer = 0;
							send(Opcode.requestAlternateBid, "p");
						}
						
					}
					else
					{
						send(Opcode.requestAlternateBid, "s");
					}
				}
				else
				{
					send(Opcode.requestAlternateBid, "d");
				}
			}
			else
			{
				send(Opcode.requestAlternateBid, "c");
			}
		}
		else
		{
			send(Opcode.requestAlternateBid, "p");
		}
		*/
	}
	
	/**
	 * Processes a dealerDiscard packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onDealerDiscard(Packet packet)
	{
		//TODO: prompt the dealer to discard a card from his/her hand	
		
		//send(Opcode.dealerDiscard);
	}
		
	/**
	 * Processes a goingAlone packet.
	 * 
	 * @param packet Packet to process
	 */
	private void onGoingAlone(Packet packet)
	{
		int option;
//		option = JOptionPane.showOptionDialog(euchreApplet, "Would you like to go alone?", "Going Alone?", JOptionPane.YES_NO_OPTION, 
//				 JOptionPane.QUESTION_MESSAGE, null,null, JOptionPane.NO_OPTION);
		
		euchreApplet.displayYesNoMessage("Would you like to go alone?");
		state=GOING_ALONE;
	}
	
	private void goingAlone(int option){
		if(option == 1)
			send(Opcode.goingAlone, new Boolean(true));
		else if(option == 0)
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
	
	/**
	 * Processes a displayCard packet.
	 * 
	 * @param packet Packet to process//TODO seperate into another method
	 */
	private void onDisplayCard(Packet packet)
	{
		//TODO: implement this
	}
	
	/***
	 * 
	 * @param response
	 */
	public void respond(int response)
	{
		switch(state){
			case GOING_ALONE:
				goingAlone(response);
				break;

			case NAMING_TRUMP:
				if(response == 1)
					send(Opcode.requestBid, suit);
				else
					send(Opcode.requestBid, "p");
				break;
				
			case SET_TRUMP:
				if(response == 1)
					send(Opcode.requestBid, suit);
				else
					send(Opcode.requestBid, "p");
				break;
		}
	}
	
}
