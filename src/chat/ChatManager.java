package chat;

import utility.Packet;

class ChatManager
{
    public ChatManager()
    {
    }

    public boolean send(ChatObject obj)
    {
        User dest = obj.getDest();
      //Packet pckt = new Packet(opcode, obj);
        return false;
    }
}
