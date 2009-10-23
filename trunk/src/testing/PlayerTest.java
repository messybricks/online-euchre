package testing;

import chat.User;
import game.*;
import junit.framework.TestCase;

public class PlayerTest extends TestCase 
{
	
	private Player player;

	public void testPlayerUser() 
	{
		Player p = new Player(new User("test"));
		
		assertTrue(p.toString().indexOf("test") == 0);
	}

	public void testPlayerString() 
	{
		Player p = new Player("test");
		assertTrue(p.toString().indexOf("test") == 0);
	}

	public void testPickupCard() 
	{
		Card d = new Card(1,'c');
		Card c = player.pickupCard(d);
		assertTrue(d==c);
	}

	public void testPlayCard() 
	{
		player.pickupCard(new Card(1,'c'));
		player.pickupCard(new Card('d',13));
		assertTrue(player.playCard(0).toString().equals("Ace of Clubs"));
		assertTrue(player.playCard(0).toString().equals("King of Diamonds"));
		//fail("Not yet implemented");
	}

	public void testMoveCard() 
	{
		player.pickupCard(new Card(1,'c'));
		player.pickupCard(new Card('d',13));
		player.moveCard(1, 0);
		
		
		fail("Not yet implemented");
	}

	public void testSwapCards() 
	{
		fail("Not yet implemented");
	}

	public void testGetUsername() 
	{
		fail("Not yet implemented");
	}

	public void testToString() 
	{
		fail("Not yet implemented");
	}
	
	public void setUp()
	{
		player = new Player("test");
	}

}
