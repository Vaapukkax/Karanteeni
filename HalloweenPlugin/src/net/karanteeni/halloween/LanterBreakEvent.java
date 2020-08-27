package net.karanteeni.halloween;

import java.util.Arrays;
import org.bukkit.Color;
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
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.item.ItemType;

public class LanterBreakEvent implements Listener {
	private Halloween plugin;
	
	public LanterBreakEvent(Halloween plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLanterBreak(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(plugin.getWorldGuard().isInHalloweenGame(event.getBlock().getLocation())) {
			event.setCancelled(true);
			
			// check if the broken block is a lantern and it is broken with a pickaxe
			if(event.getBlock().getType() == Material.LANTERN && 
					ItemType.PICKAXE.contains(event.getPlayer().getInventory().getItemInMainHand().getType())) { 
				// remove the lantern
				event.getBlock().setType(Material.AIR);
				event.getBlock().getState().update();
				
				ItemStack lantern = new ItemStack(Material.LANTERN, 1);
				ItemMeta meta = lantern.getItemMeta();
				meta.setDisplayName("§6Pelottava lyhty");
				meta.setLore(Arrays.asList("§eHalloween 2019"));
				lantern.setItemMeta(meta);
				event.getPlayer().getInventory().addItem(lantern);
				
				Location loc = event.getBlock().getLocation();
				loc.add(0.5, 0.5, 0.5);
				
				event.getBlock().getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK , SoundCategory.BLOCKS, 1f, 0.5f);
				
				event.getBlock().getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.2, 0.2, 0.2, 0.01);
				event.getBlock().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 15, 0.2, 0.2, 0.2, 0.01);
			}
		}
	}
}
