package net.karanteeni.groups.player;

import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.karanteeni.groups.KaranteeniPerms;

public class JoinEvent implements Listener{

	/**
	 * Player joins the server, load the players data and permissions
	 * @param event
	 */
	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onJoin(PlayerJoinEvent event)
	{
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		
		Group group = perms.getPlayerModel().getLocalGroup(event.getPlayer());
		
		if(group == null)
		{
			Bukkit.broadcastMessage("�4CRITICAL ERROR IN DATABASE ACCESS, GROUP RETURNED NULL!");
			Bukkit.broadcastMessage("�4PLEASE CONTACT SERVER STAFF IMMEDIATELY! MORE INFORMATION IN CONSOLE!");
			Bukkit.broadcastMessage("�4Timestamp in console: " + (new Date()).toString());
			Bukkit.broadcastMessage("�4Shutting down server to prevent further playerdata damage!");
			Bukkit.getLogger().log(Level.SEVERE, "GROUP RETURNED NULL! No default group set or access to database"
					+ " is broken!");
		}
		
		//Load the players permissions of group
		perms.getPlayerModel().loadOnlinePlayerPermissions(event.getPlayer(), group);
		
		perms.getPlayerModel().loadOnlinePlayerPrivatePermissions(event.getPlayer());
		
		/*Bukkit.broadcastMessage("Player: " + event.getPlayer().getName() + " Group: " + group.getID());
		Bukkit.broadcastMessage("Name of group in players language: " + group.getName(event.getPlayer()));
		Bukkit.broadcastMessage("Long Prefix: " + group.getPrefix(event.getPlayer(), false) + 
				" Short Prefix: " + group.getPrefix(event.getPlayer(), true) + 
				" Suffix: " + group.getSuffix());*/
	}
	
	/**
	 * Unload the permissions of this player
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onQuit(PlayerQuitEvent event)
	{
		KaranteeniPerms plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		plugin.getPlayerModel().clearOnlinePlayerPermissions(event.getPlayer());
	}
}