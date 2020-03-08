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

public class RandomGameMusicItem extends ActionItem {
	private final Sound[] vanillaSongs;
	
	/**
	 * Initializes the random music player. If song is null, the song is selected from minecraft discs/music
	 * @param item
	 * @param song
	 */
	public RandomGameMusicItem(ItemStack item, Sound[] songs) {
		super(item);
		vanillaSongs = songs;
	}

	
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inventory, InventoryMenu<T> menu,
			InventoryClickEvent event) {

		// close all the other music
		if(!(event.getWhoClicked() instanceof Player)) return InventoryMenu.FAIL;
		Player clicker = (Player)event.getWhoClicked();
		Kurebox plugin = Kurebox.getPlugin(Kurebox.class);
		plugin.getMusicManager().stopCustomMusic(clicker);
		plugin.getMusicManager().stopDiscMusic(clicker);
		plugin.getMusicManager().stopGameMusic(clicker);
	
		if(vanillaSongs != null) {
			// play random song
			plugin.getMusicManager().playMusic(clicker, vanillaSongs[(int)(Math.random() * vanillaSongs.length)], 1);
		}
		
		return InventoryMenu.CLICK;
	}

}
