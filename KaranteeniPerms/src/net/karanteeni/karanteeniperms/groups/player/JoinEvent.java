package net.karanteeni.karanteeniperms.groups.player;

import java.util.Date;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

public class JoinEvent implements Listener{

	/**
	 * Player joins the server, load the players data and permissions
	 * @param event
	 */
	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onJoin(PlayerJoinEvent event) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
				Group group = perms.getPlayerModel().getLocalGroup(event.getPlayer());
				
				if(group == null) {
					Bukkit.broadcastMessage("§4CRITICAL ERROR IN DATABASE ACCESS, GROUP RETURNED NULL!");
					Bukkit.broadcastMessage("§4PLEASE CONTACT SERVER STAFF IMMEDIATELY! MORE INFORMATION IN CONSOLE!");
					Bukkit.broadcastMessage("§4Timestamp in console: " + (new Date()).toString());
					Bukkit.broadcastMessage("§4Shutting down server to prevent further playerdata damage!");
					Bukkit.getLogger().log(Level.SEVERE, "GROUP RETURNED NULL! No default group set or access to database"
							+ " is broken!");
				}
				
				//perms.getPlayerModel().clearOnlinePlayerPermissions(event.getPlayer()); //Clear all permissions from player
				//Load the players permissions of group
				perms.getPlayerModel().loadOnlinePlayerGroupPermissions(event.getPlayer(), group);
				
				// Load players private permissions
				// load private permissions in async to prevent database lag
				if(!perms.getPlayerModel().loadOnlinePlayerPrivatePermissions(event.getPlayer()))
				Bukkit.getLogger().log(Level.SEVERE, "Failed to load player "+
						event.getPlayer().getUniqueId()+" permissions from database!");
			}
		};
		
		runnable.runTaskAsynchronously(KaranteeniPerms.getPlugin(KaranteeniPerms.class));
	}
	
	/**
	 * Unload the permissions of this player
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onQuit(PlayerQuitEvent event) {
		KaranteeniPerms plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		plugin.getPlayerModel().clearOnlinePlayerPermissions(event.getPlayer());
	}
}
