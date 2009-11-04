package game;

import java.io.Serializable;

import chat.User;
import testing.FakeThread;
import utility.Opcode;
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
	private int currentPlayerIndex;
	private int notPlaying;
	private Card trumpCard;
	private Card[] trick;
	private char trump;

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
		trumpCard = cardDistributor.flipTrump();

		//player to the left of the dealer bids
		currentPlayerIndex = CardDistributor.LEFT;
		bid(currentPlayer());
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

		//send opcode to player to request a bid
		currentPlayer().sendData(Opcode.requestBid, "" + trumpCard.getSuit());
	}

	/**
	 * receive input from the player:
	 * "s" = spades is named as trump
	 * "d" = diamonds is named as trump
	 * "h" = hearts is named as trump
	 * "c" = clubs is named as trump
	 * "p" = pass (nothing named as trump yet)
	 * 
	 * @param t the first letter of the suit named as trump (or "p" for pass)
	 */
	public void receiveBid(String t)
	{
		//if (player accepted/named trump)
		if (t != "p")
		{
			trump = t.charAt(0);
			if(state <= FOURTH_BID)
			{
				dealerDiscard();				
			}
			else
				goingAlone();
		}
		else  // if the player passed, go on to the next player
		{
			currentPlayerIndex = (currentPlayerIndex + 1) % 4;
			bid(currentPlayer());
		}
	}

	/**
	 * the dealer chooses and discards one card
	 */
	private void dealerDiscard()
	{
		state = DEALER_DISCARD;
		Trace.dprint("new state: " + state);

		//send an opcode to the dealer to tell them to discard
		cardDistributor.getPlayerOrder()[0].sendOpcode(Opcode.dealerDiscard);
	}

	/**
	 * this method is called after the bidding is done
	 * and after the player who is the dealer discards
	 * a card (if applicable).  In this state, the
	 * player who accepted/named trump will be given
	 * the chance to "go alone".
	 */
	public void goingAlone()
	{
		state = GOING_ALONE;
		Trace.dprint("new state: " + state);

		//ask currentPlayer if he/she is going alone
		currentPlayer().sendOpcode(Opcode.goingAlone);
	}

	/**
	 * sets the value of goingAlone (this happens after
	 * the user has been prompted)
	 * 
	 * @param alone true if the player is going alone, false otherwise 
	 */
	public void setGoingAlone(boolean alone)
	{
		goingAlone = alone;
		throwCard();

		if(alone)
			notPlaying = (currentPlayerIndex + 2) % 4;
		else
			notPlaying = -1;

		trick = new Card[4];
	}



	/**
	 * this method is called every time a player needs to
	 * choose and throw a card for a trick.
	 */
	private void throwCard()
	{
		//if someone still needs to throw a card
		if(state < THIRD_PLAYER_THROWS_CARD || (state < FOURTH_PLAYER_THROWS_CARD && !goingAlone))
		{
			state++;
			Trace.dprint("new state: player " + (state - FIRST_PLAYER_THROWS_CARD) + " throws card");

			//decide whose turn it is to throw a card
			if(state == FIRST_PLAYER_THROWS_CARD)
				currentPlayerIndex = CardDistributor.LEFT;
			else
				currentPlayerIndex = (currentPlayerIndex + 1) % 4;

			//if this player's partner is going alone, skip them
			if(currentPlayerIndex == notPlaying)
				receiveCard(Card.nullCard());

			//ask current player to choose and throw a card
			currentPlayer().sendOpcode(Opcode.throwCard);

		}
		else //if the last player has thrown a card
		{
			endOfTrick(winnerOfTrick());
		}
	}

	/**
	 * receives the card that was thrown by the current player
	 * 
	 * @param thrown the card that was thrown
	 */
	public void receiveCard(Card thrown)
	{
		Trace.dprint("received card: " + thrown.toString());

		//add the card to the current trick
		int numberOfCardsThrown = state - FIRST_PLAYER_THROWS_CARD;
		trick[numberOfCardsThrown] = thrown;

		//throw another card
		throwCard();
	}

	/**
	 * returns the player who won this trick
	 * 
	 * @return the player who won this trick
	 */
	private Player winnerOfTrick()
	{
		int winningCardIndex = 0;
		for(int i = 1; i < trick.length; i++)			 
			if(trick[i].getValue() > 0 &&											//if this card is not null and
					(trick[i].getSuit() == trump || 								//(this card is trump or
							trick[i].getSuit() == trick[winningCardIndex].getSuit())		//the same suit as the winning card)
							&& trick[i].getValue() > trick[winningCardIndex].getValue())	//and it's higher than the winning card
				winningCardIndex = i;											//		then this is the new winning card

		Trace.dprint("winning card: " + trick[winningCardIndex]);

		return cardDistributor.getPlayerOrder()[winningCardIndex];
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
		Trace.dprint("player " + winner.toString() + "has won the trick.");

		//TODO: increment the score of tricks for the winning team

		//TODO: check to see if there are cards left, then play another trick or end the round
		boolean cardsLeft = false;
		if(cardsLeft)
		{
			state = FIRST_PLAYER_THROWS_CARD;
			trick = new Card[4];
			throwCard();
		}
		else
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

		//TODO: score points appropriately

		//TODO: if a team has won the game, 
		boolean gameIsOver = true;
		if(gameIsOver)
		{
			//TODO: display "You won!", options for playing a new game, etc.
		}
		else
			deal();
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

	/**
	 * returns a char containing the first letter of the suit that is currently trump
	 * 
	 * @return a char containing the first letter of the suit that is currently trump
	 */
	public char getTrump()
	{
		return trump;
	}

	/**
	 * helper method that returns the current player, using the currentPlayerIndex.
	 * 
	 * @return the current player
	 */
	private Player currentPlayer()
	{
		return cardDistributor.getPlayerOrder()[currentPlayerIndex];
	}

	/**
	 * Receives forwarded packets from PacketQueueThread for processing/state updating.
	 * 
	 * @param player The player that sent the packet
	 * @param opcode The opcode associated with the packet
	 * @param data The data associated with the packet
	 */
	public void forwardPacket(Player player, Opcode opcode, Serializable data)
	{
		if(opcode == Opcode.requestBid)
			receiveBid((String)data);
		else if(opcode == Opcode.requestAlternateBid)
			receiveBid((String)data);
		else if(opcode == Opcode.goingAlone)
			setGoingAlone((Boolean) data);
		else if(opcode == Opcode.dealerDiscard)
			goingAlone();
		else
			Trace.dprint("EuchreEngine received forwarded packet from player '%s' with unimplemented opcode '%s' - ignoring.", player.getUsername(), opcode.toString());
	}
}

