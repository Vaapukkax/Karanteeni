package net.karanteeni.groups.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener{

	/**
	 * Player joins the server, load the players data
	 * @param event
	 */
	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = false)
	private void onJoin(PlayerJoinEvent event)
	{
		
	}
}
