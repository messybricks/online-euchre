package utility;

/**
 * The exception that is thrown when an invalid opcode ordinal is detected. This is usually a sign of packet corruption.
 */
public class InvalidOpcodeException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance of the InvalidOpcodeException class.
	 * @param ordinal Invalid ordinal
	 */
	public InvalidOpcodeException(int ordinal)
	{
		super("An invalid opcode ordinal (" + ordinal + ") was found. This is generally a sign of packet corruption.");
	}
}
