package net.karanteeni.kurebox.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.kurebox.Kurebox;
import net.karanteeni.kurebox.inventories.GameMusicMenu;
import net.karanteeni.kurebox.inventories.InventoryManager;
import net.karanteeni.kurebox.inventories.InventoryMultiPage;
import net.karanteeni.kurebox.inventories.MusicMenu;
import net.karanteeni.kurebox.inventoryitems.CustomMusicItem;
import net.karanteeni.kurebox.inventoryitems.VanillaMusicItem;

public class Music extends CommandChainer implements TranslationContainer {

	public Music(Kurebox plugin) {
		super(plugin, 
				"music", 
				"/music", 
				"Opens music player menu", 
				Kurebox.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		
		// generate inventories
		
		// main inventory
		MusicMenu mainInventory = new MusicMenu((Kurebox)plugin, 
				PresetActionItems.getEmpty(player), 
				player, 
				Kurebox.getTranslator().getTranslation(plugin, player, "menu.main"));
		
		// inventory for minecraft music
		GameMusicMenu minecraftInventory = new GameMusicMenu((Kurebox)plugin, 
				PresetActionItems.getEmpty(player), 
				player, 
				Kurebox.getTranslator().getTranslation(plugin, player, "menu.minecraft"));
		
		// inventory for minecraft discs
		InventoryMultiPage<Kurebox> discInventory = new InventoryMultiPage<Kurebox>(
				(Kurebox)plugin,
				PresetActionItems.getEmpty(player),
				player,
				true,
				Kurebox.getTranslator().getTranslation(plugin, player, "menu.discs"));
		// minecraft disc items
		Collection<ActionItem> discItems = new LinkedList<ActionItem>(Arrays.asList(
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_11, 1), Sound.MUSIC_DISC_11),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_13, 1), Sound.MUSIC_DISC_13),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_BLOCKS, 1), Sound.MUSIC_DISC_BLOCKS),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_CAT, 1), Sound.MUSIC_DISC_CAT),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_CHIRP, 1), Sound.MUSIC_DISC_CHIRP),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_FAR, 1), Sound.MUSIC_DISC_FAR),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_MALL, 1), Sound.MUSIC_DISC_MALL),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_MELLOHI, 1), Sound.MUSIC_DISC_MELLOHI),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_STAL, 1), Sound.MUSIC_DISC_STAL),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_STRAD, 1), Sound.MUSIC_DISC_STRAD),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_WAIT, 1), Sound.MUSIC_DISC_WAIT),
				new VanillaMusicItem(new ItemStack(Material.MUSIC_DISC_WARD, 1), Sound.MUSIC_DISC_WARD))); 
		discInventory.setSelectable(discItems);
		
		// inventory for server music
		InventoryMultiPage<Kurebox> serverInventory = new InventoryMultiPage<Kurebox>(
				(Kurebox)plugin,
				PresetActionItems.getEmpty(player),
				player,
				true,
				Kurebox.getTranslator().getTranslation(plugin, player, "menu.server"));
		Collection<ActionItem> serverMusics = new LinkedList<ActionItem>();
		String songNameTemplate = Kurebox.getTranslator().getTranslation(plugin, player, "server-music-name");
		
		// collect all music icons
		ArrayList<Material> iconMaterials = new ArrayList<Material>();
		for(String iconMaterial : plugin.getConfig().getStringList("icons.song-icons"))
			iconMaterials.add(Material.valueOf(iconMaterial));
		
		// loop and convert all songs into items
		int i = 0;
		for(Song song : ((Kurebox)plugin).getMusicManager().getSongs()) {
			String songName = null;
			if(song.getTitle() != null && !song.getTitle().equals("")) {
				songName = songNameTemplate.replace("%song%", song.getTitle());
			} else {
				songName = songNameTemplate.replace("%song%", song.getPath().getName().replaceAll(".nbs", ""));
			}
			
			ItemStack songIcon = new ItemStack(iconMaterials.get(i), 1);
			ItemMeta meta = songIcon.getItemMeta();
			meta.setDisplayName(songName);
			songIcon.setItemMeta(meta);
			
			// get the icon for item
			serverMusics.add(new CustomMusicItem(songIcon, song));
			
			if(iconMaterials.size() == ++i)
				i = 0;
		}
		
		// store items(songs) into server music menu
		serverInventory.setSelectable(serverMusics);
		
		// register inventories
		InventoryManager invManager = new InventoryManager(plugin, player, mainInventory);
		invManager.addInventory(discInventory, "kurebox:disc");
		invManager.addInventory(serverInventory, "kurebox:cmusic");
		invManager.addInventory(minecraftInventory, "kurebox:vmusic");
		
		invManager.openInventory("main");
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Kurebox.getTranslator().registerTranslation(plugin, "menu.main", "§6Music menu");
		Kurebox.getTranslator().registerTranslation(plugin, "menu.discs", "§6Minecraft discs");
		Kurebox.getTranslator().registerTranslation(plugin, "menu.server", "§6Server music");
		Kurebox.getTranslator().registerTranslation(plugin, "menu.minecraft", "§6Minecraft music");
		Kurebox.getTranslator().registerTranslation(plugin, "server-music-name", "§e%song%");
	}
}
