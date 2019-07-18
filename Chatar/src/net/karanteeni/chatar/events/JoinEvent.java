package net.karanteeni.chatar.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.event.PlayerHasJoinedEvent;

public class JoinEvent implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onJoin(PlayerHasJoinedEvent event) {
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		// load the socialspy for the given player
		chatar.getSocialSpy().loadSocialSpy(event.getPlayer());
	}
}
