package chat;

import java.io.Serializable;

class User implements Serializable
{
    private String username;

    public User(String user)
    {
        username = user;
    }

    public String getUsername()
    {
        return username;
    }
    
    public String toString()
    {
    	return username;
    }

}