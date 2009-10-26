package utility;

import java.io.Serializable;

/**
 * Exposes a method to send Packet data over some sort of connection.
 * 
 * @author bert
 *
 */
public interface TransactionThread
{
	/**
	 * Enqueues a packet to be sent.
	 * 
	 * @param packet Packet to send
	 */
	public void send(Packet packet);
	
	/**
	 * Enqueues a new packet to be sent.
	 * 
	 * @param opcode Opcode to assign to the new packet
	 */
	public void send(Opcode opcode);

	/**
	 * Enqueues a new packet to be sent.
	 * 
	 * @param opcode Opcode to assign to the new packet
	 * @param datum A serializable object to attach to the packet
	 */
	public void send(Opcode opcode, Serializable datum);
}
