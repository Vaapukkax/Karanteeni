package net.karanteeni.kurebox.inventoryitems;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.kurebox.Kurebox;

public class VanillaMusicItem extends ActionItem {
	private Sound music;
	
	public VanillaMusicItem(ItemStack item, Sound music) {
		super(item);
		this.music = music;
	}

	
	/**
	 * {@inheritDoc}
	 * @param inventory
	 * @param menu
	 * @param event
	 * @return
	 */
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(
			InventoryBase<T> inventory, 
			InventoryMenu<T> menu,
			InventoryClickEvent event) {
		
		// close all the other music
		if(!(event.getWhoClicked() instanceof Player)) return InventoryMenu.FAIL;
		Player clicker = (Player)event.getWhoClicked();
		Kurebox plugin = Kurebox.getPlugin(Kurebox.class);
		plugin.getMusicManager().stopCustomMusic(clicker);
		plugin.getMusicManager().stopDiscMusic(clicker);
		plugin.getMusicManager().stopGameMusic(clicker);
		
		// play the music stored to this item
		plugin.getMusicManager().playMusic(clicker, music, 1.0f);
		
		return InventoryMenu.CLICK;
	}
}
