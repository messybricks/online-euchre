package utility;

public class Trace
{
	public static void dprint(String format, Object... args)
	{
		String s = String.format(format, args);
		System.out.println(s);
	}
}
