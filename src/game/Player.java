package game;

import java.io.Serializable;

import utility.Opcode;
import utility.Packet;
import utility.Trace;
import utility.TransactionThread;
import chat.User;

/**
 * Acts as a Player in the game of Euchre.
 * 
 * @author rchurtub
 *
 */
public class Player implements Serializable {

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
		
		linkage.send(Opcode.CreatePlayer, new PlayerInitializationVector(user, subPlayer));
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
	
	/*public Player(String s)
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
	 * Represents a serializable subset of intransient information passed back and forth between remote instances of the Player class.
	 * 
	 * @author bert
	 *
	 */
	private class SubPlayer implements Serializable
	{
		private Hand hand;
		private int roundsWon = 0;
		private int gamesWon = 0;

		private final int uid;
		
		/**
		 * Creates a new instance of the SubPlayer class with the specified globally unique identifier
		 * 
		 * @param guid A unique identifier for this SubPlayer, to match when update requests are sent
		 */
		public SubPlayer(int guid)
		{
			uid = guid;
			hand = new Hand(5);
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
		public int getRoundsWon()
		{
			return roundsWon;
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
		
		// serialization version
		private static final long serialVersionUID = 1;
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
