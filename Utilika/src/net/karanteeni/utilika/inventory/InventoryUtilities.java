package net.karanteeni.utilika.inventory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.utilika.Utilika;

public class InventoryUtilities {
	private static final String INVENTORY_TWEAKS_SOUND_PATH = "tweaks.sound";
	private static final String INVENTORY_TWEAKS_PITCH_PATH = "tweaks.pitch";
	private static final String INVENTORY_TWEAKS_VOLUME_PATH = "tweaks.volume";
	
	private static Sound INVENTORY_TWEAKS_SOUND = Sound.ENTITY_EXPERIENCE_BOTTLE_THROW;
	private static float INVENTORY_TWEAKS_PITCH = 0.5f;
	private static float INVENTORY_TWEAKS_VOLUME = 1.7f;
	
	public void registerConfig() {
		Utilika plugin = Utilika.getPlugin(Utilika.class);
		if(!plugin.getConfig().isSet(INVENTORY_TWEAKS_SOUND_PATH)) {
			plugin.getConfig().set(INVENTORY_TWEAKS_SOUND_PATH, INVENTORY_TWEAKS_SOUND.toString());
			plugin.saveConfig();
		}
		INVENTORY_TWEAKS_SOUND = Sound.valueOf(plugin.getConfig().getString(INVENTORY_TWEAKS_SOUND_PATH));
		
		if(!plugin.getConfig().isSet(INVENTORY_TWEAKS_PITCH_PATH)) {
			plugin.getConfig().set(INVENTORY_TWEAKS_PITCH_PATH, INVENTORY_TWEAKS_PITCH);
			plugin.saveConfig();
		}
		INVENTORY_TWEAKS_PITCH = (float)plugin.getConfig().getDouble(INVENTORY_TWEAKS_PITCH_PATH);
		
		if(!plugin.getConfig().isSet(INVENTORY_TWEAKS_VOLUME_PATH)) {
			plugin.getConfig().set(INVENTORY_TWEAKS_VOLUME_PATH, INVENTORY_TWEAKS_VOLUME);
			plugin.saveConfig();
		}
		INVENTORY_TWEAKS_VOLUME = (float)plugin.getConfig().getDouble(INVENTORY_TWEAKS_VOLUME_PATH);
	}
	
	/**
	 * Searches and replaces the item at the given slot with another item from inventory
	 * @param player player in whose inventory the iten replacement will happen
	 * @param slotId slot to replace
	 * @param material item to search for
	 */
	public void searchAndReplace(Player player, int slotId, Material material, boolean heldItemSlot) {
		if(slotId == -1) return;
		
		if(!player.hasPermission("utilika.easeinventory")) return;
		
		// loop all items in inventory
		for(int i = 0; i <= 40; ++i) {
			// if we're at the location to be replaced don't replace
			if(i == slotId) continue;
			
			// check if the items found are similar
			if(player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == material) {
				ItemStack replacementStack = player.getInventory().getItem(i);
				player.getInventory().setItem(i, null);
				if(heldItemSlot)
					player.getInventory().setItemInMainHand(replacementStack);
				else
					player.getInventory().setItem(slotId, replacementStack);
				playDisplaceSound(player);
				break;
			}
		}
	}
	
	
	/**
	 * Plays the item displaced sound for player when items are empty or breaks and is replaced
	 * @param player player to whom the sound is placed
	 */
	public void playDisplaceSound(Player player) {
		player.playSound(player.getLocation(),
				INVENTORY_TWEAKS_SOUND,
				INVENTORY_TWEAKS_PITCH,
				INVENTORY_TWEAKS_VOLUME);
	}
}
