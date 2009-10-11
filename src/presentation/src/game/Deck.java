package game;

/**
 * A CardCollection that simulates a deck of cards, and is primarily a Euchre deck.
 * 
 * @author Ryan
 *
 */
public class Deck extends CardCollection 
{

	/**
	 * Creates a deck of playing cards, which size s.
	 * 
	 * @param s - Max size of the deck.
	 */
	public Deck(int s) 
	{
		super(s);
	}

	/**
	 * Randomly shuffles up the deck of card.
	 */
	public void shuffle() 
	{
		//lottery type shuffle algorithm.
		int[] temp = new int[count];
		int[] temp2 = new int[count];
		//fill an array with sequence for 0 to count-1.
		for (int i = 0; i < count; ++i)
		{
			temp[i] = i;
			temp2[i] = i;
		}
		//randomize the array of values (with no duplicate values).
		int rand, old;
		//run through and randomly jumble the int. arrays.
		for (int i = 0; i < count; ++i)
		{
			rand = (int)(Math.random()*count);
			//swap rand value with old value.
			old = temp[i];
			temp[i] = temp[rand];
			temp[rand] = old;
			rand = (int)(Math.random()*count);
			//swap rand value with old value.
			old = temp2[i];
			temp2[i] = temp2[rand];
			temp2[rand] = old;
		}
		//run through the array and swap cards around with random array.
		for (int i = 0; i < count; ++i)
		{
			swap(temp[i],temp2[i]);
		}
	}

	/**
	 * Draws a card from the top of the deck.
	 * 
	 * @return The card to be drawn.
	 */
	public Card draw() 
	{
		//remove the last card and return.
		return remove(count-1);
	}

	/**
	 * Turns the current deck into a standard euchre deck.
	 */
	public void createStandardEuchreDeck() 
	{
		//reset to 0 deck.
		count = 0;
		//add all cards for 9, 10, J, Q, K, A.
		for (int v = 9; v < 15; ++v) {
			add(new Card('h',v));
			add(new Card('d',v));
			add(new Card('s',v));
			add(new Card('c',v));
		}
		//shuffle up the new deck.
		shuffle();
	}
}
