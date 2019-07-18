package net.karanteeni.core.players.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import net.karanteeni.core.players.KPlayer;

/**
 * This class listens to player damage events and
 * if player is set to be invincible then cancel the damage event.
 * @author Nuubles
 *
 */
public class Invincibility implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onDamage(EntityDamageEvent event) {
		// if player, continue
		if(event.getEntityType() != EntityType.PLAYER)
			return;
		
		// set invincibility
		KPlayer kplayer = KPlayer.getKPlayer((Player)event.getEntity());
		event.setCancelled(kplayer.isInvincible());
	}
}
