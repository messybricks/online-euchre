package utility;

import java.io.*;

public class Packet
{
	private Opcode opcode;
	private Serializable data;

	public Packet(Opcode opcode)
	{
		this(opcode, null);
	}

	public Packet(Opcode opcode, Serializable data)
	{
		this.opcode = opcode;
		this.data = data;
	}

	// the structure of a flattened packet follows this pattern
	// 4 byte integer opcode
	// 4 byte integer packet size
	// variable size reflexive packet
	public byte[] flatten()
	{
		ByteArrayOutputStream stream = null;
		DataOutputStream bitConverter = null;

		try
		{
			if(data == null)
			{
				// if data is null, we create a simple packet of 8 bytes. it contains the opcode, and signifies that there is no data by writing a size of 0.
				stream = new ByteArrayOutputStream(8);
				bitConverter = new DataOutputStream(stream);
				bitConverter.write(opcode.ordinal());
				bitConverter.write(0);
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
				bitConverter.write(opcode.ordinal());
				bitConverter.write(target.size());
				target.writeTo(stream);
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
