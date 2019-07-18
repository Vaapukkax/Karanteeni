package net.karanteeni.teleportal.noobspawn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.karanteeni.core.event.PlayerHasJoinedEvent;
import net.karanteeni.teleportal.Teleportal;
import net.karanteeni.teleportal.spawn.SpawnManager;

public class JoinEvent implements Listener {
	@EventHandler
	private void onJoin(PlayerHasJoinedEvent event) {
		if(event.getPlayer().hasPlayedBefore())
			return;
		
		// teleport the first join player to noobspawn
		SpawnManager sm = new SpawnManager(Teleportal.getPlugin(Teleportal.class));
		sm.teleportToNoobSpawn(event.getPlayer());
	}
}
