package server;

import java.io.*;
import java.net.*;
import java.util.*;
import utility.*;

/**
 * This thread manages two queues; one for sending data over the network and one for receiving it.
 */
public class PacketQueueThread extends NetworkThread
{
	private long lastPing = 0;
	private long lastPong = 0;
	
	private static int uid = 0;
	
	public PacketQueueThread(Socket client)
	{
		super(client, "PacketQueue_" + ++uid);
	}
	
	public long getLastPing()
	{
		return lastPing;
	}
	
	public long getLastPong()
	{
		return lastPong;
	}
	
	public void setPong()
	{
		lastPong = System.currentTimeMillis();
	}
	
	public void ping()
	{
		send(new Packet(Opcode.Ping));
		lastPing = System.currentTimeMillis();
	}
	
	protected void processPacket()
	{
		Packet packet = receive();
		
		if(packet.getOpcode() == Opcode.Pong)
		{
			setPong();
		}
		else
		{
			Trace.dprint("Received packet with unimplemented opcode '%s' - ignoring.", packet.getOpcode().toString());
		}
	}
}
