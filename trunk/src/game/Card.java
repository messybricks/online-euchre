package game;

/**
 * This class acts as a card in a deck or hand.
 * 
 * @author Ryan
 *
 */
public class Card {
	
	//suit of the card.
	private char c;
	//int value/number of the card.
	private int v;
	
	/**
	 * Constructor for creating a new card.
	 * 
	 * @param suit - suit of the card.
	 * @param value - int value of the card. 
	 */
	public Card(char suit, int value) {
		c = suit;
		v = value;
	}
	
	/**
	 * Returns the char suit of the card. 'd' for diamonds, 'h','c' and 's'.
	 * @return Character suit of the card.
	 */
	public char getSuit() {
		return c;
	}
	
	/**
	 * Returns the int value of the card. 11 - 14 are face cards.
	 * @return Integer value of the card.
	 */
	public int getValue() {
		return v;
	}
	
	/**
	 * Creates a null card, to be used as a place holder.
	 * @return Token null card.
	 */
	public static Card nullCard() {
		return new Card('d',0);
	}
	
	/**
	 * Checks if a given card is a null/place holder card.
	 * @param c - Card to be tested.
	 * @return True if c.getValue() = 0, false otherwise.
	 */
	public static boolean ifNull(Card c) {
		return c.getValue() == 0;
	}
	
	/**
	 * Returns the string representation of the current card.
	 * @return String rep. of the current card.
	 */
	public String toString() {
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
		
		return str;
	}
	
}
