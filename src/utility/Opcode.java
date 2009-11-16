package utility;

/**
 * Enumerates the possible actions a Packet can perform.
 */
public enum Opcode
{
	// no operation. having 0 mean no operation makes debugging packet stuff easier. you really shouldn't use this for anything
	Nop,

	// server/client connection commands
	Ping,
	Pong,
	Auth,
	Rename,
	Quit,

	// chat commands
	SendMessage,
	UpdateUsers,
	
	// player class automation
	CreatePlayer,
	RemovePlayer,
	UpdatePlayer,
	
	// ui signals
	GameStarting,
	
	//prompts from the EuchreEngine
	requestBid,
	requestAlternateBid,
	dealerDiscard,
	goingAlone,
	throwCard,
	displayCard;

	/**
	 * Generates an instance of Opcode with the given ordinal value.
	 * 
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
