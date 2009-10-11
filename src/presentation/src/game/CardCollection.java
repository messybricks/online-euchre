package game;

/**
 * This class provides the standard methods and data needed to hold a collection of cards, such as a deck of cards.
 * 
 * @author Ryan
 */
public class CardCollection 
{

	//cards in the collection.
	private Card[] cards;
	//number of cards currently in the collection.
	protected int count;

	/**
	 * Creates a new collection of cards with max size of size.
	 * 
	 * @param size - max size of the collection.
	 */
	public CardCollection(int size) 
	{
		cards = new Card[size];
		count = 0;
	}

	/**
	 * Adds a new card to the end of the card collection.
	 * 
	 * @param c - card to be added.
	 */
	public void add(Card c) 
	{
		if (count == cards.length)
			throw new ArrayIndexOutOfBoundsException("Error: The card collection is already full.");
		//set new card to end and increment card counter.
		cards[count++] = c;
	}

	/**
	 * Removes a card at given index from the collection, and squashes the array together.
	 * 
	 * @param index - index of card to be removed.
	 * @return The card that is to be removed.
	 */
	public Card remove(int index) 
	{
		if (count == 0)
			throw new ArrayIndexOutOfBoundsException("Error: The card collection is already empty.");
		//save the card to be removed.
		Card temp = cards[index];
		//move every card after the removed card down one slot.
		for (int i = index; i < cards.length-1; ++i) {
			cards[i] = cards[i+1];
		}
		//make the end card the null card.
		cards[cards.length-1] = Card.nullCard();
		//decrement the card counter.
		--count;
		//return the removed card.
		return temp;
	}

	/**
	 * Swap two cards in the card collection.
	 * 
	 * @param a - index of one of the cards to be swapped.
	 * @param b - index of another of the cards to be swapped.
	 */
	public void swap(int a, int b) 
	{
		//save card a.
		Card temp = cards[a];
		//swap b into a.
		cards[a] = cards[b];
		//swap saved a into b.
		cards[b] = temp;
	}

	/**
	 * Returns the number of cards in the collection.
	 * 
	 * @return The number of cards in the collection.
	 */
	public int getNumberOfCards() 
	{
		return count;
	}

	/**
	 * Returns the string representation of the current card collection.
	 * 
	 * @return The string rep. of the current collection.
	 */
	public String toString() 
	{
		String str = "Collection(";
		//run through each card in the collection.
		for (int i = 0; i < count; ++i) {
			str += cards[i];
			if (i != count-1)
				str += ", ";
		}
		str += ")";
		return str;
	}
}
