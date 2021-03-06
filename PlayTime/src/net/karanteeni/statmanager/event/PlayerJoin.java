package net.karanteeni.statmanager.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.event.PlayerHasJoinedEvent;
import net.karanteeni.statmanager.StatManager;

public class PlayerJoin implements Listener {
	private StatManager plugin;
	
	public PlayerJoin(StatManager time) {
		this.plugin = time;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onJoin(PlayerHasJoinedEvent event) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				// load the given player to count the play time
				plugin.getManager().loadToMemory(event.getPlayer().getUniqueId());
			}
		};
		
		runnable.runTaskAsynchronously(plugin);
	}
}
