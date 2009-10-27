package testing;

import junit.framework.TestCase;
import game.*;

public class CardTest extends TestCase {
	
	private Card c1,c2,c3,c4,c5,c6,c7,c8;
	private CardCollection col1, col2, col3, col4, col5;
	private Hand h1, h2;
	private Deck d1, d2, d3,d4,d5;

	public void setUp() throws Exception
	{
		super.setUp();
		setUpCardTesting();
		setUpCardCollectionTesting();
		setUpDeckandHandTesting();
	}
	
	public void testCards()
	{
		//test created cards for values.
		assertTrue(c1.getValue() == 3);
		assertTrue(c2.getValue() == 5);
		assertTrue(c3.getValue() == 12);
		assertTrue(c4.getValue() == 14);
		assertTrue(c5.getValue() == 0);
		assertTrue(c6.getValue() == 0);
		assertTrue(c7.getValue() == 13);
		assertTrue(c8.getValue() == 0);
		
		//test created cards for suit.
		assertTrue(c1.getSuit() == 'c');
		assertTrue(c2.getSuit() == 'd');
		assertTrue(c3.getSuit() == 'h');
		assertTrue(c4.getSuit() == 's');
		assertTrue(c5.getSuit() == 'd');
		assertTrue(c6.getSuit() == 'd');
		assertTrue(c7.getSuit() == 's');
		assertTrue(c8.getSuit() == 'd');
		
		//test the test for null card.
		assertTrue(Card.ifNull(c8));
		assertFalse(Card.ifNull(c1));
		
		//test the to string.
		assertTrue(c1.toString().equals("3 of Clubs"));
		assertTrue(c3.toString().equals("Queen of Hearts"));
		assertTrue(c8.toString().equals("Null card"));
	}
	
	public void testCardCollection()
	{
		//test created collections for size.
		assertTrue(col1.getSize() == 5);
		assertTrue(col2.getSize() == 2);
		assertTrue(col3.getSize() == 15);
		assertTrue(col4.getSize() == 0);
		assertTrue(col5.getSize() == 0);
		
		//test adding cards.
		col2.add(c1);
		col2.add(c2);
		col1.add(c4);
		assertTrue(col2.getNumberOfCards() == 2);
		assertTrue(col2.toString().equals("Collection(" + c1 + ", " + c2 + ")"));
		//add an extra card.
		collectionException1();
		//add a null card.
		collectionException2();
		
		//test removing cards.
		//remove card 2 from collection, and see if it is c2.
		assertTrue(col2.remove(1).toString().equals(c2.toString()));
		//remove 2 cards from a 1 card collection.
		collectionException3();
		collectionException4();
		
		//test moving cards.
		col3.add(c1);
		col3.add(c2);
		col3.add(c1);
		col3.add(c4);
		col3.move(0, 3);
		assertTrue(col3.toString().equals("Collection(" + c2 + ", " + c1 + ", " + c4 + ", " + c1 +")"));
		col3.move(2, 2);
		assertTrue(col3.toString().equals("Collection(" + c2 + ", " + c1 + ", " + c4 + ", " + c1 +")"));
		collectionException5();
		//test swapping cards.
		col3.swap(0, 3);
		assertTrue(col3.toString().equals("Collection(" + c1 + ", " + c1 + ", " + c4 + ", " + c2 +")"));
		col3.swap(2, 2);
		assertTrue(col3.toString().equals("Collection(" + c1 + ", " + c1 + ", " + c4 + ", " + c2 +")"));
		collectionException6();
	}
	
	public void testHand()
	{
		h1.add(c1);
		h1.add(c2);
		h1.add(c3);
		
		//play a card at 0 and make sure it is c1.
		assertTrue(h1.play(0).toString().equals(c1.toString()));
		//play a card at 1 and make sure it is c3.
		assertTrue(h1.play(1).toString().equals(c3.toString()));
		
		h1.add(c1);
		h1.add(c3);
		
		{
			try
			{
				h1.play(8);
				assertTrue(false);
			}
			catch (IllegalArgumentException err)
			{
				assertTrue(true);
			}
		}
		
	}
	
	public void testDeck()
	{
		d1.add(c1);
		d1.add(c2);
		d1.add(c3);
		
		//test the draw method
		assertTrue(d1.draw().toString().equals(c3.toString()));
		assertTrue(d1.draw().toString().equals(c2.toString()));
		
		//make sure you cannot add extra card.
		deckException1();
	}
	
	private void collectionException1()
	{
		try
		{
			col2.add(c3);
			assertTrue(false);
		}
		catch (ArrayIndexOutOfBoundsException err)
		{
			assertTrue(true);
		}
	}
	
	private void collectionException2()
	{
		try
		{
			col3.add(null);
			assertTrue(false);
		}
		catch (NullPointerException err)
		{
			assertTrue(true);
		}
	}
	
	private void collectionException3()
	{
		try
		{
			col1.remove(0);
			col1.remove(0);
			assertTrue(false);
		}
		catch (ArrayIndexOutOfBoundsException err)
		{
			assertTrue(true);
		}
	}
	
	private void collectionException4()
	{
		try
		{
			col2.remove(10);
			assertTrue(false);
		}
		catch (IllegalArgumentException err)
		{
			assertTrue(true);
		}
	}
	
	private void collectionException5()
	{
		try
		{
			col3.move(2,17);
			assertTrue(false);
		}
		catch (IllegalArgumentException err)
		{
			assertTrue(true);
		}
	}
	
	private void collectionException6()
	{
		try
		{
			col2.swap(18,1);
			assertTrue(false);
		}
		catch (IllegalArgumentException err)
		{
			assertTrue(true);
		}
	}
	
	private void deckException1()
	{
		try
		{
			d2.add(c1);
			assertTrue(false);
		}
		catch (ArrayIndexOutOfBoundsException err)
		{
			assertTrue(true);
		}
	}
	
	private void setUpCardTesting()
	{
		//correct input.
		c1 = new Card(3,'c');
		c2 = new Card(5,'d');
		c3 = new Card(12,'h');
		c4 = new Card(14,'s');
		//incorrect input.
		c5 = new Card(15,'d');
		c6 = new Card(12,'z');
		c7 = new Card(13, 'S');
		//null card.
		c8 = Card.nullCard();
	}
	
	private void setUpCardCollectionTesting()
	{
		//correct input.
		col1 = new CardCollection(5);
		col2 = new CardCollection(2);
		col3 = new CardCollection(15);
		//incorrect input.
		col4 = new CardCollection(0);
		col5 = new CardCollection(-4);
	}
	
	private void setUpDeckandHandTesting()
	{
		h1 = new Hand(5);
		h2 = new Hand(0);
		d1 = new Deck(10);
		d2 = new Deck();
		d3 = new Deck(-5);
		d4 = new Deck();
		d5 = new Deck();
	}
}
