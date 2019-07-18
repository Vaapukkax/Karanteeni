package net.karanteeni.core.players;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.DatabaseConnector;

/**
 * Manages the KPlayer data and cache to work with player joins and quits
 * @author Nuubles
 *
 */
public class KPlayerJoin implements Listener{
	private HashMap<UUID, BukkitTask> cacheRemovers = new HashMap<UUID,BukkitTask>();
	private int cacheLongetivity;
	private KaranteeniCore core = null;
	
	/**
	 * Initializes onJoin and creates config data
	 */
	public KPlayerJoin() {
		core = KaranteeniCore.getPlugin(KaranteeniCore.class);
		if(!core.getConfig().isSet("Cache.in-memory-seconds")) {
			core.getConfig().set("Cache.in-memory-seconds", 600);
			core.saveConfig();
		}
		//Load the time KPlayer data is held in cache
		cacheLongetivity = core.getConfig().getInt("Cache.in-memory-seconds");
	}
	
	
	/**
	 * Adds player to KPlayer list when player joins
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerJoin(PlayerJoinEvent event) {
		//Don't reload the player to the list
		if(KPlayer.getKPlayer(event.getPlayer()) == null)
			new KPlayer(event.getPlayer());
		
		//Cancel cache remover if player joins so two relogs won't clear cache
		if(cacheRemovers.containsKey(event.getPlayer().getUniqueId()))
			cacheRemovers.get(event.getPlayer().getUniqueId()).cancel();
		
		// check if player has changed the name
		boolean nameSameAsBefore = nameMatches(event.getPlayer());
		
		//Add player to database
		addToDatabase(event.getPlayer());
		
		// if player has changed name reset the displayname
		if(!nameSameAsBefore)
			resetDisplayName(event.getPlayer());
			
		// load and set player display name from database
		event.getPlayer().setDisplayName(
				KaranteeniCore.getPlayerHandler().getOfflineDisplayName(event.getPlayer().getUniqueId()));
	}
	
	
	/**
	 * Sets the display name for player and if the name does not match
	 * @param player player whose displayname will be set
	 */
	private void resetDisplayName(Player player) {
		KaranteeniCore.getPlayerHandler().setDisplayName(player.getUniqueId(), player.getName());
	}
	
	
	/**
	 * Removes player from KPlayer list when player quits
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	private void onQuit(PlayerQuitEvent event) {
		KPlayer kp = KPlayer.getKPlayer(event.getPlayer());
		
		if(kp != null) {
			kp.destroy();
			
			//Is there data in cache, if not then don't create timer
			if(!KPlayer.hasCachedData(event.getPlayer().getUniqueId()))
				return;

			//Create a new cache remover to remove the data in cache
			cacheRemovers.put(event.getPlayer().getUniqueId(),
				Bukkit.getScheduler().runTaskLater(core, new Runnable() {
					UUID uuid = event.getPlayer().getUniqueId();
					
					@Override
					public void run() {
						//Clear the cache of this player after x seconds
						if(KPlayer.hasCachedData(uuid))
							KPlayer.clearCache(uuid);
					}
				}, cacheLongetivity*20));
		}
	}
	
	
	/**
	 * Checks if players name in database matches the name player currently has
	 * @param player player whose name is being checked
	 * @return true if name is the same as before or error, false otherwise
	 */
	private boolean nameMatches(final Player player) {
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return true;
		
		// select all names with this players uuid
		String query = "SELECT " + PlayerHandler.PlayerDataKeys.NAME + " FROM "+
				PlayerHandler.PlayerDataKeys.PLAYER_TABLE +
				" WHERE " + PlayerHandler.PlayerDataKeys.UUID + " = '" + player.getUniqueId().toString() + "';";
		
		try {
			Statement stmt = db.getStatement();
			ResultSet set = stmt.executeQuery(query);
			String name = "";
			if(set.next())
				name = set.getString(1);
			// if players name is the same the player has not changed the name
			return player.getName().equals(name);
		} catch(SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	
	/**
	 * Adds the given player to database
	 * @param player
	 */
	private void addToDatabase(final Player player) {
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return;
		
		/*String query = 
			"INSERT IGNORE INTO " + PlayerHandler.PlayerDataKeys.PLAYER_TABLE +
			" (" + PlayerHandler.PlayerDataKeys.UUID + ", " + 
			PlayerHandler.PlayerDataKeys.DISPLAY_NAME + ", " + 
			PlayerHandler.PlayerDataKeys.NAME +
			") VALUES (?, ?, ?);";*/
		
		String query = "INSERT INTO " + PlayerHandler.PlayerDataKeys.PLAYER_TABLE + 
				" (" + PlayerHandler.PlayerDataKeys.UUID + ", " +
				PlayerHandler.PlayerDataKeys.DISPLAY_NAME + ", " +
				PlayerHandler.PlayerDataKeys.NAME +
				") VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE " +
				PlayerHandler.PlayerDataKeys.NAME + " = ?;";
		
		//Save the players data to the database
		try {
			PreparedStatement st = db.prepareStatement(query);
			st.setString(1, player.getUniqueId().toString());
			st.setString(2, player.getDisplayName());
			st.setString(3, player.getName());
			st.setString(4, player.getName());
			
			st.executeUpdate();
			st.close();
			//db.runQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
