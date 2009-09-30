package game;

/**
 * Class to simulate a player's hand in a card game.
 * 
 * @author Ryan
 *
 */
public class Hand extends CardCollection {

	/**
	 * Creates a hand for a player of size s.
	 * 
	 * @param s - Max size of the hand.
	 */
	public Hand(int s) {
		super(s);
	}
	
	/**
	 * Play a card from the hand.
	 * 
	 * @param i - Index of card to be played.
	 * @return Card to be played from the hand.
	 */
	public Card play(int i) {
		return remove(i);
	}
	
}