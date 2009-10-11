package utility;

import java.io.*;

/**
 * A packet is a datum to be sent over a network interface. This implementation is expanded to include automatic data
 * serialization, so it becomes possible to seemingly send objects directly over the internet. A packet consists of an
 * A packet contains an opcode, which defines why the packet is sent, and a datum which describes the operation.
 */
public class Packet
{
	private Opcode opcode;
	private Serializable data;

	public static final int HEADER_SIZE = 8;

	/**
	 * Creates a new instance of the Packet class.
	 * 
	 * @param opcode Opcode defining the action to perform
	 */
	public Packet(Opcode opcode)
	{
		this(opcode, null);
	}

	/**
	 * Creates a new instance of the Packet class.
	 * 
	 * @param opcode Opcode defining the action to perform
	 * @para data Some serializable object describing how to perform the action
	 */
	public Packet(Opcode opcode, Serializable data)
	{
		this.opcode = opcode;
		this.data = data;
	}

	/**
	 * Returns this packet's opcode.
	 * 
	 * @return this packet's opcode
	 */
	public Opcode getOpcode()
	{
		return opcode;
	}

	/**
	 * Returns this packet's datum.
	 * 
	 * @return this packet's datum
	 */
	public Serializable getData()
	{
		return data;
	}

	/**
	 * Recreates a packet from a byte array created using the Packet.flatten() method.
	 * 
	 * @param array A byte array created using the Packet.flatten() method
	 * @throws InvalidPacketException if the given array does not represent a valid flattened Packet object
	 * @throws IllegalArgumentException if array is null
	 */
	public Packet(byte[] array) throws InvalidPacketException, IllegalArgumentException
	{
		// check for validity
		if(array == null)
			throw new IllegalArgumentException("Cannot instantiate a packet from a null array.");
		if(array.length < HEADER_SIZE)
			throw new InvalidPacketException("Packet contains a corrupted header.");

		ByteArrayInputStream stream = new ByteArrayInputStream(array);
		DataInputStream dataIn = new DataInputStream(stream);

		try
		{
			// decode the opcode from its ordinal
			opcode = Opcode.fromOrdinal(dataIn.readInt());
			int dataSize = dataIn.readInt();

			// if dataSize is zero, then we have no datum to read. otherwise, deserialize it
			if(dataSize == 0)
				data = null;
			else
			{
				ObjectInputStream objectIn = new ObjectInputStream(stream);
				// the following cast cannot fail, because if an object is not serializable, readObject() will fail
				data = (Serializable) objectIn.readObject();
			}
		}
		catch (InvalidOpcodeException ex)
		{
			throw new InvalidPacketException(ex.getMessage());
		}
		catch (ClassNotFoundException ex)
		{
			throw new InvalidPacketException("Packet referenced a typename that does not exist in this AppDomain.");
		}
		catch (IOException ex)
		{
			throw new InvalidPacketException(ex.getMessage());
		}
	}

	// the structure of a flattened packet follows this pattern
	// 4 byte integer opcode
	// 4 byte integer packet size
	// variable size packet datum
	/**
	 * Flattens this packet into an array of bytes to be sent over a network.
	 */
	public byte[] flatten()
	{
		ByteArrayOutputStream stream = null;
		DataOutputStream bitConverter = null;

		try
		{
			if(data == null)
			{
				// if data is null, we create a simple packet of 8 bytes. it contains the opcode, and signifies that there is no data by writing a size of 0.
				stream = new ByteArrayOutputStream(HEADER_SIZE);
				bitConverter = new DataOutputStream(stream);
				bitConverter.writeInt(opcode.ordinal());
				bitConverter.writeInt(0);
			}
			else
			{
				// if we have data to serialize, we create a target stream to write to. this allows us to determine the size of the serialized data
				ByteArrayOutputStream target = new ByteArrayOutputStream();
				ObjectOutputStream serializer = new ObjectOutputStream(target);
				serializer.writeObject(data);
				serializer.close();

				// next, we create our output stream, write the opcode and packet size, and then flush the packet data to the output stream.
				stream = new ByteArrayOutputStream();
				bitConverter = new DataOutputStream(stream);
				bitConverter.writeInt(opcode.ordinal());
				bitConverter.writeInt(target.size());
				stream.write(target.toByteArray());
			}

			return stream.toByteArray();
		}
		catch (IOException e)
		{
			Trace.dprint("Unable to flatten packet. Message: %s", e.getMessage());
			return new byte[0];
		}
	}
}
