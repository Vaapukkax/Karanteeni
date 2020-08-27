package net.karanteeni.karpet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareBinaryComponent;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.command.bare.BarePlayerComponent;
import net.karanteeni.core.command.defaultcomponent.BinaryLoader.BINARY;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.inventory.InventoryList;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.karpet.menu.CarpetCustomiserMenu;
import net.karanteeni.karpet.menu.InventoryEditor;

public class CarpetCommand extends BareCommand implements TranslationContainer {
	private CarpetHandler handler;
	BareBinaryComponent binary = new BareBinaryComponent(BINARY.ON_OFF);
	
	public CarpetCommand(CarpetHandler handler) {
		super(Karpet.getPlugin(Karpet.class), 
				"carpet", 
				"/carpet [on/off] [<player>]", 
				"Toggle and edit flying carpets of players", 
				Karpet.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
		this.handler = handler;
		CarpetCustomiserMenu.registerTranslations();
	}
	

	@Override
	public void registerTranslations() {
		Karpet.getTranslator().registerRandomTranslation(plugin, 
				"carpet.enabled", 
				"A magical carpet spawns below your feet");
		Karpet.getTranslator().registerRandomTranslation(plugin, 
				"carpet.disabled", 
				"Your carpet flew to its faraway home");
		Karpet.getTranslator().registerTranslation(plugin, 
				"carpet.reset", 
				"Your carpet has been reset");
		Karpet.getTranslator().registerTranslation(plugin, 
				"carpet.reset-others", 
				"You resetted the carpet of [%players%]");
		Karpet.getTranslator().registerTranslation(plugin, 
				"carpet.disabled-others", 
				"You removed carpet from [%players%]");
		Karpet.getTranslator().registerTranslation(plugin, 
				"carpet.enabled-others", 
				"You gave a carpet to [%players%]");
		Karpet.getTranslator().registerTranslation(plugin, 
				"carpet.cannot-enable-here", 
				"Magic carpet cannot be enabled here");
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			// autofill for on/off binary
			List<String> autoFill = binary.autofill(sender, cmd, label, args[0]);
			autoFill.add("design");
			autoFill.add("reset");
			return filterByPrefix(autoFill, args[0], false);
			
		} else if(args[0].equalsIgnoreCase("design")) {
			return null; // cannot design for others
		} else if(args[0].equalsIgnoreCase("reset")) {
			if(sender.hasPermission("reset-others")) {
				// suggest players
				BarePlayerComponent component = new BarePlayerComponent(false);
				return component.autofill(sender, cmd, label, args[1]);
			}
		} else { // on / off
			if(sender.hasPermission("change-others")) {
				// suggest players
				BarePlayerComponent component = new BarePlayerComponent(false);
				return component.autofill(sender, cmd, label, args[1]);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Can the carpet be enabled at the given location
	 * @param location location where the carpet should be enabled
	 * @return true if carpet can be enabled, false otherwise
	 */
	private boolean canEnable(Location location) {
		if(handler.getFlagManager() != null)
			return handler.getFlagManager().isCarpetAllowed(location);
		return true;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) { // toggle self///////////////////////////////////////////////////
			// allow only players and check permission
			if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
			if(!sender.hasPermission("karpet.use")) return CommandResult.NO_PERMISSION;
			Player player = (Player)sender;
			
			// check if the carpet can be enabled at this location
			if(!canEnable(player.getLocation())) {
				return new CommandResult(Karpet.getTranslator().getTranslation(this.plugin, player, "carpet.cannot-enable-here"),
						ResultType.NO_PERMISSION, Sounds.NO.get());
			}
			
			// toggle carpet
			if(handler.hasCarpet(player)) {
				handler.removeCarpet(player);
				sendCarpetMessage(player, false);
			} else {
				handler.addCarpet(player);
				sendCarpetMessage(player, true);
			}
		} else if(args.length == 1) { // modifying self///////////////////////////////////////////////
			if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
			
			Player player = (Player)sender;
			if(args[0].equalsIgnoreCase("design")) {
				// open the carpet editor if player has permission to use carpet
				if(!sender.hasPermission("karpet.use")) return CommandResult.NO_PERMISSION;
				openEditor(player);
			} else if(args[0].equalsIgnoreCase("reset")) {
				// reset own carpet if permission allows
				if(!sender.hasPermission("karpet.reset")) return CommandResult.NO_PERMISSION;
				resetCarpet(player, Arrays.asList(player));
			} else {
				if(!sender.hasPermission("karpet.use")) return CommandResult.NO_PERMISSION;
				// enable or disable own carpet
				Boolean result = binary.loadData(sender, cmd, label, args[0]);
				if(result == null) return binary.getInvalidArgumentResult(sender);
				if(result) {
					// check if the carpet can be enabled here
					if(!canEnable(player.getLocation())) {
						return new CommandResult(Karpet.getTranslator().getTranslation(this.plugin, player, "carpet.cannot-enable-here"),
								ResultType.NO_PERMISSION, Sounds.NO.get());
					}
					
					handler.addCarpet(player);
				} else {
					handler.removeCarpet(player);
				}
				
				sendCarpetMessage(player, result);
			}
			
		} else if(args.length == 2) { // modifying others///////////////////////////////////////////
			
			if(args[0].equalsIgnoreCase("reset")) { // RESET
				// reset other players carpets if found
				if(!sender.hasPermission("karpet.reset-others")) return CommandResult.NO_PERMISSION;
				BarePlayerComponent component = new BarePlayerComponent(false);
				List<Player> players = component.loadData(sender, cmd, label, args[1]);
				// send player not found error
				if(players == null || players.isEmpty()) 
					return new CommandResult(
							Karpet.getDefaultMsgs().playerNotFound(sender, args[1]), 
							ResultType.INVALID_ARGUMENTS, 
							Sounds.NO.get());
				
				// reset the carpets
				resetCarpet(sender, players);
				// send the result
				Karpet.getMessager().sendMessage(
						sender, 
						Sounds.SETTINGS.get(), 
						Karpet.getTranslator().getTranslation(
								plugin, 
								sender, 
								"carpet.reset-others")
						.replace("%players%", ArrayFormat.join(ArrayFormat.playersToArray(players), ", ")));
			} else { // ON / OFF
				if(!sender.hasPermission("karpet.change-others")) return CommandResult.NO_PERMISSION;
				// enable or disable other players carpet
				Boolean result = binary.loadData(sender, cmd, label, args[0]);
				if(result == null) return binary.getInvalidArgumentResult(sender);

				// load players from a given argument
				BarePlayerComponent component = new BarePlayerComponent(false);
				List<Player> players = component.loadData(sender, cmd, label, args[1]);
				// send player not found error
				if(players == null || players.isEmpty()) 
					return new CommandResult(
							Karpet.getDefaultMsgs().playerNotFound(sender, args[1]), 
							ResultType.INVALID_ARGUMENTS, 
							Sounds.NO.get());
				
				// loop each player
				for(Player player : players) {
					if(result) {
						// check if the carpet can be enabled here
						if(!canEnable(player.getLocation())) {
							Karpet.getMessager().sendMessage(player, 
									Sounds.NO.get(), 
									Karpet.getTranslator().getTranslation(this.plugin, player, "carpet.cannot-enable-here"));
							continue;
						}
						
						handler.addCarpet(player);
					} else {
						handler.removeCarpet(player);
					}
					
					sendCarpetMessage(player, result);
				}
				
				// send the result message
				if(result) {
					Karpet.getMessager().sendMessage(
							sender, 
							Sounds.SETTINGS.get(),
							Prefix.NEUTRAL +
							Karpet.getTranslator().getTranslation(plugin, sender, "carpet.enabled-others")
							.replace("%players%", ArrayFormat.join(ArrayFormat.playersToArray(players), ", ")));
				} else {
					Karpet.getMessager().sendMessage(
							sender, 
							Sounds.SETTINGS.get(),
							Prefix.NEUTRAL +
							Karpet.getTranslator().getTranslation(plugin, sender, "carpet.disabled-others")
							.replace("%players%", ArrayFormat.join(ArrayFormat.playersToArray(players), ", ")));
				}
			}
			
		} else {
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		return CommandResult.SUCCESS;
	}
	
	
	/**
	 * Send the carpet disabled message to players
	 * @param player player to send to
	 * @param enabled was the carpet enabled or disabled
	 */
	private void sendCarpetMessage(Player player, boolean enabled) {
		if(enabled) {
			Karpet.getMessager().sendActionBar(
					player, 
					Sounds.EQUIP.get(), 
					Karpet.getTranslator().getRandomTranslation(plugin, player, "carpet.enabled"));
		} else {
			Karpet.getMessager().sendActionBar(
					player, 
					Sounds.EQUIP.get(), 
					Karpet.getTranslator().getRandomTranslation(plugin, player, "carpet.disabled"));
		}
	}
	
	
	/**
	 * Resets the carpet design for given player
	 * @param sender sender who sent the command
	 * @param players players of whom the design will be reset
	 */
	private void resetCarpet(CommandSender sender, List<Player> players) {
		for(Player player : players) {
			handler.setLayout(player, handler.getDefaultLayout());
			Karpet.getMessager().sendMessage(
					player, 
					Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karpet.getTranslator().getTranslation(plugin, sender, "carpet.reset"));
		}
	}
	
	
	/**
	 * Open carpet creator
	 * @player player to whom the editor will be opened
	 */
	private void openEditor(Player player) {
		InventoryEditor editor = new InventoryEditor(
				(Karpet)plugin, 
				player, 
				handler.getLayout(player.getUniqueId())/*,
				handler.getDefaultLayout()*/);
		
		// create the editor manager
		CarpetCustomiserMenu menu = new CarpetCustomiserMenu(
				(Karpet)plugin, 
				player, 
				editor);
		
		// create the material picker list for the block selector
		InventoryList<Karpet> list = new InventoryList<Karpet>(
				(Karpet)plugin, 
				PresetActionItems.getEmpty(player),
				player,
				false,
				Karpet.getTranslator().getTranslation(plugin, player, "carpet.available-blocks"),
				true,
				"selected",
				true);
		
		// set the pickable items
		list.setSelectable(Karpet.getItemManager().convertMaterialToItemStack(handler.getCarpetMaterials()));
		
		// collect the no permission indexes
		List<Integer> indeces = new ArrayList<Integer>();
		int counter = 0;
		for(Material material : handler.getCarpetMaterials()) {
			if(!player.hasPermission("karpet.blocks."+material.name().toLowerCase()))
				indeces.add(counter);
			++counter;
		}
		
		// add the item picker as a possible inventory type (pick the materials ofc)
		menu.addInventory(list, "picker");
		
		// set the no permission items
		list.setNoPermissionIndeces(indeces);
		
		// open the inventory
		menu.openInventory("main");
	}
}
