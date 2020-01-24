package net.karanteeni.core.block.executable;

import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public abstract class ActionSign extends ActionBlock {
	public String translationKey = null;
	public String[] defaultContent;
	
	/**
	 * Create a block executable object. No other constructors will be used.
	 * You may override this constructor, BUT NOT MAKE OTHER ONES
	 * @param block block this executable uses
	 */
	public ActionSign(Block block) throws IllegalArgumentException {
		super(block);
		if(!(block.getState() instanceof Sign)) throw new IllegalArgumentException("Argument is not a Sign");
	}

	
	/**
	 * Sets the sign translation key and default content
	 * @param translationKey key to use for translation
	 * @param defaultContent array with a length of 4
	 * @return true if translation set successful, false otherwise
	 */
	public boolean setTranslatable(String translationKey, String[] defaultContent) {
		this.translationKey = translationKey;
		this.defaultContent = defaultContent;
		return false;
	}
	
	
	/**
	 * Changes the sign content
	 * @param content content to set the sign to
	 * @return true if content set, false otherwise
	 */
	public boolean setContent(String[] content) {
		return false;
	}
	
	
	/**
	 * Check if this sign is a translated sign
	 * @return true if sign is translatable, false otherwise
	 */
	public boolean isTranslated() {
		return this.translationKey != null;
	}
	
	
	/**
	 * Get all the players who can see this sign
	 * @return Players who are able to see this sign
	 */
	public Set<Player> getViewingPlayers() {
		return null;
	}
	
	
	/**
	 * Updates the sign for all viewing players
	 */
	public void update() {
		
	}
	
	
	/**
	 * Translates the sign content to the given player
	 * @param player player to translate the content to
	 */
	public void translateContent(Player player) {
		
	}
	
	
	/**
	 * Sets the temporary sign content to given arguments
	 * @param player player to whom the temporary content is set to
	 * @param content content to set to
	 */
	public void setDisplay(Player player, String[] content) {
		
	}
	
	
	/**
	 * Return the sign object of this action
	 * @return sign this action is assigned to
	 */
	public Sign getSign() {
		return (Sign)this.getBlock().getState();
	}
}
