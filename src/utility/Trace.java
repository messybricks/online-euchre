package utility;

import java.lang.reflect.*;

/**
 * Provides functions to assist in tracing code execution.
 */
public class Trace
{
	/**
	 * Prints a debug string to the console if this is a debug build.
	 * @param format Format string to print
	 * @param args Optional arguments to the format string
	 */
	public static void dprint(String format, Object... args)
	{
		String s = String.format(format, args);
		System.out.println(s);
	}
	
	/**
	 * Prints an array using dprint.
	 * @param array Array to print
	 */
	public static void dprintArray(Object array)
	{
		StringBuilder string = new StringBuilder("[");
		int length = Array.getLength(array);
		
		for(int i = 0; i < length - 1; ++i)
		{
			string.append(Array.get(array, i).toString());
			string.append(", ");
		}
		
		if(length > 0)
			string.append(Array.get(array, length - 1));
		
		string.append(']');
		dprint(string.toString());
	}

	/**
	 * Prints a network debugging string to the console if this is a network debugging build.
	 * @param format Format string to print
	 * @param args Optional arguments to the format string
	 */
	public static void nprint(String format, Object... args)
	{
		dprint(format, args);
	}
}
