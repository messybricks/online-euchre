package chat;

import server.ServerSocketThread;
import utility.Opcode;
import utility.Packet;

/**
 * Class to manage chat messages.
 * 
 * @author Jason King
 *
 */
public class ChatManager
{
	private ServerSocketThread thread;

	/*
	 * Default Constructor.
	 */
	public ChatManager(ServerSocketThread serverSocketThread)
	{
		thread = serverSocketThread;
	}

	/*
	 * Send a chat object.
	 * 
	 * @param obj The chat object to send
	 * @return boolean True if it was sent successfully, false otherwise
	 */
	public boolean send(ChatObject obj)
	{
		User dest = obj.getDest();
		if(obj.getDest() == null)
		{
			thread.sendGlobal(Opcode.SendMessage, obj);
		}
		else
		{
			thread.sendSpecified(dest.getUsername(), Opcode.SendMessage, obj);
		}
		//Packet pckt = new Packet(opcode, obj);
		return false;
	}
}
