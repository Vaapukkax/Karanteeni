package net.karanteeni.core.information.translation;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.KaranteeniCore;

public class PlayerJoinLoadTranslation implements Listener {
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onJoin(AsyncPlayerPreLoginEvent event) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				KaranteeniCore.getTranslator().loadLocale(event.getUniqueId());
			}
		};
		
		runnable.runTaskAsynchronously(KaranteeniCore.getPlugin(KaranteeniCore.class));
	}
}
