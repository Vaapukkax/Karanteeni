package net.karanteeni.core.inventory.preset;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniCore;

public class PresetActionItems {
	private static KaranteeniCore plugin;
	
	public static void initialize(KaranteeniCore plugin) {
		PresetActionItems.plugin = plugin;
	}
	
	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionAccept getAccept(Player player, boolean returnOnAccept, boolean enabled) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.accept");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.LIME_CONCRETE, 1), text);
		
		String text1 = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.accept-not-ready");
		ItemStack item1 = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.GRAY_CONCRETE, 1), text1);
		
		return new ActionAccept(item, item1, returnOnAccept, enabled);
	}
	
	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionAccept getAccept(Player player, boolean returnOnAccept, boolean enabled, String disabledText) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.accept");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.LIME_CONCRETE, 1), text);
		
		ItemStack item1 = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.GRAY_CONCRETE, 1), disabledText);
		
		return new ActionAccept(item, item1, returnOnAccept, enabled);
	}

	
	public static ActionAccept getAccept(Player player, boolean returnOnAccept, boolean enabled, String enabledText, String disabledText) {
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.LIME_CONCRETE, 1), enabledText);
		ItemStack item1 = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.GRAY_CONCRETE, 1), disabledText);
		return new ActionAccept(item, item1, returnOnAccept, enabled);
	}
	
	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionReturn getReturn(Player player) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.return");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.ARROW, 1), text);
		
		return new ActionReturn(item);
	}
	
	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionClose getClose(Player player) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.close");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.BARRIER, 1), text);
		
		return new ActionClose(item);
	}

	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionNextPage getNextPage(Player player) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.next_page");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.ENDER_EYE, 1), text);
		
		return new ActionNextPage(item);
	}
	
	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionPreviousPage getPreviousPage(Player player) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.previous_page");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.ENDER_PEARL, 1), text);
		
		return new ActionPreviousPage(item);
	}

	
	/**
	 * Returns the accept item to be used in inventorymenus
	 * @param player player to whom the accept button is forwarded to
	 * @param returnOnAccept should this button return on accept
	 * @return the generated action item
	 */
	public static ActionNoPermission getNoPermission(Player player) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.no_permission");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.GRAY_DYE, 1), text);
		
		return new ActionNoPermission(item);
	}
	
	
	/**
	 * Returns the default empty item to show to the player
	 * @param player player to show the empty item to
	 * @return the default empty item
	 */
	public static ItemStack getEmpty(Player player) {
		String text = KaranteeniCore.getTranslator().getTranslation(plugin, player, "INVENTORY.empty");
		ItemStack item = KaranteeniCore.getItemManager().setDisplayName(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), text);
		return item;
	}
}
