package net.karanteeni.core.inventory.preset;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ActionToggle extends ActionItem {
	private boolean usesGlow;
	private ItemStack originalOn = null;
	private ItemStack originalOff = null;
	private ItemStack on = null;
	private ItemStack off = null;
	private String dataKey = null;
	private boolean active;
	
	/**
	 * Initializes the toggle item
	 * @param item item to set as toggleable
	 * @param on is the item on or off
	 * @param key key to store the toggle state
	 */
	public ActionToggle(ItemStack item, boolean on, String key) {
		super(item);
		this.usesGlow = true; // no other items given so this will use glow
		this.setGlowing(on);
		this.dataKey = key;
		this.on = item;
		this.active = on;
		this.originalOn = item;
	}

	
	/**
	 * Initializes the toggle with two items and no glow
	 * @param onItem item to represent the ON state
	 * @param offItem item to represent the OFF state
	 * @param on should this be on or off by default
	 * @param key key to store the value to the menu
	 */
	public ActionToggle(ItemStack onItem, ItemStack offItem, boolean on, String key) {
		super(on?onItem:offItem);
		this.usesGlow = false; // items will be representing the state
		this.dataKey = key;
		this.off = offItem;
		this.on = onItem;
		this.active = on;
		this.off = offItem;
		this.on = onItem;
		this.originalOn = onItem;
		this.originalOff = offItem;
	}

	
	/**
	 * Initializes the toggle item. No data will be set to the menu
	 * @param item item to set as toggleable
	 * @param on is the item on or off
	 * @param key key to store the toggle state
	 */
	public ActionToggle(ItemStack item, boolean on) {
		super(item);
		this.usesGlow = true; // no other items given so this will use glow
		this.setGlowing(on);
		this.on = item;
		this.active = on;
		this.originalOn = item;
	}

	
	/**
	 * Initializes the toggle with two items and no glow. No data will be set to the menu
	 * @param onItem item to represent the ON state
	 * @param offItem item to represent the OFF state
	 * @param on should this be on or off by default
	 */
	public ActionToggle(ItemStack onItem, ItemStack offItem, boolean on) {
		super(on?onItem:offItem);
		this.usesGlow = false; // items will be representing the state
		this.active = on;
		
		this.off = offItem;
		this.on = onItem;
		this.originalOn = onItem;
		this.originalOff = offItem;
	}
	
	
	/**
	 * Returns the original active item
	 */
	@Override
	public ItemStack getItem() {
		return this.originalOn;
	}
	
	
	/**
	 * Returns the original inactive item
	 * @return
	 */
	public ItemStack getOffItem() {
		return this.originalOff;
	}
	
	
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event) {
		// will we be using glow effects as toggle OR items
		if(usesGlow) {
			this.active = !this.active;
			// toggle the glow
			setGlowing(this.active, inventory.getInventory(), event.getSlot());
			// store the data
			if(dataKey != null)
				menu.setObject(this.dataKey, this.active);
		} else {
			this.active = !active;
			// toggle the item
			this.setItemStack(this.active?this.on:this.off);
			// store the data
			if(dataKey != null)
				menu.setObject(this.dataKey, this.active);
		}
		
		return InventoryMenu.CLICK;
	}
	
	
	/**
	 * Sets the toggler either enabled or disabled. DOES NOT STORE THE DATA TO HOLDER!
	 * @param enabled the state to which this toggler will be set to
	 */
	public void setEnabled(boolean enabled) {
		// will we be using glow effects as toggle OR items
		if(usesGlow) {
			this.active = enabled;
			// toggle the glow
			setGlowing(this.active);
		} else {
			this.active = enabled;
			// toggle the item
			this.setItemStack(this.active?this.on:this.off);
		}
	}
	
	
	/**
	 * The true full form of set enabled, will always work. Do not use if item is hidden and should not become visible
	 * @param inventory inventory the is in
	 * @param menu menu to store the data to
	 * @param slot slot the item is in
	 */
	public <T extends KaranteeniPlugin> void setEnabled(InventoryBase<T> inventory, InventoryMenu<T> menu, int slot, boolean enabled) {
		// will we be using glow effects as toggle OR items
		this.active = enabled;
		if(usesGlow) {
			// toggle the glow
			setGlowing(this.active, inventory.getInventory(), slot);
			// store the data
			if(dataKey != null)
				menu.setObject(this.dataKey, this.active);
		} else {
			// toggle the item
			this.setItemStack(this.active?this.on:this.off);
			// store the data
			if(dataKey != null)
				menu.setObject(this.dataKey, this.active);
		}
	}
	
	
	/**
	 * Sets the toggler either enabled or disabled. DOES NOT STORE THE DATA TO HOLDER!
	 * @param enabled the state to which this toggler will be set to
	 */
	public <T extends KaranteeniPlugin> void setEnabled(InventoryMenu<T> menu, boolean enabled) {
		// will we be using glow effects as toggle OR items
		if(usesGlow) {
			this.active = enabled;
			// toggle the glow
			setGlowing(this.active);
		} else {
			this.active = enabled;
			// toggle the item
			this.setItemStack(this.active?this.on:this.off);
		}
		// store the data
		if(dataKey != null)
			menu.setObject(this.dataKey, this.active);
	}
	
	
	/**
	 * Check whether this toggler is on or off
	 * @return true if on, false if off
	 */
	public boolean isEnabled() {
		return this.active;
	}
}
