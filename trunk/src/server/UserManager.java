package server;

import java.util.ArrayList;

import chat.ChatObject;
import chat.User;
import server.ServerSocketThread;
import utility.Opcode;
import utility.Packet;

/**
 * Class to manage chat messages.
 * 
 * @author Jason King
 *
 */
public class UserManager
{
	private ServerSocketThread thread;
	ArrayList<User> users;
	
	/*
	 * Default Constructor.
	 */
    public UserManager(ServerSocketThread serverSocketThread)
    {
    	thread = serverSocketThread;
    	users = new ArrayList<User>();
    }

    /*
     * Send a chat object.
     * 
     * @param obj The chat object to send
     * @return boolean True if it was sent succesfully, false otherwise
     */
    public boolean add(User usr)
    {
    	users.add(usr);
       	thread.sendGlobal(Opcode.AddUser, users);
      //Packet pckt = new Packet(opcode, obj);
        return false;
    }

    /*
     * Send a chat object.
     * 
     * @param obj The chat object to send
     * @return boolean True if it was sent succesfully, false otherwise
     */
    public boolean remove(User usr)
    {
    	users.remove(usr);
       	thread.sendGlobal(Opcode.AddUser, users);
      //Packet pckt = new Packet(opcode, obj);
        return false;
    }
}