package net.karanteeni.chatar.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.event.PlayerHasJoinedEvent;

public class JoinEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	private void onPreJoin(AsyncPlayerPreLoginEvent event) {
		// verify that the player can actually join
		if(event.getLoginResult() == Result.ALLOWED) {
			Chatar plugin = Chatar.getPlugin(Chatar.class);
			
			// load the ignore data to player
			plugin.getIgnoreData().loadData(event.getUniqueId());			
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onJoin(PlayerHasJoinedEvent event) {
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		// load the socialspy for the given player
		chatar.getSocialSpy().loadSocialSpy(event.getPlayer());
	}
	
	
	private void onQuit(PlayerQuitEvent event) {
		Chatar plugin = Chatar.getPlugin(Chatar.class);
		plugin.getIgnoreData().unload(event.getPlayer().getUniqueId());
	}
}
