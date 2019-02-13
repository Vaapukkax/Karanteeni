package net.karanteeni.nature.entity.events;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import net.karanteeni.nature.Katura;
import net.karanteeni.nature.data.EntityClass;

public class EntitySpawn implements Listener
{
	private static String spawnPrefix = "entity.%s.spawn";
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	public EntitySpawn() 
	{
		pl = Katura.getPlugin(Katura.class);
		
		//Loop all entities which can spawn
		for(EntityType type : EntityType.values())
		{
			if(type.isSpawnable())
				if(!pl.getConfig().isSet(String.format(spawnPrefix, type.toString()))) 
					pl.getConfig().set(String.format(spawnPrefix, type.toString()), true);
		}
		pl.saveConfig();
	}
	
	/**
	 * Handles entity spawns
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSpawn(EntitySpawnEvent event)
	{
		//Modify only spawnable entities
		if(!event.getEntityType().isSpawnable())
			return;
		
		List<MetadataValue> meta = event.getEntity().getMetadata(EntityClass.KEY.toString());
		boolean isVanilla = meta.isEmpty();
		if(isVanilla)
		{
			if(!pl.getConfig().getBoolean(String.format(spawnPrefix, event.getEntityType().toString())))
				event.setCancelled(true);
			
			//Set meta to entity to inform it being vanilla
			event.getEntity().setMetadata(EntityClass.KEY.toString(), 
					new FixedMetadataValue(pl, EntityClass.VANILLA));
		}
	}
}
