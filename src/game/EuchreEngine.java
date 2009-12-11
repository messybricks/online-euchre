package game;

import java.io.Serializable;

import chat.ChatObject;
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
	private int leadingPlayerIndex = 0;
	private int notPlaying;
	private Card trumpCard;
	private Card[] trick;
	private char trump;
	private int teamThatNamedTrump;

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
		displayCard(trumpCard,cardDistributor.getPlayerOrder()[CardDistributor.DEALER].getPID(),false);


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
			//send an opcode to each player to show that the trump card is flipped down
			for(int i = 0; i<4; i++)
				cardDistributor.getPlayerOrder()[i].sendData(Opcode.displayCard, new CardWrapper(Card.nullCard(), cardDistributor.getPlayerOrder()[CardDistributor.DEALER].getPID(),false));
		}

		//send opcode to player to request a bid
		if(state >= FIFTH_BID)
			currentPlayer().sendData(Opcode.requestAlternateBid, "" + trumpCard.getSuit());
		else
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
		if (!t.equals("p"))
		{
			trump = t.charAt(0);
			teamThatNamedTrump = currentPlayer().getPID() % 2;
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

		// display the new trump icon in the middle of each client's screen
		for (int i = 0; i < 4; i++)
			cardDistributor.getPlayerOrder()[i].sendData(Opcode.displayTrump, new Character(trump));

		// clear bid card if it's there
		displayCard(Card.nullCard(), cardDistributor.getPlayerOrder()[CardDistributor.DEALER].getPID(),false);

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
		currentPlayerIndex = CardDistributor.LEFT;
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
		if(state < FOURTH_PLAYER_THROWS_CARD)
		{
			state++;
			if(state == FIRST_PLAYER_THROWS_CARD)
				leadingPlayerIndex = currentPlayerIndex;
			Trace.dprint("new state: player " + (state - FIRST_PLAYER_THROWS_CARD) + " throws card");
			Trace.dprint("current player index: " + currentPlayerIndex);
			
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
		if(thrown.equals(Card.nullCard()))
			displayGameMessage(null, currentPlayer().getUsername() + " was skipped.");
		else
			displayGameMessage(null, currentPlayer().getUsername() + " has played the " + thrown.toString());

		//add the card to the current trick
		//int numberOfCardsThrown = state - FIRST_PLAYER_THROWS_CARD;
		trick[currentPlayerIndex] = thrown;
		if(!thrown.equals(Card.nullCard()))//this skips anyone not playing
			if(state==FIRST_PLAYER_THROWS_CARD)
				displayCard(thrown,cardDistributor.getPlayerOrder()[currentPlayerIndex].getPID(),true);
			else
				displayCard(thrown,cardDistributor.getPlayerOrder()[currentPlayerIndex].getPID(),false);

		//throw another card
		currentPlayerIndex = (currentPlayerIndex + 1) % 4;
		throwCard();
	}

	/**
	 * returns the player who won this trick
	 * 
	 * @return the player who won this trick
	 */
	private Player winnerOfTrick()
	{
		//set the number of cards in this trick
		int numberOfCards = trick.length;		
		if(goingAlone)
			numberOfCards--;
		
		//set the suit of the left jack
		char leftSuit = 'c';		
		if(trump == 'c')
			leftSuit = 's';
		else if(trump == 'h')
			leftSuit = 'd';
		else if(trump == 'd')
			leftSuit = 'h';
		
		int winningCardIndex = leadingPlayerIndex;		//initial index of "winning card" defaults to the leading card

		//for each card in the trick
		for(int i = 0; i < numberOfCards; i++)			 						
		{
			if(trick[winningCardIndex].getSuit() == leftSuit && trick[winningCardIndex].getValue() == 11)
			{
				//do nothing here if the winning card is the left
			}
			//if this card is trump and the winning card is not
			else if(trick[i].getSuit() == trump && trick[winningCardIndex].getSuit() != trump)
				winningCardIndex = i;  			
			//else if this card is the same suit as the current winning card and has a higher value
			else if(trick[i].getSuit() == trick[winningCardIndex].getSuit() && trick[i].getValue() > trick[winningCardIndex].getValue())
				winningCardIndex = i;
			
			//if this card is the right Jack
			if(trick[i].getSuit() == trump && trick[i].getValue() == 11)
			{
				winningCardIndex = i;
				break;
			}
			
			//if this card is the left Jack
			if(trick[i].getSuit() == leftSuit && trick[i].getValue() == 11)
				winningCardIndex = i;
		}

		Trace.dprintArray(trick);
		Trace.dprint("winning card: " + trick[winningCardIndex]);
		Trace.dprint("trump: " + trump);

		//set the current player to the winner of the trick
		currentPlayerIndex = winningCardIndex;
		
		
		return currentPlayer();
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
		displayGameMessage(null, winner.getUsername() + " has won the trick.");
		displayCard(Card.nullCard(),0,false);//tell clients to clear the screen

		//increment the score of tricks for the winning team
		winner.winTrick();

		boolean cardsLeft = false;
		for(int i = 0; i < winner.getCards().length; i++)
		{
			if(winner.getCards()[i] != null && !winner.getCards()[i].equals(Card.nullCard()))
				cardsLeft = true;
		}

		//if there are cards left in the current player's hand
		if(cardsLeft)
		{
			state = GOING_ALONE; // not actually asking if going alone, but throwCard requires the machine to start in this state to work properly.
			trick = new Card[4];
			
			//currentPlayerIndex = (currentPlayerIndex + 1) % 4;	
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
			
		//change current player to one of the players on the team that named trump
		if(currentPlayer().getPID() % 2 != teamThatNamedTrump)
			currentPlayerIndex = (currentPlayerIndex + 1) % 4;

		Trace.dprint(currentPlayer().getUsername() + "'s team named trump.");
		
		//the number of tricks won by current player's team
		int teamTricksWon = currentPlayer().getTricksWon() + cardDistributor.getPlayerOrder()[(currentPlayerIndex + 2) % 4].getTricksWon();
		
		//if current player's team lost the round
		if(teamTricksWon <= 2)
		{
			Trace.dprint(currentPlayer().getUsername() + "'s team won " + teamTricksWon + " tricks, so the other team gets 2 points");
			
			//other team gets two points
			cardDistributor.getPlayerOrder()[(currentPlayerIndex + 1) % 4].incrementScore(2);
			cardDistributor.getPlayerOrder()[(currentPlayerIndex + 3) % 4].incrementScore(2);
		}
		else if(teamTricksWon == 3 || currentPlayer().getTricksWon() == 4)
		{
			Trace.dprint(currentPlayer().getUsername() + "'s team won " + teamTricksWon + " tricks.  They get 1 point.");
			currentPlayer().incrementScore(1);
			cardDistributor.getPlayerOrder()[(currentPlayerIndex + 2) % 4].incrementScore(1);
		}
		else if(teamTricksWon == 5 && !goingAlone)
		{
			Trace.dprint(currentPlayer().getUsername() + "'s team won 5 tricks.  They get 2 points.");
			currentPlayer().incrementScore(2);
			cardDistributor.getPlayerOrder()[(currentPlayerIndex + 2) % 4].incrementScore(2);
		}
		else if(teamTricksWon == 5 && goingAlone)
		{
			Trace.dprint(currentPlayer().getUsername() + "'s team won 5 tricks and went alone.  They get 4 points.");
			currentPlayer().incrementScore(4);
			cardDistributor.getPlayerOrder()[(currentPlayerIndex + 2) % 4].incrementScore(4);
		}

		
		
		//Send scores to all the players
		for(int i = 0; i < 4; i++)
		{
			currentPlayerIndex = (currentPlayerIndex + 1) % 4;
			displayGameMessage(currentPlayer(), "You won " + currentPlayer().getTricksWon() + " tricks.");
			currentPlayer().resetTricksWon();
			displayGameMessage(currentPlayer(), "---------------------");
			for(int j = 0; j < 4; j++)
				displayGameMessage(currentPlayer(), "" + cardDistributor.getPlayerOrder()[j].getUsername() + "'s points: " + cardDistributor.getPlayerOrder()[j].getScore());
			displayGameMessage(currentPlayer(), "---------------------");
		}
		
		//CHEAT CODE: user name G0D wins the game after the first round.
		for(int i = 0; i < 4; i++)
		{
			if(cardDistributor.getPlayerOrder()[i].getUsername().equals("G0D"))
				cardDistributor.getPlayerOrder()[i].incrementScore(10);
		}
		
		//determine if someone has won the game
		if(currentPlayer().getScore() >= 10)
		{
			win(currentPlayerIndex);
		}
		else if(cardDistributor.getPlayerOrder()[(currentPlayerIndex + 1) % 4].getScore() >=10)
		{
			win((currentPlayerIndex + 1) % 4);
		}
		else
			deal();
	}

	/**
	 * Displays a terminal message in the chat window of the given player (or to all players if player is null) 
	 * 
	 * @param player the player to which the message will be sent (null = all players)
	 * @param message the message to be sent
	 */
	private void displayGameMessage(Player player, String message)
	{
		//send message to all players
		if (player == null)
		{
			for(int i = 0; i < 4; i++)
			{
				currentPlayerIndex = (currentPlayerIndex + 1) % 4;
				currentPlayer().sendData(Opcode.SendMessage, new ChatObject(null, null, message));
			}				
		}
		else
		{
			player.sendData(Opcode.SendMessage, new ChatObject(null, null, message));
		}
	}
	
	/**
	 * sends an opcode to each player indicating true if their team won, false otherwise
	 * 
	 * @param index the index of a player on the winning team
	 */
	private void win(int index)
	{
		cardDistributor.getPlayerOrder()[index].sendData(Opcode.endGame, new Boolean(true));
		cardDistributor.getPlayerOrder()[(index + 2) % 4].sendData(Opcode.endGame, new Boolean(true));
		cardDistributor.getPlayerOrder()[(index + 1) % 4].sendData(Opcode.endGame, new Boolean(false));
		cardDistributor.getPlayerOrder()[(index + 3) % 4].sendData(Opcode.endGame, new Boolean(false));
	}

	/**
	 * sends an opcode to each player to display a card on the screen
	 * 
	 * @param card the card to be displayed
	 * @param player the player who threw the card, -1 if it is for the kitty
	 */
	private void displayCard(Card card,int player,boolean first)
	{
		for (int i = 0; i < 4; i++)
		{
			cardDistributor.getPlayerOrder()[i].sendData(Opcode.displayCard, new CardWrapper(card,player,first));
		}
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

