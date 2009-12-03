package game;

import java.io.Serializable;

import utility.*;
import chat.User;

/**
 * Acts as a Player in the game of Euchre.
 * 
 * @author rchurtub
 *
 */
public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private User user;
	private SubPlayer subPlayer;
	
	private transient TransactionThread thread;
	private transient PlayerChangedCallback updatePlayer;
	
	private static int nextGuid = 1;
	
	/**
	 * Creates a new player.
	 * 
	 * @param user - User associated with the player object.
	 */
	public Player(User user, TransactionThread linkage)
	{
		this.user = user;
		this.subPlayer = new SubPlayer(nextGuid++);
		thread = linkage;
		
		createRemotePlayer(linkage);
	}
	
	/**
	 * Creates a new player.
	 * 
	 * @param user - User associated with the player object.
	 * @throws IllegalArgumentException if the remote packet is not a CreatePlayer packet.
	 */
	public Player(Packet remote, TransactionThread linkage)
	{
		if(remote.getOpcode() == Opcode.CreatePlayer)
		{
			PlayerInitializationVector iv = (PlayerInitializationVector)remote.getData();
			this.user = iv.getUser();
			this.subPlayer = iv.getSubPlayer();
			thread = linkage;
		}
		else
			throw new IllegalArgumentException(String.format("Cannot create a remote player instance using a Packet with opcode '%s'.", remote.getOpcode().toString()));
	}
	
	/**
	 * Sends a CreatePlayer packet to the given thread that will create a remote instance of this player on that client.
	 * 
	 * @param remote remote thread to create a new Player on
	 */
	public void createRemotePlayer(TransactionThread remote)
	{
		remote.send(Opcode.CreatePlayer, new PlayerInitializationVector(user, subPlayer));
	}
	
	/*
	public Player(String s)
	{
		throw new sun.reflect.generics.reflectiveObjects.NotImplementedException();
	}*/
	
	/**
	 * Adds a new card to the end of the Players subPlayer.getHand().
	 * 
	 * @param c - card to be added.
	 */
	public Card pickupCard(Card c)
	{
		subPlayer.getHand().add(c);
		transferData();
		return c;
	}
	
	/**
	 * Plays a card at given index from the Players subPlayer.getHand().
	 * 
	 * @param index - index of card to be played.
	 * @return The card that is played.
	 */
	public Card playCard(int c)
	{
		Card card = subPlayer.getHand().play(c);
		transferData();
		return card;
	}
	
	/**
	 * Move a card in the Players subPlayer.getHand().
	 * 
	 * @param oldIndex - index of old position of card.
	 * @param newIndex - index of new position of card.
	 */
	public void moveCard(int oldIndex, int newIndex)
	{
		subPlayer.getHand().move(oldIndex, newIndex);
		transferData();
	}
	
	/**
	 * Swap two cards in the Players subPlayer.getHand().
	 * 
	 * @param card1 - index of one of the cards to be swapped.
	 * @param card2 - index of another of the cards to be swapped.
	 */
	public void swapCards(int card1, int card2)
	{
		subPlayer.getHand().swap(card1, card2);
		transferData();
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
	
	public int getIndex(Card c)
	{
		return subPlayer.getHand().getIndex(c);
	}
	
	/**
	 * Returns the string representation of the current Player.
	 * 
	 * @return The string rep. of the current collection.
	 */
	public String toString()
	{
		String str = "";
		
		str += getUsername() + ": " + subPlayer.getHand();
		
		return str;
	}
	
	public Card[] getCards()
	{
		return subPlayer.getHand().getCards();
	}
	
	/**
	 * Returns an integer representing the number of cards in this hand.
	 * 
	 * @return an integer representing the number of cards in this hand.
	 * @see Hand.getNumberOfCards
	 */
	public int getCardCount()
	{
		return subPlayer.getHand().getNumberOfCards();
	}
		
	/**
	 * Gets this Player's globally unique identifier.
	 */
	public int getGuid()
	{
		return subPlayer.getGuid();
	}

	/**
	 * A private data transmission method for synchronizing data between the client and server.
	 */
	private void transferData()
	{
		if(thread == null)
			Trace.dprint("### Warning: transaction thread in player ID %s was null. Cannot synchronize player.", user.getUsername());
		else
			thread.send(Opcode.UpdatePlayer, subPlayer);
	}
	
	/**
	 * send this player an opcode
	 * 
	 * @param opcode the opcode to be sent to this player
	 */
	public void sendOpcode(Opcode opcode)
	{
		thread.send(opcode, new TargetedPackage(subPlayer.getGuid(), null));
	}

	
	public void setPID(int x)
	{
		subPlayer.team = x;
		transferData();
	}
	
	public int getPID()
	{
		return subPlayer.team;
	}
	
	/**
	 * send this player an opcode with associated data
	 * 
	 * @param opcode the opcode to be sent to this player
	 * @param datum data to associate with this opcode
	 */
	public void sendData(Opcode opcode, Serializable datum)
	{
		thread.send(opcode, new TargetedPackage(subPlayer.getGuid(), datum));
	}
	
	/**
	 * Updates the internal data associated with this Player using an UpdatePlayer packet.
	 * 
	 * @param packet UpdatePlayer packet containing new data for this Player
	 * @throws IllegalArgumentException if packet is not an UpdatePlayer packet
	 */
	public void updateData(Packet packet)
	{
		if(packet.getOpcode() == Opcode.UpdatePlayer)
		{
			SubPlayer updatedData = (SubPlayer)packet.getData();
			if(updatedData.getGuid() == subPlayer.getGuid())
			{
				subPlayer = updatedData;
				if(updatePlayer != null)
					updatePlayer.PlayerUpdated(this);
			}
		}
		else
			throw new IllegalArgumentException(String.format("Cannot update a remote player instance using a Packet with opcode '%s'.", packet.getOpcode().toString()));
	}
	
	/**
	 * Sets a new function to be called when this player is updated. A previous call to this function is nullified by another.
	 * 
	 * @param callback function to call
	 */
	public void setPlayerChangedCallback(PlayerChangedCallback callback)
	{
		updatePlayer = callback;
	}
	
	/**
	 * Increments the score for this team by a given value
	 * 
	 * @param points the number of points to add to this team's score
	 */
	public void incrementScore(int points)
	{
		subPlayer.incrementScore(points);
		transferData();
	}
	
	/**
	 * Increments the number of tricks won by this team this round.
	 */
	public void winTrick()
	{
		subPlayer.winTrick();
		transferData();
	}
	
	/**
	 * returns the number of tricks won by this team this round
	 * 
	 * @return the number of tricks won by this team this round
	 */
	public int getTricksWon()
	{
		return subPlayer.getTricksWon();
	}
	
	/**
	 * returns this team's score
	 * 
	 * @return this team's score
	 */
	public int getScore()
	{
		return subPlayer.getScore();
	}
	
	
	/**
	 * Represents a serializable subset of intransient information passed back and forth between remote instances of the Player class.
	 * 
	 * @author bert
	 *
	 */
	private class SubPlayer implements Serializable
	{
		private Hand hand;
		private int tricksWon = 0;
		private int score = 0;
		private int gamesWon = 0;
		private int team = 0;

		private final int uid;
		
		private static final long serialVersionUID = 2L;
		
		/**
		 * Creates a new instance of the SubPlayer class with the specified globally unique identifier
		 * 
		 * @param guid A unique identifier for this SubPlayer, to match when update requests are sent
		 */
		public SubPlayer(int guid)
		{
			uid = guid;
			// Changed size of hand to six, because the dealer will momentarily have 6 cards.
			hand = new Hand(6);
		}

		/**
		 * Increments the number of tricks won by this team this round.
		 */
		public void winTrick() 
		{
			tricksWon++;
		}
		
		/**
		 * returns the number of tricks won by this team this round
		 * 
		 * @return the number of tricks won by this team this round
		 */
		public int getTricksWon()
		{
			return tricksWon;
		}


		public void setPID(int x)
		{
			team = x;
		}
		
		public int getPID()
		{
			return team;
		}
		
		/**
		 * Returns this SubPlayer's globally unique identifier.
		 */
		public int getGuid()
		{
			return uid;
		}
		
		/**
		 * Gets the Hand associated with this SubPlayer.
		 * 
		 * @return the Hand associated with this SubPlayer.
		 */
		public Hand getHand()
		{
			return hand;
		}
		
		/**
		 * Gets the number of rounds that this SubPlayer has won.
		 * 
		 * @return the number of rounds that this SubPlayer has won.
		 */
		public int getScore()
		{
			return score;
		}
		
		/**
		 * Increments the score for this team by a given value
		 */
		public void incrementScore(int points)
		{
			score = score + points;
		}
		
		/**
		 * Gets the number of games that this SubPlayer has won.
		 * 
		 * @return the number of games that this SubPlayer has won.
		 */
		public int getGamesWon()
		{
			return gamesWon;
		}
	}
	
	/**
	 * Encapsulates the information required to create a remote instance of an existing Player object.
	 * 
	 * @author bert
	 */
	private class PlayerInitializationVector implements Serializable
	{
		private User user;
		private SubPlayer subPlayer;
		
		private static final long serialVersionUID = 1;
		
		/**
		 * Creates a new instance of the PlayerInitializationVector class
		 * @param myUser User associated with this IV
		 * @param mySubPlayer SubPlayer associated with this IV
		 */
		public PlayerInitializationVector(User myUser, SubPlayer mySubPlayer)
		{
			user = myUser;
			subPlayer = mySubPlayer;
		}
		
		/**
		 * Returns the User associated with this IV
		 */
		public User getUser()
		{
			return user;
		}
		
		/**
		 * Returns the SubPlayer structure for this IV.
		 */
		public SubPlayer getSubPlayer()
		{
			return subPlayer;
		}
	}
}
