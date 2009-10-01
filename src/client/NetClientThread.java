package client;

import java.net.*;
import utility.*;
import chat.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class NetClientThread extends NetworkThread
{
	// do not modify code here! anything you may want to change is found below, in packet processing
	private EuchreApplet euchreApplet;
	
	/**
	 * Creates a new instance of the NetClientThread class wrapping the given socket.
	 * @param client Socket connection to server
	 * @param applet 
	 */
	public NetClientThread(Socket client, EuchreApplet applet)
	{
		super(client, "NetClient");
		euchreApplet = applet;
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
		else
			Trace.dprint("Received packet with unimplemented opcode '%s' - ignoring.", packet.getOpcode().toString());
	}
	
	/**
	 * Processes a Ping packet.
	 * @param packet Packet to process
	 */
	private void onPing(Packet packet)
	{
		send(Opcode.Pong);
	}
	
	/**
	 * Processes a Quit packet.
	 * @param packet Packet to process
	 */
	private void onQuit(Packet packet)
	{
		// TODO: Implement client Quit packet
	}
	
	/**
	 * Processes a SendMessage packet.
	 * @param packet Packet to process
	 */
	private void onSendMessage(Packet packet)
	{
		ChatObject chat = (ChatObject)packet.getData();
		euchreApplet.receiveMessage(chat);
	}
}
