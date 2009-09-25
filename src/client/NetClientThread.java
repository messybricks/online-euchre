package client;

import java.io.*;
import java.net.*;
import java.util.*;
import utility.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class NetClientThread extends NetworkThread
{
	public NetClientThread(Socket client)
	{
		super(client, "NetClient");
	}
	
	protected void processPacket()
	{
		Packet packet = receive();
		
		if(packet.getOpcode() == Opcode.Ping)
		{
			send(new Packet(Opcode.Pong));
		}
		else
		{
			Trace.dprint("Received packet with unimplemented opcode '%s' - ignoring.", packet.getOpcode().toString());
		}
	}
}
