package net.karanteeni.core.inventory.preset;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ActionPreviousPage extends ActionItem {

	public ActionPreviousPage(ItemStack item) {
		super(item);
	}

	
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event) {
		return InventoryMenu.PREVIOUS_PAGE;
	}

}
