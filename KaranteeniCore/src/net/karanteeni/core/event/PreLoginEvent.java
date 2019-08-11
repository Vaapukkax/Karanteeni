package net.karanteeni.core.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

/**
 * Prevent players from joining until server is fully booted (30s delay on join)
 * @author Matti
 *
 */
public class PreLoginEvent implements Listener {
	private long boot;
	
	public PreLoginEvent(long boot) {
		this.boot = boot + 30000;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onLogin(AsyncPlayerPreLoginEvent event) {
		if(boot >= System.currentTimeMillis()) {
			event.setLoginResult(Result.KICK_OTHER);
			event.setKickMessage("Palvelin käynnistyy vielä");
		}
	}
}
