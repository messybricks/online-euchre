package game;

/**
 * Class to handle dealing with the passing of cards to each player.
 * 
 * @author rchurtub
 *
 */
public class CardDistributor {
	
	//deck of cards to be used in the euchre game.
	private Deck deck;
	//Reference to each player in the game, with respect to the current dealer.
	private Player player[];
	
	public static final int DEALER = 0;
	public static final int LEFT = 1;
	public static final int ACROSS = 2;
	public static final int RIGHT = 3;
	
	public CardDistributor(Player dealer, Player left, Player across, Player right)
	{
		//create the euchre deck.
		deck = new Deck();
		//set up the references to each player.
		player = new Player[4];
		player[0] = dealer;
		player[1] = left;
		player[2] = across;
		player[3] = right;
	}
	
	public CardDistributor(Player p[])
	{
		this(p[0],p[1],p[2],p[3]);
	}
	
	/**
	 * Deals out the round to each of the players, in a 2 then 3 pass style.
	 */
	public void dealRound()
	{
		//first way around the table.
		for (int i = 0; i < 2; ++i)
			player[1].pickupCard(deck.draw());
		for (int i = 0; i < 3; ++i)
			player[2].pickupCard(deck.draw());
		for (int i = 0; i < 2; ++i)
			player[3].pickupCard(deck.draw());
		for (int i = 0; i < 3; ++i)
			player[0].pickupCard(deck.draw());
		//second way around the table.
		for (int i = 0; i < 3; ++i)
			player[1].pickupCard(deck.draw());
		for (int i = 0; i < 2; ++i)
			player[2].pickupCard(deck.draw());
		for (int i = 0; i < 3; ++i)
			player[3].pickupCard(deck.draw());
		for (int i = 0; i < 2; ++i)
			player[0].pickupCard(deck.draw());
	}
	
	/**
	 * Draws one card from the top of the deck to be bidding on for trump.
	 * 
	 * @return A card to be bidding on for trump.
	 */
	public Card flipTrump()
	{
		return deck.draw();
	}
	
	/**
	 * needs to be implemented
	 * 
	 * @param trump 
	 */
	public void dealerDiscard(Card trump)
	{
		
	}
	
	/**
	 * Swaps around the dealer and respective players.
	 */
	public void nextRound()
	{
		//rotate the dealer and respective players.
		Player temp = player[3];
		
		for (int i = 3; i > 0; --i)
			player[i] = player[i-1];
		player[0] = temp;
	}
	
	/**
	 * Gets an array containing the order of the players with respect to the dealer.
	 *   index DEALER = dealer
	 *   index LEFT = left of dealer
	 *   index ACROSS = across dealer
	 *   index RIGHT = right of dealer
	 *   
	 * @return An array of player position with respect to the dealer.
	 */
	public Player[] getPlayerOrder()
	{
		return player;
	}

}
