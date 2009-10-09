package server;

import java.util.ArrayList;

import chat.ChatObject;
import chat.User;
import server.ServerSocketThread;
import utility.Opcode;
import utility.Packet;
import utility.Trace;

/**
 * Class to manage chat messages.
 * 
 * @author Jason King
 *
 */
public class UserManager
{
	private ServerSocketThread thread;
	private ArrayList<User> users;

	/**
	 * Default Constructor.
	 * 
	 * @param serverSocketThread Socket thread to send messages via
	 */
	public UserManager(ServerSocketThread serverSocketThread)
	{
		thread = serverSocketThread;
		users = new ArrayList<User>();
	}

	/**
	 * Adds a user to this manager's client list.
	 * 
	 * @param usr User to add
	 */
	public void add(User usr)
	{
		users.add(usr);
		thread.sendGlobal(Opcode.UpdateUsers, users);
	}

	/**
	 * Removes a user from this manager's client list.
	 * 
	 * @param usr User to remove
	 */
	public void remove(User usr)
	{
		int removeIndex = 0;
		for(int x = 0; x < users.size(); x++)
		{
			if(users.get(x).getUsername().compareTo(usr.getUsername()) == 0)
			{
				removeIndex = x;
				break;
			}
		}

		users.remove(removeIndex);
		thread.sendGlobal(Opcode.UpdateUsers, users);
	}
}