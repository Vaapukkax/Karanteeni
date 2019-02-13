package net.karanteeni.core.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	private ItemType itemType = new ItemType();
	
	/**
	 * Adds a glow effect to an item
	 * @param item
	 * @return
	 */
	public ItemStack addGlow(ItemStack item)
	{
		if(item.getEnchantments().isEmpty())
		{
			//Does not contain enchantments
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		
		return item;
	}
	
	/**
	 * removes the glow from item
	 * @param item
	 * @return
	 */
	public ItemStack removeGlow(ItemStack item)
	{
		if(!item.getEnchantments().isEmpty())
		{
			item.removeEnchantment(Enchantment.DURABILITY);
			item.getItemMeta().removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		return item;
	}
	
	/**
	 * Returns the itemtypes
	 * @return
	 */
	public ItemType getItemTypes()
	{
		return itemType;
	}
}
