package net.karanteeni.core.inventory.preset;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ActionAccept extends ActionItem {
	private boolean returnOnAccept;
	private boolean enabled;
	private ItemStack enabledItem;
	private ItemStack disabledItem;
	
	public ActionAccept(ItemStack enabledItem, ItemStack disabledItem, boolean returnOnAccept, boolean enabled) {
		super(enabled ? enabledItem : disabledItem);
		this.returnOnAccept = returnOnAccept;
		this.enabled = enabled;
	}

	
	/**
	 * Can this button be clicked eg. is this enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
	
	
	/**
	 * Sets this accept button as enabled or disabled
	 * @param enabled true to enable this button, false to disable
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if(enabled)
			this.setItemStack(enabledItem);
		else
			this.setItemStack(disabledItem);
	}
	
	
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event) {
		// if the button is disabled, don't do anything
		if(!enabled) return InventoryMenu.FAIL;
		return returnOnAccept ? InventoryMenu.SUCCESS_RETURN : InventoryMenu.SUCCESS;
	}
}
