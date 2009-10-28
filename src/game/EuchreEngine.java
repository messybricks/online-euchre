package game;

import utility.Trace;

/**
 * a state machine that runs the euchre game
 * 
 * @author mvmesenb
 */
public class EuchreEngine 
{
	//the state of this machine
	private int state;
	
	//possible states
	public static final int START = 0;
	public static final int DEAL = 1;
	public static final int FIRST_BID = 2;
	public static final int SECOND_BID = 3;
	public static final int THIRD_BID = 4;
	public static final int FOURTH_BID = 5;
	public static final int FIFTH_BID = 6;
	public static final int SIXTH_BID = 7;
	public static final int SEVENTH_BID = 8;
	public static final int EIGHTH_BID = 9;
	public static final int DEALER_DISCARD = 10;
	public static final int GOING_ALONE = 11;
	public static final int FIRST_PLAYER_THROWS_CARD = 12;
	public static final int SECOND_PLAYER_THROWS_CARD = 13;
	public static final int THIRD_PLAYER_THROWS_CARD = 14;
	public static final int FOURTH_PLAYER_THROWS_CARD = 15;
	public static final int END_OF_TRICK = 16;
	public static final int END_OF_ROUND = 17;
	
	private boolean goingAlone = false;
	private CardDistributor cardDistributor;
	private int currentPlayer;
	private Card trump;
	
	public EuchreEngine(Player dealer, Player left, Player across, Player right)
	{
		cardDistributor = new CardDistributor(dealer, left, across, right);
	}
	
	
	/**
	 * starts the euchre engine state machine
	 */
	public void start()
	{
		state = START;
		Trace.dprint("new state: " + state);
		
		//TODO: implement start state
		//TODO: get four players, put them into player array
		
		
		
		deal();		
	}
	
	/**
	 * deals a hand
	 */
	private void deal()
	{
		//rotate the dealer if this is not the first time
		if(state != START)
			cardDistributor.nextRound();
		
		state = DEAL;
		Trace.dprint("new state: " + state);
		
		//deal this hand
		cardDistributor.dealRound();
		trump = cardDistributor.flipTrump();
		
		//player to the left of the dealer bids
		currentPlayer = CardDistributor.LEFT;
		bid(cardDistributor.getPlayerOrder()[currentPlayer]);
	}
	
	/**
	 * gives a player the chance to accept/name trump
	 * 
	 * @param p the player whose turn it is 
	 */
	private void bid(Player p)
	{
		//set the state, or re-deal if there have been eight bids already
		if(state < EIGHTH_BID)
			state++;
		else
			deal();
		
		Trace.dprint("new state: " + state);
		
		if(state == FIFTH_BID)
		{
			//TODO: flip down trump card
		}
		
		//TODO: ask player to accept/name trump
	}
	
	public void receiveBid(Character trump)
	{
		//if (player accepted/named trump)
		if (trump != 'p')
		{
			if(state <= FOURTH_BID)
			{
				state = DEALER_DISCARD;
				Trace.dprint("new state: " + state);
			}
			else
				goingAlone();
		}
		else  // if the player passed, go on to the next player
		{
			currentPlayer = (currentPlayer++) % 4;
			bid(cardDistributor.getPlayerOrder()[currentPlayer]);
		}
	}
	
	/**
	 * the dealer chooses and discards one card
	 */
	private void dealerDiscard()
	{
		
		//ask dealer to choose a card to discard
		cardDistributor.dealerDiscard(trump);
		
		goingAlone();
	}
	
	/**
	 * if the player who named trump is going alone
	 */
	private void goingAlone()
	{
		state = GOING_ALONE;
		Trace.dprint("new state: " + state);
		
		//TODO: ask currentPlayer if he/she is going alone
		goingAlone = false;
		
		throwCard(cardDistributor.getPlayerOrder()[cardDistributor.LEFT]);		
	}
	
	/**
	 * the player whose turn it is chooses a card to throw
	 * 
	 * @param p the player whose turn it is
	 */
	private void throwCard(Player p)
	{
		// if someone still needs to throw a card
		if(state < THIRD_PLAYER_THROWS_CARD || (state < FOURTH_PLAYER_THROWS_CARD && !goingAlone))
			state++;
		else // if the last player has thrown a card
		{
			//TODO: replace p with the player that won the trick
			endOfTrick(p);
		}
		
		Trace.dprint("new state: " + state);
		
		
		//TODO: replace p with "next" player
		throwCard(p);
	}
	
	/**
	 * score points appropriately at the end of a trick.  If there are cards left, play another trick.
	 * Otherwise, end the round.
	 * 
	 * @param winner the player that won the trick
	 */
	private void endOfTrick(Player winner)
	{
		state = END_OF_TRICK;
		Trace.dprint("new state: " + state);
		
		//TODO: score points appropriately
		
		//TODO: implement this if/else structure:
		//if there are cards left, play another trick
			//Player dummy = new Player("dummy");
			//throwCard(dummy);
		//otherwise, end the round
			endOfRound();
	}
	
	/**
	 * go here at the end of a trick when there are no cards left.  If the game is not over, deal a 
	 * new hand.  Otherwise, exit. 
	 */
	private void endOfRound()
	{ 
		state = END_OF_ROUND;
		Trace.dprint("new state: " + state);
		
		//TODO: if a team has won the game, 
			// display "You won!", options for playing a new game, etc.
			System.exit(1);
		//TODO: else, deal a new hand:
			//deal();
	}
	
	/**
	 * returns the state of this state machine
	 * 
	 * @return the state of this state machine
	 */
	public int getState()
	{
		return state;
	}
		
}
