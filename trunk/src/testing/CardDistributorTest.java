package testing;
import chat.User;
import game.*;

import junit.framework.TestCase;

public class CardDistributorTest extends TestCase {

	
	public void testDealRound() {
		Player one =new Player(new User("one"),new FakeThread());
		Player two =new Player(new User("two"),new FakeThread());
		Player three =new Player(new User("three"),new FakeThread());
		Player four =new Player(new User("four"),new FakeThread());
		CardDistributor dealer =new CardDistributor(one,two,three,four);
		
		//make sure that each player properly gets 5 cards after dealRound is called
		dealer.dealRound();
		
		//try to play 5 cards, if any has less, an Exception will be caught
		try
		{
			one.playCard(0);
			one.playCard(0);
			one.playCard(0);
			one.playCard(0);
			one.playCard(0);
			two.playCard(0);
			two.playCard(0);
			two.playCard(0);
			two.playCard(0);
			two.playCard(0);
			three.playCard(0);
			three.playCard(0);
			three.playCard(0);
			three.playCard(0);
			three.playCard(0);
			four.playCard(0);
			four.playCard(0);
			four.playCard(0);
			four.playCard(0);
			four.playCard(0);
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		//try to play a sixth card for each player, should always throw an exception
		try
		{
			one.playCard(0);
			assertTrue(false);
		}
		catch(Exception e){}
		try
		{
			two.playCard(0);
			assertTrue(false);
		}
		catch(Exception e){}
		try
		{
			three.playCard(0);
			assertTrue(false);
		}
		catch(Exception e){}
		try
		{
			four.playCard(0);
			assertTrue(false);
		}
		catch(Exception e){}
	}



	public void testNextRound() {
		Player one =new Player(new User("one"),new FakeThread());
		Player two =new Player(new User("two"),new FakeThread());
		Player three =new Player(new User("three"),new FakeThread());
		Player four =new Player(new User("four"),new FakeThread());
		CardDistributor dealer =new CardDistributor(one,two,three,four);
		dealer.nextRound();
		
		//check if players are rotated properly
		assertTrue(dealer.getPlayerOrder()[0]==four);
		assertTrue(dealer.getPlayerOrder()[1]==one);
		assertTrue(dealer.getPlayerOrder()[2]==two);
		assertTrue(dealer.getPlayerOrder()[3]==three);
		
	}



}
