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

	/**
	 * starts the euchre engine state machine
	 */
	public void start()
	{
		state = START;
		Trace.dprint("new state: " + state);
		
		//TODO: implement start state
		
		deal();		
	}
	
	/**
	 * deals a hand
	 */
	private void deal()
	{
		state = DEAL;
		Trace.dprint("new state: " + state);
		
		//TODO: implement deal state
		
		//TODO: replace this with player left of the dealer
		Player p = new Player("dummy");
		bid(p);
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
		
		//TODO: ask user to accept/name trump
		//TODO: if the user passes, call bid() on the next player
		
		if(state <= FOURTH_BID)
			dealerDiscard();
		else
			goingAlone();
	}
	
	/**
	 * the dealer chooses and discards one card
	 */
	private void dealerDiscard()
	{
		state = DEALER_DISCARD;
		Trace.dprint("new state: " + state);
		
		//TODO: ask dealer to choose a card to discard
		
		goingAlone();
	}
	
	/**
	 * if the player who named trump is going alone
	 */
	private void goingAlone()
	{
		state = GOING_ALONE;
		Trace.dprint("new state: " + state);
		
		//TODO: ask the player who named trump if he/she is going alone
		goingAlone = false;
		
		//TODO: replace this with the leading player
		Player p = new Player("dummy");
		throwCard(p);		
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
			Player dummy = new Player("dummy");
			throwCard(dummy);
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
