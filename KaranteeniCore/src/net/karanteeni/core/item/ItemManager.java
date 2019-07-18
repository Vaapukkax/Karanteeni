package net.karanteeni.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	private ItemType itemType = new ItemType();
	
	/**
	 * Sets the displayname of a given item
	 * @param item item to set the name to
	 * @param name name to give to the item
	 * @return the item with changed name
	 */
	public ItemStack setDisplayName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	
	/**
	 * Adds a glow effect to an item
	 * @param item
	 * @return
	 */
	@Deprecated
	public ItemStack addGlow(ItemStack item) {
		if(item.getEnchantments().isEmpty()) {
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
	@Deprecated
	public ItemStack removeGlow(ItemStack item) {
		if(!item.getEnchantments().isEmpty()) {
			item.removeEnchantment(Enchantment.DURABILITY);
			item.getItemMeta().removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		return item;
	}
	
	
	/**
	 * Converts a collection of materials into a list of itemstacks. Each itemstack will have exactly 1 item
	 * @param materials materials to convert to itemstacks
	 * @return materials as itemstacks
	 */
	public List<ItemStack> convertMaterialToItemStack(Collection<Material> materials) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(Material material : materials)
			list.add(new ItemStack(material, 1));
		return list;
	}
	
	
	/**
	 * Returns the itemtypes
	 * @return
	 */
	public ItemType getItemTypes() {
		return itemType;
	}
}
