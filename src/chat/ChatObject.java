package chat;

/**
 * Class to hold everything needed in a chat object.
 * 
 */
class ChatObject
{
    private User sourceUser;
    private User destUser;
    private String message;

    /**
     * Creates a chat object to transmit a String from one
     * user to another.
     *
     * @param source the user that sent the message
     * @param dest the user to recieve the message
     * @param mess the String to be sent
     */
    public ChatObject(User source, User dest, String mess)
    {
        sourceUser = source;
	destUser = dest;
	message = mess;
    }

    /**
     * Get the user that sent the message
     *
     * @returns User the user that sent the message
     */
    public User getSource()
    {
        return sourceUser;
    }

    /**
     * Get the user that recieves the message
     *
     * @returns User the User that recieves the message
     */
    public User getDest()
    {
        return destUser;
    }
    
    /**
     * Get the text of the message
     * 
     * @return the message
     */
    public String getMessage()
    {
    	return message;
    }
}
