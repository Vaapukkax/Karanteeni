package net.karanteeni.kurebox.inventoryitems;

import java.util.List;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.kurebox.Kurebox;

public class RandomSongMusicItem extends ActionItem {
	private final List<Song> songs;
	
	/**
	 * Initializes the random music player. If song is null, the song is selected from minecraft discs/music
	 * @param item
	 * @param song
	 */
	public RandomSongMusicItem(ItemStack item, List<Song> songs) {
		super(item);
		this.songs = songs;
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
		Random r = new Random(System.currentTimeMillis());
	
		if(songs != null) {
			// play random song
			plugin.getMusicManager().playSong(clicker, songs.get(r.nextInt(songs.size())));
		}
		
		return InventoryMenu.CLICK;
	}

}
