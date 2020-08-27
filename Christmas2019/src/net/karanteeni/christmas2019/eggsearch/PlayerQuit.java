package net.karanteeni.christmas2019.eggsearch;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import net.karanteeni.christmas2019.Christmas;

public class PlayerQuit implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Christmas plugin = Christmas.getInstance();
		plugin.getGameState().finishEdit(event.getPlayer().getUniqueId());
	}
}
