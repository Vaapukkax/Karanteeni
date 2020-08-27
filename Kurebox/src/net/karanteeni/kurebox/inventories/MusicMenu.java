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
import net.karanteeni.kurebox.inventoryitems.CustomMusicMenu;
import net.karanteeni.kurebox.inventoryitems.RadioMusicItem;
import net.karanteeni.kurebox.inventoryitems.RandomGameMusicItem;
import net.karanteeni.kurebox.inventoryitems.RandomSongMusicItem;
import net.karanteeni.kurebox.inventoryitems.StopMusicItem;
import net.karanteeni.kurebox.inventoryitems.VanillaDiscMenu;
import net.karanteeni.kurebox.inventoryitems.VanillaMusicMenu;

public class MusicMenu extends InventoryBase<Kurebox> {

	public MusicMenu(Kurebox plugin, 
			ItemStack emptyItem, 
			Player player,
			String title) {
		super(plugin, 
				PresetActionItems.getEmpty(player), 
				player, 
				InventoryType.CHEST, 
				6, 
				true, 
				title);
	}

	
	@Override
	public void onOpen() {
		fillItems();
	}

	
	@Override
	protected void fillItems() {
		Material radioIcon 		= Material.valueOf(plugin.getConfig().getString("icons.radio"));
		Material random 		= Material.valueOf(plugin.getConfig().getString("icons.random"));
		Material vanillaSongs 	= Material.valueOf(plugin.getConfig().getString("icons.vanilla-music-menu"));
		Material vanillaDiscs 	= Material.valueOf(plugin.getConfig().getString("icons.vanilla-discs-menu"));
		Material customSongs 	= Material.valueOf(plugin.getConfig().getString("icons.server-music-menu"));
		Material stopMusic		= Material.valueOf(plugin.getConfig().getString("icons.stop"));
		
		// radio
		this.setAction(new RadioMusicItem(
				setItemName(new ItemStack(radioIcon, 1), "icon.radio")), 4, 1);
		
		// inventory openers
		this.setAction(new VanillaDiscMenu(
				setItemName(new ItemStack(vanillaDiscs, 1), "icon.vanilla-discs")), 1, 2);
		this.setAction(new CustomMusicMenu(
				setItemName(new ItemStack(customSongs, 1), "icon.custom-music")), 4, 2);
		this.setAction(new VanillaMusicMenu(
				setItemName(new ItemStack(vanillaSongs, 1), "icon.vanilla-music")), 7, 2);

		// random players
		this.setAction(new RandomGameMusicItem(
				setItemName(new ItemStack(random, 1), "icon.random"), new Sound[] {
			Sound.MUSIC_DISC_11,
			Sound.MUSIC_DISC_13,
			Sound.MUSIC_DISC_BLOCKS,
			Sound.MUSIC_DISC_CAT,
			Sound.MUSIC_DISC_CHIRP,
			Sound.MUSIC_DISC_FAR,
			Sound.MUSIC_DISC_MALL,
			Sound.MUSIC_DISC_MELLOHI,
			Sound.MUSIC_DISC_STAL,
			Sound.MUSIC_DISC_STRAD,
			Sound.MUSIC_DISC_WAIT,
			Sound.MUSIC_DISC_WARD
		}), 1, 3);
		this.setAction(new RandomSongMusicItem(
				setItemName(new ItemStack(random, 1), "icon.random"), plugin.getMusicManager().getSongs()), 4, 3);
		this.setAction(new RandomGameMusicItem(
				setItemName(new ItemStack(random, 1), "icon.random"), new Sound[] {
			Sound.MUSIC_CREATIVE,
			Sound.MUSIC_CREDITS,
			Sound.MUSIC_DRAGON,
			Sound.MUSIC_END,
			Sound.MUSIC_GAME,
			Sound.MUSIC_MENU,
			Sound.MUSIC_NETHER_BASALT_DELTAS, // TODO fix nether music odds
			Sound.MUSIC_NETHER_CRIMSON_FOREST,
			Sound.MUSIC_NETHER_NETHER_WASTES,
			Sound.MUSIC_NETHER_SOUL_SAND_VALLEY,
			Sound.MUSIC_NETHER_WARPED_FOREST,
			Sound.MUSIC_UNDER_WATER
		}), 7, 3);
		
		// back button
		this.setAction(PresetActionItems.getReturn(player), 0, 5);
		
		// stop button
		this.setAction(new StopMusicItem(
				setItemName(new ItemStack(stopMusic, 1), "icon.stop-music")), 4, 5);
		
		// close button
		this.setAction(PresetActionItems.getClose(player), 8, 5);
	}
	
	
	/**
	 * Finds a translation with the given key and sets it as the items name
	 * @param item
	 * @param translationKey
	 */
	private ItemStack setItemName(ItemStack item, String translationKey) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Kurebox.getTranslator().getTranslation(plugin, player, translationKey));
		item.setItemMeta(meta);
		return item;
	}
}
