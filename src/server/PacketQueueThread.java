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
	
	public PacketQueueThread(Socket client)
	{
		super(client);
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
}
