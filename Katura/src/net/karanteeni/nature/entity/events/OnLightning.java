package net.karanteeni.nature.entity.events;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.LightningStrikeEvent.Cause;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import net.karanteeni.core.block.BlockType;
import net.karanteeni.nature.Katura;

public class OnLightning implements Listener{

	private static String configKey = "lightning-transform-blocks";
	private static String configKey2 = "lightning-transform-size";
	
	public OnLightning()
	{
		Plugin pl = Katura.getPlugin(Katura.class);
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey))
		{
			pl.getConfig().set(configKey, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(configKey2))
		{
			pl.getConfig().set(configKey2, 4);
			pl.saveConfig();
		}
	}
	
	/**
	 * Creates lightning effects when a lightning is struck
	 * @param event
	 */
	@EventHandler
	private void onLightning(LightningStrikeEvent event)
	{
		if(!Katura.getPlugin(Katura.class).getConfig().getBoolean(configKey))
			return;
		
		//This works only with weather
		if(!(event.getCause().equals(Cause.WEATHER) || event.getCause().equals(Cause.COMMAND)))
			return;
		
		BoundingBox box = event.getLightning().getBoundingBox();
		final int SIZE = Katura.getPlugin(Katura.class).getConfig().getInt(configKey2);
		box.expand(SIZE);
		
		Location middlePoint = event.getLightning().getLocation();
		Random r = new Random();
		
		for(double x = box.getMinX(); x < box.getMaxX(); ++x)
		{
			for(double y = box.getMinY(); y < box.getMaxY(); ++y)
			{
				for(double z = box.getMinZ(); z < box.getMaxZ(); ++z)
				{
					Location loc = new Location(event.getWorld(), x, y, z);
					Block block = loc.getBlock();
					
					if(block.getType().equals(Material.AIR))
						continue;
					
					//How close are we to the struck area
					if(r.nextDouble() < (SIZE-Math.abs(loc.distance(middlePoint)))/SIZE)
					{
						if(block.getType().equals(Material.SAND))
							switchType(block, Material.RED_SAND);
						else if(BlockType.GROWABLE.contains(block.getType()))
							switchType(block, Material.DIRT);
						else if(BlockType.LOG.contains(block.getType()))
							switchType(block, Material.COAL_BLOCK);
						else if(block.getType().equals(Material.COAL_BLOCK))
							switchType(block, Material.DIAMOND_ORE);
						else if(BlockType.LEAVES.contains(block.getType()))
							switchType(block, Material.AIR);
						else if(BlockType.STONE.contains(block.getType()))
							switchType(block, Material.COBBLESTONE);
						else if(block.getType().equals(Material.GRAVEL))
							switchType(block, Material.SAND);
						else if(block.getType().equals(Material.STONE_BRICKS))
							switchType(block, Material.CRACKED_STONE_BRICKS);
						else if(block.getType().equals(Material.SOUL_SAND))
							loc.getWorld().spawnEntity(loc.add(0, 1, 0), EntityType.ZOMBIE);
					}
				}
			}
		}
	}
	
	/**
	 * Switches the type of the block
	 * @param block
	 * @param material
	 */
	private void switchType(Block block, Material material)
	{
		block.setType(material);
		block.getState().update();
	}
}
