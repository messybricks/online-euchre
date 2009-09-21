package chat;

class chatManager
{
    public chatManager()
    {
    }

    public boolean send(chatObject obj)
    {
        User dest = obj.getDest();
        Packet pckt = new Packet(opcode, obj);

    }
}
