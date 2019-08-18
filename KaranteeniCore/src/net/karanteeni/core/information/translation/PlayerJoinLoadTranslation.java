package net.karanteeni.core.information.translation;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.KaranteeniCore;

public class PlayerJoinLoadTranslation implements Listener {
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onJoin(PlayerJoinEvent event) {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				KaranteeniCore.getTranslator().loadLocale(event.getPlayer());
			}
		};
		
		runnable.runTaskAsynchronously(KaranteeniCore.getPlugin(KaranteeniCore.class));
	}
}
