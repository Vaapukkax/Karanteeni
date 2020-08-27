package net.karanteeni.kurebox.inventories;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.kurebox.Kurebox;
import net.karanteeni.kurebox.inventoryitems.RandomGameMusicItem;
import net.karanteeni.kurebox.inventoryitems.VanillaMusicItem;

public class GameMusicMenu extends InventoryBase<Kurebox> {

	public GameMusicMenu(Kurebox plugin, 
			ItemStack emptyItem, 
			Player player,  
			String title) {
		super(plugin, emptyItem, player, InventoryType.CHEST, 6, true, title);
	}

	
	@Override
	public void onOpen() {
		fillItems();
	}
	
	
	@Override
	protected void fillItems() {
		setItem("icons.vanilla-music.menu", 		"game-music.menu", 			Sound.MUSIC_MENU, 		1, 1);
		setItem("icons.vanilla-music.survival", 	"game-music.survival", 		Sound.MUSIC_GAME, 		3, 1);
		setItem("icons.vanilla-music.underwater", 	"game-music.underwater", 	Sound.MUSIC_UNDER_WATER, 5, 1);
		setItem("icons.vanilla-music.dragon", 		"game-music.dragon", 		Sound.MUSIC_DRAGON, 	7, 1);
		setRandomMusicItem("icons.vanilla-music.nether", "game-music.nether", 
				new Sound[] {
					Sound.MUSIC_NETHER_BASALT_DELTAS,
					Sound.MUSIC_NETHER_CRIMSON_FOREST,
					Sound.MUSIC_NETHER_NETHER_WASTES,
					Sound.MUSIC_NETHER_SOUL_SAND_VALLEY,
					Sound.MUSIC_NETHER_WARPED_FOREST
				 }, 1, 3);
		setItem("icons.vanilla-music.creative", 	"game-music.creative", 		Sound.MUSIC_CREATIVE, 	3, 3);
		setItem("icons.vanilla-music.end", 			"game-music.end", 			Sound.MUSIC_END, 		5, 3);
		setItem("icons.vanilla-music.end-screen", 	"game-music.end-screen", 	Sound.MUSIC_CREDITS, 	7, 3);
		// back button
		this.setAction(PresetActionItems.getReturn(player), 0, 5);
		
		// close button
		this.setAction(PresetActionItems.getClose(player), 8, 5);
	}
	
	
	/**
	 * Sets individial action item
	 * @param material
	 * @param nameKey
	 * @param music
	 * @param x
	 * @param y
	 */
	private void setItem(String material, String nameKey, Sound music, int x, int y) {
		Material loadedMaterial = Material.valueOf(plugin.getConfig().getString(material));
		this.setAction(new VanillaMusicItem(
				renameItem(new ItemStack(loadedMaterial, 1), nameKey), 
				music), x, y);
	}
	
	private void setRandomMusicItem(String material, String nameKey, Sound[] music, int x, int y) {
		Material loadedMaterial = Material.valueOf(plugin.getConfig().getString(material));
		this.setAction(new RandomGameMusicItem(
				renameItem(new ItemStack(loadedMaterial, 1), nameKey), 
				music), x, y);
	}
	
	/**
	 * Renames the given item with a translation found with the given key
	 * @param item
	 * @param key
	 * @return
	 */
	private ItemStack renameItem(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Kurebox.getTranslator().getTranslation(plugin, player, key));
		item.setItemMeta(meta);
		return item;
	}
}
