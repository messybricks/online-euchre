package game;

import chat.User;

/**
 * Acts as a Player in the game of Euchre.
 * 
 * @author rchurtub
 *
 */
public class Player {

	private Hand hand;
	private User user;
	private int roundsWon = 0;
	private int gamesWon = 0;
	
	/**
	 * Creates a new player.
	 * 
	 * @param user - User associated with the player object.
	 */
	public Player(User user)
	{
		this.user = user;
		hand = new Hand(5);
	}
	
	/**
	 * Adds a new card to the end of the Players hand.
	 * 
	 * @param c - card to be added.
	 */
	public Card pickupCard(Card c)
	{
		hand.add(c);
		return c;
	}
	
	/**
	 * Plays a card at given index from the Players hand.
	 * 
	 * @param index - index of card to be played.
	 * @return The card that is played.
	 */
	public Card playCard(int c)
	{
		Card card = hand.play(c);
		return card;
	}
	
	/**
	 * Move a card in the Players hand.
	 * 
	 * @param oldIndex - index of old position of card.
	 * @param newIndex - index of new position of card.
	 */
	public void moveCard(int oldIndex, int newIndex)
	{
		hand.move(oldIndex, newIndex);
	}
	
	/**
	 * Swap two cards in the Players hand.
	 * 
	 * @param card1 - index of one of the cards to be swapped.
	 * @param card2 - index of another of the cards to be swapped.
	 */
	public void swapCards(int card1, int card2)
	{
		hand.swap(card1, card2);
	}
	
	/**
	 * Returns the name of the current Player.
	 * 
	 * @return The name of the current Player.
	 */
	public String getUsername()
	{
		return user.getUsername();
	}
	
	/**
	 * Returns the string representation of the current Player.
	 * 
	 * @return The string rep. of the current collection.
	 */
	public String toString()
	{
		String str = "";
		
		str += getUsername() + ": " + hand;
		
		return str;
	}
}
