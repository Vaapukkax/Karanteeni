package net.karanteeni.karanteenials.player.misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.MaterialLoader;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class ClearInventoryCommand extends CommandChainer implements TranslationContainer {

	public ClearInventoryCommand() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"clear", 
				"/clear [<player> [<material> [<count>]]]", 
				"Clears players inventory of given materials", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "clear.self", "Cleared items from your inventory");
		Karanteenials.getTranslator().registerTranslation(plugin, "clear.other", "Cleared items for [%players%]");
		Karanteenials.getTranslator().registerTranslation(plugin, "clear.material-not-found", "Could not find material(s) with %materials%");
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length <= 2) {
			return this.defaultAutofill(sender, cmd, label, args);
		} else if(args.length == 3) {
			return Arrays.asList("1", "10", "16", "32", "64", "128", "256", "512", "1024", "2048");
		}
		
		return null;
	}
	
	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		List<Player> players = this.getObject(PlayerLoader.PLAYER_KEY_MULTIPLE);
		List<Material> materials = null; // materials to remove
		int clearCount = Integer.MAX_VALUE; // item count to be removed
		
		// materials have been set
		if(args.length > 1 && this.hasData(MaterialLoader.MATERIAL_KEY_MULTIPLE)) {
			materials = this.getObject(MaterialLoader.MATERIAL_KEY_MULTIPLE);
		}
		
		// a count has been set
		if(args.length == 3) {
			try {
				clearCount = Integer.parseInt(args[2]);
			} catch(NumberFormatException e) {
				return CommandResult.INVALID_ARGUMENTS; // add a invalid number message
			}
		}
		
		if(args.length == 0) { // clear only own inventory
			if(!(sender instanceof Player)) // if self clear was by console, prevent it
				return CommandResult.NOT_FOR_CONSOLE;
			((Player)sender).getInventory().clear();
			((Player)sender).updateInventory();
			Karanteenials.getMessager().sendMessage(sender, 
					Sounds.FIREWORK_LOW.get(), 
					Prefix.NEUTRAL + 
					Karanteenials.getTranslator().getTranslation(plugin, sender, "clear.self"));
			return CommandResult.SUCCESS;
		}
		
		
		// clearing multiple inventories
		if(players.size() == 0) { // no players found, give error
			return new CommandResult(Karanteenials.getDefaultMsgs().playerNotFound(sender, args[0]), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		} else if(materials != null && materials.size() == 0) { // no materials found, give error
			return new CommandResult(Karanteenials.getTranslator().getTranslation(plugin, sender, "clear.material-not-found").replace("%materials%", args[1]), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		clearItems(players, materials, clearCount);
		
		// send the result message
		Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL +
				Karanteenials.getTranslator().getTranslation(plugin, sender, "clear.other")
				.replace("%players%", ArrayFormat.join(ArrayFormat.playersToArray(players), ", ")));
		
		return CommandResult.SUCCESS;
	}
		
	
	/**
	 * Clears items from given players, given items and a given count
	 * @param players players to clear from
	 * @param materials materials to clear. if null then clear all types and all
	 * @param count count of materials to clear
	 */
	private void clearItems(List<Player> players, List<Material> materials, int count) {
		if(materials == null || materials.isEmpty()) { // clear all materials
			for(Player player : players) {
				player.getInventory().clear();
				player.updateInventory();
			}
		} else {
			if(count == Integer.MAX_VALUE) { // clear all of a given type
				for(Player player : players)
				for(Material mat : materials)
				player.getInventory().remove(mat);
			} else { // clear an amount of a given type
				for(Player player : players) { // loop all players					
					Inventory inventory = player.getInventory();
					for(Material mat : materials) { // loop all materials
						int clearCount = count;
						HashMap<Integer, ? extends ItemStack> items = inventory.all(mat);
						for(Entry<Integer, ? extends ItemStack> entry : items.entrySet()) {							
							ItemStack item = inventory.getItem(entry.getKey());
							int itemCount = item.getAmount();
							if(clearCount >= itemCount)
								inventory.setItem(entry.getKey(), null);
							else
								item.setAmount(item.getAmount()-clearCount);
							clearCount -= itemCount;
							if(clearCount <= 0) break;
						}
					}

					player.updateInventory();
				}
			}
		}
	}
}
