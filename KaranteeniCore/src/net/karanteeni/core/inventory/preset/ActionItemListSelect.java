package net.karanteeni.core.inventory.preset;

import java.util.Queue;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

/**
 * Works in a similiar way to toggler. Does not return or change inventory state except
 * changes the item in this item position according to the queue given 
 * @author Nuubles
 *
 */
public class ActionItemListSelect extends ActionItem {
	// queue of items
	Queue<ItemStack> items;
	
	public ActionItemListSelect(Queue<ItemStack> items) {
		super(items.peek());
		this.items = items;
	}
	

	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event) {
		// loop the items
		ItemStack itemStack = items.poll();
		this.setItemStack(itemStack);
		items.add(itemStack);
		
		// return the changed item
		return InventoryMenu.CHANGE;
	}

	
	/**
	 * Returns the selected item
	 * @return current selected item
	 */
	public ItemStack getSelected() {
		// get and return the first item in the queue
		return items.peek();
	}
}
