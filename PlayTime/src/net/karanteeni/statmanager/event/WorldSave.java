package net.karanteeni.statmanager.event;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.statmanager.StatManager;

public class WorldSave implements Listener {
	private StatManager plugin;
	
	public WorldSave(StatManager plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onWorldSave(WorldSaveEvent event) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				// save the players play times when the world saves
				if(Bukkit.getWorlds().size() == 0 || event.getWorld().equals(Bukkit.getWorlds().get(0))) {
					plugin.getManager().performLevelCheck();
					plugin.getManager().save();
				}
			}
		};
		
		runnable.runTaskAsynchronously(plugin);
	}
	
}
