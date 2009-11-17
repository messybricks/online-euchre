package utility;
import java.io.*;

/**
 * Represents a piece of data that has a specific target, as opposed to data meant to be sent globally.
 *
 */
public class TargetedPackage implements Serializable {
	
	private int targetGUID;
	private Serializable datum;
	
	private static final long serialVersionUID = 0L;
	
	/**
	 * Creates a new TargetedPackage with the given target player GUID and piece of data.
	 * 
	 * @param target target player GUID
	 * @param data piece of data to send. must be serializable (obviously)
	 */
	public TargetedPackage(int target, Serializable data)
	{
		targetGUID = target;
		datum = data;
	}
	
	/**
	 * Gets the data associated with this targeted packet.
	 * 
	 * @return ...data
	 */
	public Serializable getData()
	{
		return datum;
	}
	
	/**
	 * Gets the target this packet is addressed to.
	 * 
	 * @return this packet's target (you dolt)
	 */
	public int getTarget()
	{
		return targetGUID;
	}

}
