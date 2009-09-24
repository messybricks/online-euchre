package chat;

import utility.Packet;

/**
 * Class to manage chat messages.
 * 
 * @author Jason King
 *
 */
class ChatManager
{
	/*
	 * Default Constructor.
	 */
    public ChatManager()
    {
    }

    /*
     * Send a chat object.
     * 
     * @param obj The chat object to send
     * @return boolean True if it was sent succesfully, false otherwise
     */
    public boolean send(ChatObject obj)
    {
        User dest = obj.getDest();
      //Packet pckt = new Packet(opcode, obj);
        return false;
    }
}
