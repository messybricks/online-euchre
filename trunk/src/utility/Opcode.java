package utility;

public enum Opcode
{
	// server/client connection commands
	Ping,
	Pong,
	Quit,

	// chat commands
	SendMessage;

	/**
	 * Generates an instance of Opcode with the given ordinal value.
	 * @param ordinal Ordinal to use
	 * @return Opcode with given ordinal
	 * @throws InvalidOpcodeException if the given ordinal does not exist in the Opcode enumeration
	 */
	public static Opcode fromOrdinal(int ordinal) throws InvalidOpcodeException
	{
		for(Opcode value : Opcode.values())
			if(value.ordinal() == ordinal)
				return value;
		
		throw new InvalidOpcodeException(ordinal);
	}
}
