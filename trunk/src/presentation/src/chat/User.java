package chat;

import java.io.Serializable;

/**
 * contains information for individual users 
 */
public class User implements Serializable
{
	private String username;

	/**
	 * Default Constructor
	 * 
	 * @param user a String containing the name of this user
	 */
	public User(String user)
	{
		username = user;
	}

	/**
	 * returns the name of this user
	 * 
	 * @return the name of this user
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * returns the name of this user
	 * 
	 * @return the name of this user
	 */
	public String toString()
	{
		return getUsername();
	}

}
