package net.karanteeni.core.inventory.preset;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ActionSelect extends ActionItem {
	private final String key;
	private final boolean returnOnSelect;
	
	/**
	 * Initializes the action selector.
	 * @param item item to select
	 * @param key key to store the selected item to
	 * @param returnOnSelect return to the previous menu when selecting
	 */
	public ActionSelect(ItemStack item, String key, boolean returnOnSelect) {
		super(item); 
		this.key = key;
		this.returnOnSelect = returnOnSelect;
	}

	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event) {
		menu.setObject(key, this.getItem());
		return returnOnSelect ? InventoryMenu.RETURN_RETURN : InventoryMenu.CLICK;
	}

}
