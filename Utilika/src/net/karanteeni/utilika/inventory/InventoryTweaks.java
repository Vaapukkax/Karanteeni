package net.karanteeni.utilika.inventory;

import org.bukkit.GameMode;
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
	private Utilika plugin;
	
	
	public InventoryTweaks(Utilika plugin) {
		this.plugin = plugin;
	}
	
	
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
					plugin.getInventoryUtilities().searchAndReplace(event.getPlayer(), heldItemSlot_, type_, heldSlot_);
			}
		};
		
		runnable.runTaskLater(Utilika.getPlugin(Utilika.class), 3);
	}
	
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void blockPlaceEvent(BlockPlaceEvent event) {
		// check if this is the last item to be used
		if(event.isCancelled()) return;
		if(event.getItemInHand().getAmount() > 1) return;
		if(event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
		
		if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
			// verify we're not modifying a block using item
			if(event.getPlayer().getInventory().getItem(40) != null && 
					!event.getPlayer().getInventory().getItem(40).getType().isBlock()) return;
			
			plugin.getInventoryUtilities().searchAndReplace(event.getPlayer(), 40, event.getPlayer().getInventory().getItemInOffHand().getType(), false);
		} else if(event.getHand().equals(EquipmentSlot.HAND)) {
			// verify we're not modifying a block using item
			if(event.getPlayer().getInventory().getItemInMainHand() != null && 
					!event.getPlayer().getInventory().getItemInMainHand().getType().isBlock()) return;
			
			plugin.getInventoryUtilities().searchAndReplace(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot(), 
					event.getPlayer().getInventory().getItemInMainHand().getType(), true);
		}
	}
}
