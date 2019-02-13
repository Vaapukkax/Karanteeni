package net.karanteeni.nature.block.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import net.karanteeni.nature.Katura;

public class AutoGrass implements Listener{
	
	private static String configKey = "automatic-grass-handling";
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	public AutoGrass()
	{
		pl = Katura.getPlugin(Katura.class);
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey))
		{
			pl.getConfig().set(configKey, true);
			pl.saveConfig();
		}
	}
	
	/**
	 * When breaking a block, move the grass from current block to block below if not pressing shift
	 * @param event
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event)
	{
		if(!pl.getConfig().getBoolean(configKey))
			return;
		
		Material blockType = event.getBlock().getType();
		
		if(!(blockType.equals(Material.GRASS_BLOCK) ||
				blockType.equals(Material.PODZOL) ||
				blockType.equals(Material.MYCELIUM)))
			return;
		
		//Player is not sneaking
		if(!event.getPlayer().isSneaking())
		{
			//Don't do this effect when player has a silk touch tool in hand
			if(!event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH))
			{
				Block block = event.getBlock().getLocation().add(0.0, -1.0, 0.0).getBlock();
				
				if(block.getType().equals(Material.DIRT))
				{
					block.setType(blockType);
					block.getState().update();
				}
			}
		}
	}
	
	/**
	 * When a dirt is places on top if grass, mycelium or podzol the blocks switch places
	 * @param event
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBreak(BlockPlaceEvent event)
	{
		//Player is not sneaking
		if(!event.getPlayer().isSneaking())
		{
			Material blockType = event.getBlock().getType();
			
			//Jos ei ole dirt niin palataan
			if(!event.getBlock().getType().equals(Material.DIRT))
				return;
			
			Block blockBelow = event.getBlock().getLocation().add(0, -1, 0).getBlock();
			
			//If there's no block below then return
			if(blockBelow == null)
				return;
			
			if(!(blockBelow.getType().equals(Material.GRASS_BLOCK) ||
					blockBelow.getType().equals(Material.PODZOL) ||
					blockBelow.getType().equals(Material.MYCELIUM)))
				return;
			
			
			//Swap the blocks
			event.getBlock().setType(blockBelow.getType());
			event.getBlock().getState().update();
			blockBelow.setType(blockType);
			blockBelow.getState().update();
		}
	}
}
