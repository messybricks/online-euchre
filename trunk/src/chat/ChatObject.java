package chat;

import java.io.*;
import java.awt.*;

/**
 * container class for variables relating to a chat message 
 */
public class ChatObject implements Serializable
{
	private User sourceUser;
	private User destUser;
	private String message;
	private Color color;

	/**
	 * Creates a chat object to transmit a String from one
	 * user to another. This uses the default color - Black.
	 *
	 * @param source the user that sent the message
	 * @param dest the user to receive the message
	 * @param mess the String to be sent
	 */
	public ChatObject(User source, User dest, String mess)
	{
		sourceUser = source;
		destUser = dest;
		message = mess;
		color = Color.PINK;
	}

	/**
	 * Creates a chat object to transmit a String from one
	 * user to another.
	 *
	 * @param source the user that sent the message
	 * @param dest the user to receive the message
	 * @param mess the String to be sent
	 * @param messageColor the color to display the message as
	 */
	public ChatObject(User source, User dest, String mess, Color messageColor)
	{
		this(source, dest, mess);
		color = messageColor;
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
	 * Get the intended color to display this message
	 *
	 * @returns User the color this message should display as
	 */
	public Color getColor()
	{
		return color;
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
