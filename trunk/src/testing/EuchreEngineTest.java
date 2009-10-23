package testing;

import junit.framework.TestCase;
import game.*;

public class EuchreEngineTest extends TestCase {

	private EuchreEngine e;
	
	public void testStart() 
	{
		e = new EuchreEngine();
		e.start();
		assertTrue(e.getState() == EuchreEngine.END_OF_ROUND);
	}
}
