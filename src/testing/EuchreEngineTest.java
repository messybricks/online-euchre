package testing;

import utility.Trace;
import chat.*;
import game.*;
import junit.framework.TestCase;

/**
 * unit testing for the EuchreEngine class
 */
public class EuchreEngineTest extends TestCase {

	private Player one = new Player(new User("dummy"), new FakeThread());
	private Player two = new Player(new User("dummy"), new FakeThread());
	private Player three = new Player(new User("dummy"), new FakeThread());
	private Player four = new Player(new User("dummy"), new FakeThread());
	private EuchreEngine e;
	
	/**
	 * unit testing for the EuchreEngine constructor
	 */
	public void testEuchreEngine() {
		e = new EuchreEngine(one, two, three, four);
		assertTrue(e != null);
		
		e.start();
	}

	/**
	 * unit testing for the start method
	 */
	public void testStart() {
		testEuchreEngine();
		assertTrue(e.getState() == EuchreEngine.FIRST_BID);
	}

	/**
	 * unit testing for the receiveBid method
	 */
	public void testReceiveBid() {
		testEuchreEngine();
		
		e.receiveBid("p");
		assertTrue(e.getState() == EuchreEngine.SECOND_BID);
		e.receiveBid("p");
		assertTrue(e.getState() == EuchreEngine.THIRD_BID);
		e.receiveBid("s");
		assertTrue(e.getState() == EuchreEngine.DEALER_DISCARD);
	}

	/**
	 * unit testing for the goingAlone method
	 */
	public void testGoingAlone() {
		testReceiveBid();
		e.goingAlone();
		assertTrue(e.getState() == EuchreEngine.GOING_ALONE);
	}

	/**
	 * unit testing for the setGoingAlone method
	 */
	public void testSetGoingAlone() {
		testGoingAlone();
		e.setGoingAlone(false);
		assertTrue(e.getState() == EuchreEngine.FIRST_PLAYER_THROWS_CARD);
	}
	
	/**
	 * unit testing for the throwCard method
	 */
	public void testThrowCard() {
		testGoingAlone();
		e.setGoingAlone(false);
		
		
		
		char trump = e.getTrump();
		assertTrue(e.getState() == EuchreEngine.FIRST_PLAYER_THROWS_CARD);
		e.receiveCard(new Card(trump, 10));
		assertTrue(e.getState() == EuchreEngine.SECOND_PLAYER_THROWS_CARD);
		e.receiveCard(new Card('d', 12));
		assertTrue(e.getState() == EuchreEngine.THIRD_PLAYER_THROWS_CARD);
		e.receiveCard(new Card(trump, 11));
		assertTrue(e.getState() == EuchreEngine.FOURTH_PLAYER_THROWS_CARD);
		e.receiveCard(new Card(trump, 9));
	}

	/**
	 * unit testing for the getState method
	 */
	public void testGetState() {
		testEuchreEngine();
		assertTrue(e.getState() == EuchreEngine.FIRST_BID);
	}

}
