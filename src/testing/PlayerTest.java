package testing;

import utility.Trace;
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
	}



	public void testToString() 
	{
		player.pickupCard(new Card(2,'c'));
		player.pickupCard(new Card(3,'c'));
		player.pickupCard(new Card(2,'d'));
		Trace.dprint(player.toString());
		assertTrue(player.toString().equals("test: Collection(2 of Clubs, 3 of Clubs, 2 of Diamonds)"));
		
	}
	
	public void setUp()
	{
		player = new Player(new User("test"));
	}

}
