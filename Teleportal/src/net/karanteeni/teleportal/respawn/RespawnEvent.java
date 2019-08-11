package net.karanteeni.teleportal.respawn;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import net.karanteeni.teleportal.Teleportal;
import net.karanteeni.teleportal.spawn.SpawnManager;

public class RespawnEvent implements Listener {
	private boolean overrideBed = false;
	
	public RespawnEvent(Teleportal plugin) {
		if(!plugin.getConfig().isSet("respawn.override-bed")) {
			plugin.getConfig().set("respawn.override-bed", false);
			plugin.saveConfig();
		}
		
		this.overrideBed = plugin.getConfig().getBoolean("respawn.override-bed");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent event) {
		if(!overrideBed && event.isBedSpawn())
			return;
		
		SpawnManager rm = new SpawnManager(Teleportal.getPlugin(Teleportal.class));
		Location loc = rm.getRespawnLocation();
		if(loc != null)
			event.setRespawnLocation(loc);
	}
}
