package net.karanteeni.acidaddons.events;

import java.util.LinkedList;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class CobbleStoneForm implements Listener {
	private Material[] materials = new Material[] {
			Material.IRON_NUGGET,
			Material.NAUTILUS_SHELL,
			Material.GRAVEL
	};
	private float[] probabilities = new float[] {
			0.06f,
			0.01f,
			0.005f
	};
	private int[] maxDropCount = new int[] {
			2,
			1,
			3
	};
	
	
	@EventHandler
	public void cobbleStoneForm(BlockBreakEvent event) {
		// generate drops only for stone
		if(event.getBlock().getType() != Material.COBBLESTONE) return;
		
		// disable for silk touch
		ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
		if(mainHandItem != null && mainHandItem.containsEnchantment(Enchantment.SILK_TOUCH)) return;
		
		LinkedList<ItemStack> dropItems = new LinkedList<ItemStack>();
		Random r = new Random(System.currentTimeMillis());
		
		// loop all possible drops and collect droppable items
		for(int i = 0; i < materials.length; ++i) {
			if(r.nextDouble() > probabilities[i]) continue;
			ItemStack item = new ItemStack(materials[i], (int)(1 + Math.floor(r.nextDouble() * maxDropCount[i])));
			dropItems.add(item);
		}
		
		if(dropItems.isEmpty()) return;
		
		// change the drops
		event.setDropItems(false);
		for(ItemStack dropItem : dropItems) {
			event.getBlock().getLocation().getWorld().dropItemNaturally(
					event.getBlock().getLocation().add(0.5, 0.5, 0.5), dropItem);
		}
	}
	
	
	@EventHandler
	public void spawn(EntityShootBowEvent event) {
		Entity snowball = event.getProjectile().getWorld().spawnEntity(event.getProjectile().getLocation(), EntityType.SNOWBALL);
		snowball.setVelocity(event.getProjectile().getVelocity());
		event.setProjectile(snowball);
	}
}
