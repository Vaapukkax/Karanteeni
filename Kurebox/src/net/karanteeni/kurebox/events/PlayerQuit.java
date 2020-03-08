package net.karanteeni.kurebox.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.karanteeni.kurebox.Kurebox;

/**
 * Stops music for playing for players who quit the game
 * @author Nuubles
 *
 */
public class PlayerQuit implements Listener {
	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		Kurebox.getPlugin(Kurebox.class).getMusicManager().stopCustomMusic(event.getPlayer());
	}
	
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Kurebox.getPlugin(Kurebox.class).getMusicManager().stopCustomMusic(event.getPlayer());
	}
}
