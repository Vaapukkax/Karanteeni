package net.karanteeni.utilika.inventory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.utilika.Utilika;

public class InventoryTweaks implements Listener {
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onItemBreak(PlayerItemBreakEvent event) {
		int heldItemSlot = -1;
		Material type = null;
		boolean heldSlot = false;
		
		// get the slot in which the item was held at
		if(event.getBrokenItem().equals(event.getPlayer().getInventory().getItemInMainHand())) {
			heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
			heldSlot = true;
			type = event.getPlayer().getInventory().getItemInMainHand().getType();
		} else if(event.getBrokenItem().equals(event.getPlayer().getInventory().getItemInOffHand())) {
			heldItemSlot = 40;
			type = event.getPlayer().getInventory().getItemInOffHand().getType();
		}
		
		// verify the item was found
		if(heldItemSlot == -1 || type == null || type == Material.AIR)
			return;
		
		int heldItemSlot_ = heldItemSlot;
		Material type_ = type;
		boolean heldSlot_ = heldSlot;
		
		// run the code after the event has ran
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				// replace the item only if the hand is empty
				if(event.getPlayer().getInventory().getItemInMainHand() == null ||
						event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
					searchAndReplace(event.getPlayer(), heldItemSlot_, type_, heldSlot_);
			}
		};
		
		runnable.runTaskLater(Utilika.getPlugin(Utilika.class), 3);
	}
	
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = false)
	public void blockPlaceEvent(BlockPlaceEvent event) {
		// check if this is the last item to be used
		if(event.getItemInHand().getAmount() != 1) return;
		
		if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
			// verify we're not modifying a block using item
			if(event.getPlayer().getInventory().getItem(40) != null && 
					!event.getPlayer().getInventory().getItem(40).getType().isBlock()) return;
			
			searchAndReplace(event.getPlayer(), 40, event.getPlayer().getInventory().getItemInOffHand().getType(), false);
		} else if(event.getHand().equals(EquipmentSlot.HAND)) {
			// verify we're not modifying a block using item
			if(event.getPlayer().getInventory().getItemInMainHand() != null && 
					!event.getPlayer().getInventory().getItemInMainHand().getType().isBlock()) return;
			
			searchAndReplace(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), 
					event.getPlayer().getInventory().getItemInMainHand().getType(), true);
		}
	}
	

	/**
	 * Searches and replaces the item at the given slot with another item from inventory
	 * @param player player in whose inventory the iten replacement will happen
	 * @param slotId slot to replace
	 * @param material item to search for
	 */
	public void searchAndReplace(Player player, int slotId, Material material, boolean heldItemSlot) {
		if(slotId == -1) return;
		
		if(!player.hasPermission("utilika.easeinventory")) return;
		
		// loop all items in inventory
		for(int i = 0; i <= 40; ++i) {
			// if we're at the location to be replaced don't replace
			if(i == slotId) continue;
			
			// check if the items found are similar
			if(player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == material) {
				ItemStack replacementStack = player.getInventory().getItem(i);
				player.getInventory().setItem(i, null);
				if(heldItemSlot)
					player.getInventory().setItemInMainHand(replacementStack);
				else
					player.getInventory().setItem(slotId, replacementStack);
				playDisplaceSound(player);
				break;
			}
		}
	}
	
	
	/**
	 * Plays the item displaced sound for player when items are empty or breaks and is replaced
	 * @param player player to whom the sound is placed
	 */
	public void playDisplaceSound(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 0.5f, 1.7f);
	}
}
