package net.karanteeni.randomitems.items;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.randomitems.RandomItems;

public class FortuneCookie {
	
	public static ShapelessRecipe getRecipe() {
		ItemStack cookie = new ItemStack(Material.COOKIE, 1);
		ItemMeta meta = cookie.getItemMeta();
		meta.setLore(Arrays.asList("§6Fortune cookie"));
		cookie.setItemMeta(meta);
		cookie = RandomItems.getItemManager().addGlow(cookie);
		NamespacedKey cookieKey = new NamespacedKey(RandomItems.getPlugin(RandomItems.class), "fortune_cookie");
		
		ShapelessRecipe cookieRecipe = new ShapelessRecipe(cookieKey, cookie);
		cookieRecipe.addIngredient(2, Material.WHEAT);
		cookieRecipe.addIngredient(1, Material.PAPER);
		cookieRecipe.addIngredient(1, Material.COCOA_BEANS);
		return cookieRecipe;
	}
	
	
	public static boolean isFortuneCookie(ItemStack itemStack) {
		List<String> lore = itemStack.getItemMeta().getLore();
		if(lore == null || lore.isEmpty()) {
			return false;
		} else {
			return lore.get(0).equals("§6Fortune cookie");
		}
	}
}
