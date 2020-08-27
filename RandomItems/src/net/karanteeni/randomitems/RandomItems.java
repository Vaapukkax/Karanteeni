package net.karanteeni.randomitems;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RandomItems extends JavaPlugin {
	BukkitRunnable runnable = null;
	
	@EventHandler
	public void onEnable() {
		runnable = new BukkitRunnable() {
			Random random = new Random();
			Material[] materials = Material.values();
			Enchantment[] enchantments = Enchantment.values();
			PotionEffectType[] effects = PotionEffectType.values();
			
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					try {
						ItemStack item = new ItemStack(materials[random.nextInt(materials.length)], 1);
						
						if(item.getType() == Material.ENCHANTED_BOOK) {
							Enchantment ench = enchantments[random.nextInt(enchantments.length)];
							item.addEnchantment(ench, random.nextInt(ench.getMaxLevel()));
						} else if (item.getType() == Material.POTION || item.getType() == Material.LINGERING_POTION || item.getType() == Material.SPLASH_POTION) {
							PotionEffectType type = effects[random.nextInt(effects.length)];
							PotionEffect effect = new PotionEffect(type, random.nextInt(4000), random.nextInt(3));
							PotionMeta meta = (PotionMeta)item.getItemMeta();
							meta.addCustomEffect(effect, true);
							item.setItemMeta(meta);
						}
						
						HashMap<Integer, ItemStack> items = player.getInventory().addItem(item);
						player.updateInventory();
						// drop all overflow items to ground
						for(ItemStack item_ : items.values()) {
							player.getLocation().getWorld().dropItem(player.getLocation(), item_);
						}
						
					} catch(Exception e) {
						Bukkit.broadcastMessage(player.getName() + " yritti saada itemiä muttei saanut :c");
					}
				}
			}
		};
		
		runnable.runTaskTimer(this, 30l, 600l);
	}
}
