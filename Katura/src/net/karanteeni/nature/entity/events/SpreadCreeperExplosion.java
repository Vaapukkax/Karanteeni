package net.karanteeni.nature.entity.events;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

import net.karanteeni.nature.Katura;

public class SpreadCreeperExplosion implements Listener{
	
	private static String configtag = "explosion-explodes-creeper";
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	/**
	 * Constructor for creeper explosion cascade
	 */
	public SpreadCreeperExplosion()
	{
		pl = Katura.getPlugin(Katura.class);
		
		if(!pl.getConfig().isSet(configtag))
		{
			pl.getConfig().set(configtag, true);
			pl.saveConfig();
		}
	}
	
	/**
	 * Cascades the creepers explosions
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onCreeperExplodeDamage(EntityDamageEvent event)
	{
		//Allow only damage by explosions
		if(!(event.getCause().equals(DamageCause.BLOCK_EXPLOSION) ||
				event.getCause().equals(DamageCause.ENTITY_EXPLOSION)))
		return;
		
		//Only allow creepers to spread
		if(!event.getEntityType().equals(EntityType.CREEPER))
			return;
		
		if(!pl.getConfig().getBoolean(configtag))
			return;
		
		Creeper creeper = (Creeper)event.getEntity();
		creeper.setMaxFuseTicks(0);
	}
}
