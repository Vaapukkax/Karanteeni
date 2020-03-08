package net.karanteeni.kurebox.inventoryitems;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class VanillaMusicMenu extends ActionItem {
	
	public VanillaMusicMenu(ItemStack item) {
		super(item);		
	}

	
	/**
	 * {@inheritDoc}
	 * @param inventory
	 * @param menu
	 * @param event
	 * @return
	 */
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(
			InventoryBase<T> inventory, 
			InventoryMenu<T> menu,
			InventoryClickEvent event) {
		
		// close all the other music
		if(!(event.getWhoClicked() instanceof Player)) return InventoryMenu.FAIL;
		
		menu.openInventory("kurebox:vmusic");
		
		return InventoryMenu.CLICK;
	}
}
