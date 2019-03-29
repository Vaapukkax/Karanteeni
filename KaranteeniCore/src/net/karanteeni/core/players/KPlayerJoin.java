package net.karanteeni.core.players;

import java.sql.PreparedStatement;
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
	public KPlayerJoin()
	{
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
	private void playerJoin(PlayerJoinEvent event)
	{
		//Don't reload the player to the list
		if(KPlayer.getKPlayer(event.getPlayer()) == null)
			new KPlayer(event.getPlayer());
		
		//Cancel cache remover if player joins so two relogs won't clear cache
		if(cacheRemovers.containsKey(event.getPlayer().getUniqueId()))
			cacheRemovers.get(event.getPlayer().getUniqueId()).cancel();
		
		//Add player to database
		addToDatabase(event.getPlayer());
	}
	
	/**
	 * Removes player from KPlayer list when player quits
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	private void onQuit(PlayerQuitEvent event)
	{
		KPlayer kp = KPlayer.getKPlayer(event.getPlayer());
		
		if(kp != null)
		{
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
	 * Adds the given player to database
	 * @param player
	 */
	private void addToDatabase(final Player player)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return;
		
		String query = 
			"INSERT IGNORE INTO " + PlayerHandler.PlayerDataKeys.PLAYER_TABLE +
			" (" + PlayerHandler.PlayerDataKeys.UUID + ", " + 
			PlayerHandler.PlayerDataKeys.DISPLAY_NAME + ", " + 
			PlayerHandler.PlayerDataKeys.NAME +
			") VALUES (?, ?, ?);";
		
		//Save the players data to the database
		try {
			PreparedStatement st = db.prepareStatement(query);
			st.setString(1, player.getUniqueId().toString());
			st.setString(2, player.getDisplayName());
			st.setString(3, player.getName());
			
			st.executeUpdate();
			st.close();
			//db.runQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
