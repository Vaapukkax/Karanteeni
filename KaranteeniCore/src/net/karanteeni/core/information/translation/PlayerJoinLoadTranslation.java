package net.karanteeni.core.information.translation;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.karanteeni.core.KaranteeniCore;

public class PlayerJoinLoadTranslation implements Listener {
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onJoin(PlayerJoinEvent event) {
		KaranteeniCore.getTranslator().loadLocale(event.getPlayer());
	}
}
