package net.karanteeni.karanteenials.functionality;

import net.karanteeni.karanteenials.Karanteenials;

/**
 * Commonly needed functions and data in Karanteenials, such as Back data
 * used to return player to the position before teleportation
 * @author Nuubles
 *
 */
public class PlayerFunctionality {
	Karanteenials plugin;
	
	private final TpToggle tptoggle;
	private final PlayerBlock playerBlock; 
	private final PowerLevel powerLevel;
	private final GameModeModule gmm;
	
	public PlayerFunctionality(Karanteenials plugin) {
		this.plugin = plugin;
		powerLevel = new PowerLevel(plugin);
		playerBlock = new PlayerBlock(plugin);
		tptoggle = new TpToggle(plugin);
		gmm = new GameModeModule();
		gmm.registerTranslations();
	}
	
	
	/**
	 * Initialize database tables
	 */
	public static void initDatabaseTables() {		
		GameModeModule.initTable();
		PlayerBlock.initTable();
		TpToggle.initTable();
	}
	
	
	/**
	 * Returns the active gamemode module
	 * @return gamemode module
	 */
	public GameModeModule getGameModeModule() {
		return gmm;
	}
	
	
	/**
	 * Returns the player block data manager class
	 * @return
	 */
	public PlayerBlock getBlockPlayer() {
		return this.playerBlock;
	}
	
	
	/**
	 * Returns the tptoggle data manager class
	 * @return
	 */
	public TpToggle getTpToggle() {
		return this.tptoggle;
	}
	
	
	/**
	 * Returns the power level of player
	 * @return
	 */
	public PowerLevel getPowerLevel() {
		return this.powerLevel;
	}
}