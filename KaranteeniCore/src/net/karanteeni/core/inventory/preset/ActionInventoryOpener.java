package net.karanteeni.core.inventory.preset;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

/**
 * When selecting this this item will open a new inventory for the player with the given key.
 * If no inventory is found and error sound will be played
 * @author Nuubles
 *
 */
public class ActionInventoryOpener extends ActionItem {
	private final String inventoryKey;
	
	public ActionInventoryOpener(ItemStack item, boolean closeOnSelect, String inventoryKey) {
		super(item);
		this.inventoryKey = inventoryKey;
	}

	
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event) {
		// get the inventory to open
		if(!menu.hasInventory(inventoryKey))
			return InventoryMenu.FAIL;

		// open the new inventory
		menu.openInventory(inventoryKey);
		return InventoryMenu.OPEN_PAGE;
	}

}
