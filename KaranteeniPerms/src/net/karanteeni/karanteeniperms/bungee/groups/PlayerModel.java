package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;

/**
 * Contains the connection to the database
 * and manages the caching. Is used to get the group of a player.
 * LocalGroup, GlobalGroup, GlobalLevel
 * @author Nuubles
 *
 */
public class PlayerModel {

	private BungeeGroupDatabase db;
	private KaranteeniPermsBungee plugin;
	private HashMap<UUID, PermissionPlayer> permissionPlayers = new HashMap<UUID, PermissionPlayer>();
	private HashMap<UUID, PermissionPlayer> playersToLoad = new HashMap<UUID, PermissionPlayer>();
	
	
	/** Permissions of players */
	//private HashMap<UUID,PermissionAttachment> playerPermissions = new HashMap<UUID,PermissionAttachment>();
	
	/**
	 * Creates a new playerdata handler
	 * @param pl
	 */
	public PlayerModel(KaranteeniPermsBungee pl) throws IllegalArgumentException {
		if(this.plugin != null) throw new IllegalArgumentException("PlayerModel has already been initialized!");
		this.plugin = pl;
		db = new BungeeGroupDatabase(); //May throw SQLException		
	}
	
	
	/**
	 * Clears the permission player attached to this uuid
	 * @param uuid
	 */
	public void clearPermissionPlayer(UUID uuid) {
		PermissionPlayer pp = permissionPlayers.remove(uuid);
		if(pp != null) pp.deActivatePlayer();
		pp = playersToLoad.remove(uuid);
		if(pp != null) pp.deActivatePlayer();
	}
	
	
	/**
	 * Clears all permission players from memory
	 */
	public void clearAllPermissionPlayers() {
		for(Entry<UUID, PermissionPlayer> entry : permissionPlayers.entrySet())
			entry.getValue().deActivatePlayer();
		for(Entry<UUID, PermissionPlayer> entry : playersToLoad.entrySet())
			entry.getValue().deActivatePlayer();
	}
	
	
	/**
	 * Returns the group database manager
	 * @return access to database
	 */
	public BungeeGroupDatabase getGroupDatabase() {
		return this.db;
	}
	
	
	/**
	 * Returns the permissionplayer according to given UUID
	 * @param uuid
	 * @return
	 */
	public PermissionPlayer getPermissionPlayer(UUID uuid) {
		// load the activated player
		PermissionPlayer player = permissionPlayers.get(uuid);
		if(player != null) return player;
		
		// get the player who is about to be activated
		player = playersToLoad.get(uuid);
		if(player != null) return player;
		
		// load the player but do not activate it
		player = PermissionPlayer.getPermissionPlayer(uuid);
		return player;
	}
	
	
	/**
	 * Load permission player data before activation
	 * @param uuid uuid of the players data to load
	 */
	protected void loadPermissionPlayerData(UUID uuid) {
		PermissionPlayer pl = PermissionPlayer.getPermissionPlayer(uuid);
		playersToLoad.put(uuid, pl);
	}
	
	
	/**
	 * Activates a loaded permissionplayer
	 * @param uuid uuid of the player to activate
	 * @return true if player was loaded, false otherwise
	 * @throws NullPointerException trown if PermissionPlayer data was not loaded for this given player
	 */
	protected boolean activatePermissionPlayer(UUID uuid) throws NullPointerException {
		PermissionPlayer player = playersToLoad.remove(uuid);
		if(player == null)
			throw new NullPointerException("Tried to activate not loaded PermissionPlayer: " + uuid.toString());
		
		permissionPlayers.put(uuid, player);
		return player.activatePlayer();
	}
}