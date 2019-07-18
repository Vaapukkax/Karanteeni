package net.karanteeni.statmanager.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import net.karanteeni.statmanager.StatManager;

public class PlayerQuit implements Listener {
	StatManager plugin;
	
	public PlayerQuit(StatManager time) {
		this.plugin = time;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		// unloads and saves the players play time
		plugin.getManager().unloadUUID(event.getPlayer().getUniqueId());
	}
}
