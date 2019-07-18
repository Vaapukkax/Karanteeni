package net.karanteeni.core.inventory;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.core.KaranteeniPlugin;

public abstract class ActionItem {
	private ItemStack item;
	
	public ActionItem(ItemStack item) {
		this.item = item;
	}
	
	
	/**
	 * Returns the item at this action slot
	 * @return
	 */
	public ItemStack getItem() {
		return this.item;
	}
	
	
	/**
	 * Sets the name of this item to something else
	 * @param name new name for the item
	 */
	public ActionItem setItemName(String name) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setDisplayName(name);
		this.item.setItemMeta(meta);
		return this;
	}
	
	
	/**
	 * Sets the lore for this item
	 * @param lore new lore for the item
	 * @return self
	 */
	public ActionItem setItemLore(List<String> lore) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setLore(lore);
		return this;
	}
	
	
	/**
	 * Change the type of the item
	 * @param type new type for item
	 * @return this
	 */
	public ActionItem setType(Material type) {
		this.item.setType(type);
		return this;
	}
	
	
	/**
	 * Sets the itemstack of this item. NOTE! Will not take the actual itemstack, just modifies the existing stack
	 * @param itemStack
	 */
	public void setItemStack(ItemStack itemStack) {
		ItemMeta meta = itemStack.getItemMeta();
		//Material type = itemStack.getType();
		this.item.setItemMeta(meta);
	}
	
	
	/**
	 * Makes an item glow
	 * @param glowing true to make item glow, false to make it not glow
	 * @return this
	 */
	public ActionItem setGlowing(boolean glowing, Inventory inventory, int slot) {
		/*net.minecraft.server.v1_13_R2.ItemStack nmsStack = (net.minecraft.server.v1_13_R2.ItemStack) getField(item, "handle");
		NBTTagCompound compound = nmsStack.getTag();
		
		// initialize the compound if we need to
		if (compound == null) {
			compound = new NBTTagCompound();
			nmsStack.setTag(compound);
		}
		
		// empty enchanting compound
		if(glowing)
			compound.set("ench", new NBTTagList());
		else
			compound.remove("ench");*/
		
		if(glowing) {
			ItemMeta meta = this.item.getItemMeta();
			meta.addEnchant(Enchantment.LUCK, 1, false);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			this.item.setItemMeta(meta);
		} else {
			ItemMeta meta = this.item.getItemMeta();
			meta.removeEnchant(Enchantment.LUCK);
			meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			this.item.setItemMeta(meta);
		}
		inventory.setItem(slot, item);
		
		/*if(glowing)
			KaranteeniCore.getItemManager().addGlow(this.item);
		else
			KaranteeniCore.getItemManager().removeGlow(this.item);*/
		return this;
	}
	
	
	/**
	 * Makes an item glow. Does not update visible items
	 * @param glowing true to make item glow, false to make it not glow
	 * @return this
	 */
	public ActionItem setGlowing(boolean glowing) {		
		if(glowing) {
			ItemMeta meta = this.item.getItemMeta();
			meta.addEnchant(Enchantment.LUCK, 1, false);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			this.item.setItemMeta(meta);
		} else {
			ItemMeta meta = this.item.getItemMeta();
			meta.removeEnchant(Enchantment.LUCK);
			meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			this.item.setItemMeta(meta);
		}

		return this;
	}
	
	
	/*private Object getField(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			throw new RuntimeException("Unable to retrieve field content.", e);
		}
	}*/
	
	
	/**
	 * Returns true if the actionitem is glowing, false otherwise
	 * @return true if glowing, false if not
	 */
	/*public boolean isGlowing() {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = (net.minecraft.server.v1_13_R2.ItemStack) getField(item, "handle");
		return nmsStack.getTag().hasKey("ench");
	}*/
	
	/**
	 * Called when this itemstack slot is called
	 * @param inventory
	 * @param menu
	 * @param event
	 * @return
	 */
	public abstract <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu, InventoryClickEvent event);
}
