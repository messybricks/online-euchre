package utility;

/**
 * The exception that is thrown when a packet is corrupted and cannot be read.
 */
public class InvalidPacketException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of the InvalidOpcodeException class.
	 * 
	 * @param ordinal Invalid ordinal
	 */
	public InvalidPacketException(String message)
	{
		super("An instance of Packet was corrupted and could not be read. Failed: " + message);
	}
}
