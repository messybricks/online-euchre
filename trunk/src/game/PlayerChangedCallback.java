package game;

/**
 * This interface provides a mechanism by which an object can 'listen' for changes to Player objects that it owns.
 * Whenever a Player object is updated, the PlayerUpdated method will be called.
 * 
 * @author bert
 *
 */
public interface PlayerChangedCallback
{
	/**
	 * This method is called whenever a Player object is updated.
	 * 
	 * @param player the Player that was updated
	 */
	public void PlayerUpdated(Player player);
}
