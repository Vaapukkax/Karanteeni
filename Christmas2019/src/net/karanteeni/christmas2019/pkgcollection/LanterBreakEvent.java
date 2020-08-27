package net.karanteeni.christmas2019.pkgcollection;

import java.util.Arrays;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.item.ItemType;

public class LanterBreakEvent implements Listener {
	private Christmas plugin;
	
	public LanterBreakEvent(Christmas christmas) {
		this.plugin = christmas;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLanterBreak(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(plugin.getWorldGuard().isInHalloweenGame(event.getBlock().getLocation())) {
			event.setCancelled(true);
			
			// check if the broken block is a lantern and it is broken with a pickaxe
			if(event.getBlock().getType() == Material.LIME_GLAZED_TERRACOTTA && 
					ItemType.PICKAXE.contains(event.getPlayer().getInventory().getItemInMainHand().getType())) { 
				// remove the lantern
				event.getBlock().setType(Material.AIR);
				event.getBlock().getState().update();
				
				ItemStack lantern = new ItemStack(Material.LIME_GLAZED_TERRACOTTA, 1);
				ItemMeta meta = lantern.getItemMeta();
				meta.setDisplayName("§aVihreä paketti");
				meta.setLore(Arrays.asList("§aVihreä paketti"));
				lantern.setItemMeta(meta);
				event.getPlayer().getInventory().addItem(lantern);
				
				Location loc = event.getBlock().getLocation();
				loc.add(0.5, 0.5, 0.5);
				
				event.getBlock().getWorld().playSound(loc, Sound.BLOCK_BAMBOO_BREAK , SoundCategory.BLOCKS, 1f, 0.5f);
				
				event.getBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 15, 0.2, 0.2, 0.2, 0.01);
				event.getBlock().getWorld().spawnParticle(Particle.SNOW_SHOVEL, loc, 15, 0.2, 0.2, 0.2, 0.01);
			}
			
			
			// check if the broken block is a lantern and it is broken with a pickaxe
			if(event.getBlock().getType() == Material.RED_GLAZED_TERRACOTTA && 
					ItemType.PICKAXE.contains(event.getPlayer().getInventory().getItemInMainHand().getType())) { 
				// remove the lantern
				event.getBlock().setType(Material.AIR);
				event.getBlock().getState().update();
				
				ItemStack lantern = new ItemStack(Material.RED_GLAZED_TERRACOTTA, 1);
				ItemMeta meta = lantern.getItemMeta();
				meta.setDisplayName("§cPunainen paketti");
				meta.setLore(Arrays.asList("§cPunainen paketti"));
				lantern.setItemMeta(meta);
				event.getPlayer().getInventory().addItem(lantern);
				
				Location loc = event.getBlock().getLocation();
				loc.add(0.5, 0.5, 0.5);
				
				event.getBlock().getWorld().playSound(loc, Sound.BLOCK_BAMBOO_BREAK , SoundCategory.BLOCKS, 1f, 0.5f);
				
				event.getBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 15, 0.2, 0.2, 0.2, 0.01);
				event.getBlock().getWorld().spawnParticle(Particle.SNOW_SHOVEL, loc, 15, 0.2, 0.2, 0.2, 0.01);
			}
			
			
			// check if the broken block is a lantern and it is broken with a pickaxe
			if(event.getBlock().getType() == Material.LIGHT_BLUE_GLAZED_TERRACOTTA && 
					ItemType.PICKAXE.contains(event.getPlayer().getInventory().getItemInMainHand().getType())) { 
				// remove the lantern
				event.getBlock().setType(Material.AIR);
				event.getBlock().getState().update();
				
				ItemStack lantern = new ItemStack(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1);
				ItemMeta meta = lantern.getItemMeta();
				meta.setDisplayName("§bSininen paketti");
				meta.setLore(Arrays.asList("§bSininen paketti"));
				lantern.setItemMeta(meta);
				event.getPlayer().getInventory().addItem(lantern);
				
				Location loc = event.getBlock().getLocation();
				loc.add(0.5, 0.5, 0.5);
				
				event.getBlock().getWorld().playSound(loc, Sound.BLOCK_BAMBOO_BREAK , SoundCategory.BLOCKS, 1f, 0.5f);
				
				event.getBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 15, 0.2, 0.2, 0.2, 0.01);
				event.getBlock().getWorld().spawnParticle(Particle.SNOW_SHOVEL, loc, 15, 0.2, 0.2, 0.2, 0.01);
			}
		}
	}
}
