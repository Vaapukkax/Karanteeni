package net.karanteeni.core.players;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.core.net.Priority;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import net.karanteeni.core.KaranteeniCore;

/**
 * Manages the KPlayer data and cache to work with player joins and quits
 * @author Nuubles
 *
 */
public class KPlayerJoin implements Listener{
	private HashMap<UUID, BukkitTask> cacheRemovers = new HashMap<UUID,BukkitTask>();
	private int cacheLongetivity;
	private KaranteeniCore core = null;
	private String DISPLAYNAME_KEY = "displayname";
	
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
	 * Called before player joines the server
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	private void playerLogin(AsyncPlayerPreLoginEvent event) {
		// if player is not allowed to join, prevent login
		if(event.getLoginResult() != Result.ALLOWED)
			return;
		
		// prevent player loading twice
		if(KPlayer.getKPlayer(event.getUniqueId()) == null)
			new KPlayer(event.getUniqueId());
		
		
		//Cancel cache remover if player joins so two relogs won't clear cache
		if(cacheRemovers.containsKey(event.getUniqueId()))
			cacheRemovers.get(event.getUniqueId()).cancel();
		
		// check if player has changed the name
		boolean nameSameAsBefore = nameMatches(event.getUniqueId(), event.getName());
		//Add player to database
		addToDatabase(event.getUniqueId(), event.getName());
		
		// if the player has changed names, reset nickname
		if(!nameSameAsBefore)
			resetDisplayName(event.getUniqueId(), event.getName());
		
		// set the displayname loader
		KPlayer kp = KPlayer.getKPlayer(event.getUniqueId());
		if(kp != null) {
			// store the displayname to set to kplayer data
			kp.setData(core, DISPLAYNAME_KEY, KaranteeniCore.getPlayerHandler().getOfflineDisplayName(event.getUniqueId()));
			kp.addPlayerJoinModifier(EventPriority.LOWEST, this::displaynameSetter);
		}
	}
	
	
	/**
	 * Sets the players displayname once player has joined
	 * @param kplayer player to join
	 */
	private void displaynameSetter(KPlayer kplayer) {
		if(kplayer.dataExists(core, DISPLAYNAME_KEY)) {
			kplayer.getPlayer().setDisplayName(kplayer.getString(core, DISPLAYNAME_KEY));
			kplayer.removeData(core, DISPLAYNAME_KEY);
		}
	}
	
	
	/**
	 * Sets the player to the players data holder class
	 * @param event player joined server
	 */
	@EventHandler (priority = EventPriority.MONITOR)
	private void onLogin(PlayerLoginEvent event) {
		KPlayer kp = KPlayer.getKPlayer(event.getPlayer());
		if(kp == null) return;
		
		// set the player to the players data holder class
		kp.setPlayer(event.getPlayer());
	}
	
	
	/**
	 * Runs the player data (such as nickname) modifications
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void onJoin(PlayerJoinEvent event) {
		KPlayer kp = KPlayer.getKPlayer(event.getPlayer());
		if(kp == null) return;
		
		kp.runPlayerJoinModifiers();
		kp.runPlayerLateJoinModifiers();
	}
	
	
	/**
	 * Sets the display name for player and if the name does not match
	 * @param player player whose displayname will be set
	 */
	private void resetDisplayName(final UUID uuid, final String name) {
		KaranteeniCore.getPlayerHandler().setDisplayName(uuid, name);
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
	 * Removes player from KPlayer list when player quits
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	private void onQuit(PlayerKickEvent event) {
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
	private boolean nameMatches(final UUID uuid, final String playerName) {
		Connection conn = null;
		//if(!db.isConnected()) return true;
		
		// select all names with this players uuid
		String query = "SELECT " + PlayerHandler.PlayerDataKeys.NAME + " FROM "+
				PlayerHandler.PlayerDataKeys.PLAYER_TABLE +
				" WHERE " + PlayerHandler.PlayerDataKeys.UUID + " = '" + uuid.toString() + "';";
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			ResultSet set = stmt.executeQuery(query);
			String name = "";
			if(set.next())
				name = set.getString(1);
			// if players name is the same the player has not changed the name
			return playerName.equals(name);
		} catch(SQLException e) {
			e.printStackTrace();
			return true;
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /* ignored */ }
		}
	}
	
	
	/**
	 * Adds the given player to database
	 * @param player
	 */
	private void addToDatabase(final UUID uuid, final String playerName) {
		Connection conn = null;
		
		String query = "INSERT INTO " + PlayerHandler.PlayerDataKeys.PLAYER_TABLE + 
				" (" + PlayerHandler.PlayerDataKeys.UUID + ", " +
				PlayerHandler.PlayerDataKeys.DISPLAY_NAME + ", " +
				PlayerHandler.PlayerDataKeys.NAME +
				") VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE " +
				PlayerHandler.PlayerDataKeys.NAME + " = ?;";
		
		//Save the players data to the database
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, uuid.toString());
			st.setString(2, playerName);
			st.setString(3, playerName);
			st.setString(4, playerName);
			
			st.executeUpdate();
			st.close();
			//db.runQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /* ignored */ }
		}
	}
}
