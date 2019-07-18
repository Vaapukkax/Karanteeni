package net.karanteeni.nature.entity.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;
import net.karanteeni.nature.Katura;

public class HungerPreventer implements Listener {
	private double limit = 1;
	
	public HungerPreventer() {
		Plugin plugin = Katura.getPlugin(Katura.class);
		if(!plugin.getConfig().isSet("prevent-hunger-damage-from-level")) {
			plugin.getConfig().set("prevent-hunger-damage-from-level", 1);
			plugin.saveConfig();
		}
		limit = plugin.getConfig().getInt("prevent-hunger-damage-from-level");
	}
	
	@EventHandler
	private void preventHungerDamate(EntityDamageEvent event) {
		// if the entity is starving, prevent the starvation
		if(event.getCause() == DamageCause.STARVATION && event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			double damage = Math.max(0, Math.min(event.getDamage(), player.getHealth()-limit));
			if(damage > 0)
				event.setDamage(Math.max(0, Math.min(event.getDamage(), player.getHealth()-limit)));
			else
				event.setCancelled(true);
		}
	}
}
