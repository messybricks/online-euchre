package game;

/**
 * This class acts as a card in a deck or hand.
 * 
 * @author Ryan
 */
public class Card 
{

	//suit of the card.
	private char c;
	//int value/number of the card.
	private int v;

	/**
	 * Constructor for creating a new card. Creates null card if invalid parameters.
	 * 
	 * @param suit - suit of the card.
	 * @param value - int value of the card, must fall between 1 and 14 to be vaild. 
	 */
	public Card(char suit, int value) 
	{
		suit = Character.toLowerCase(suit);
		
		//is the value correct.
		if (value < 1 || value > 14)
			v = 0;
		else
			v = value;
		
		//is the suit correct.
		if (suit == 'h' || suit == 'd' || suit == 's' || suit == 'c')
			c = suit;
		else
		{
			c = 'd';
			v = 0;
		}
	}
	
	/**
	 * Constructor for creating a new card. Creates null card if invalid parameters.
	 * 
	 * @param value - int value of the card.
	 * @param suit - suit of the card. 
	 */
	public Card(int value, char suit)
	{
		this(suit,value);
	}

	/**
	 * Returns the char suit of the card. 'd' for diamonds, 'h','c' and 's'.
	 * 
	 * @return Character suit of the card.
	 */
	public char getSuit() 
	{
		return c;
	}

	/**
	 * Returns the int value of the card. 11 - 14 are face cards.
	 * 
	 * @return Integer value of the card.
	 */
	public int getValue() 
	{
		return v;
	}

	/**
	 * Creates a null card, to be used as a place holder.
	 * 
	 * @return Token null card.
	 */
	public static Card nullCard() 
	{
		return new Card('d',0);
	}

	/**
	 * Checks if a given card is a null/place holder card, not null inself.
	 * 
	 * @param c - Card to be tested.
	 * @return True if c.getValue() = 0, false otherwise.
	 * @throws Exception 
	 */
	public static boolean ifNull(Card c)
	{
		return c.getValue() == 0;
	}

	/**
	 * Returns the string representation of the current card.
	 * 
	 * @return String rep. of the current card.
	 */
	public String toString() 
	{
		String str = "";
		//Get the correct face card associated with value.
		switch(v) {
		case 1 : str = "Ace"; break;
		case 11: str = "Jack"; break;
		case 12: str = "Queen"; break;
		case 13: str = "King"; break;
		case 14: str = "Ace"; break;
		default: str = v + "";
		}

		str += " of ";

		//Get the correct symbol of the card.
		switch(c) {
		case 'h': str += "Hearts"; break;
		case 'd': str += "Diamonds"; break;
		case 's': str += "Spades"; break;
		case 'c': str += "Clubs"; break;
		}
		
		//is the card null;
		if (v == 0)
			str = "Null card";

		return str;
	}	
}
