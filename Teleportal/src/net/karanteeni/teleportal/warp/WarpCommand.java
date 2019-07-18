package net.karanteeni.teleportal.warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class WarpCommand extends CommandChainer implements TranslationContainer {
	
	public WarpCommand(CommandLoader comp) {
		super(Teleportal.getPlugin(Teleportal.class), 
				"warp", 
				"/warp [<player>] <warp name>", 
				"warps player to a given warp", 
				Teleportal.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
		
		// set the loader to this component
		this.setLoader(comp);
		registerTranslations();
	}


	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// invalid arguments
		if(args.length != 1 && args.length != 2)
			return CommandResult.INVALID_ARGUMENTS;
		
		// get the loaded warp
		Warp warp = this.<Warp>getObject("warp");
		boolean self = args.length == 1;
		List<Player> teleported = new ArrayList<Player>();
		
		// player is warping themself, check if command sender is player
		if(args.length == 1) {
			// add self to teleported
			if(!(sender instanceof Player)) {
				Teleportal.getMessager().sendMessage(sender, Sounds.NO.get(),
						Prefix.NEGATIVE +
						Teleportal.getDefaultMsgs().defaultNotForConsole());
				return CommandResult.OTHER;
			}
			
			// check if sender has the permission to warp
			if(!sender.hasPermission("teleportal.warp.use")) {
				return CommandResult.NO_PERMISSION;
			}
			
			teleported.add((Player)sender);
		} else {
			// teleporting other players, check permission
			if(!sender.hasPermission("teleportal.warp.send")) {
				return CommandResult.NO_PERMISSION;
			}
			
			// teleporting other players, get them
			teleported.addAll(Teleportal.getPlayerHandler().getOnlinePlayers(sender, args[1]));
		}
		
		// teleport all players to the warp
		for(Player player : teleported) {
			if(warp.teleport(player) == null && self) {
				// no permission to teleport
				Teleportal.getMessager().sendActionBar(player, Sounds.NO.get(), 
						Prefix.NEGATIVE +
						Teleportal.getTranslator().getTranslation(plugin, player, "warp.no-permission")
						.replace("%warp%", warp.getDisplayName()));
				continue;
			}
			
			if(self) { // teleport self
				Teleportal.getMessager().sendActionBar(player, Sounds.TELEPORT.get(), 
						Teleportal.getTranslator().getTranslation(plugin, player, "warp.teleport.self")
						.replace("%warp%", warp.getDisplayName()));
			} else { // teleport others
				Teleportal.getMessager().sendActionBar(player, Sounds.TELEPORT.get(), 
						Teleportal.getTranslator().getTranslation(plugin, player, "warp.teleport.self-by-other")
						.replace("%warp%", warp.getDisplayName()));
			}
		}
		
		// if teleported others, message about the results
		if(!self) {
			if(teleported.size() == 0) {
				// no one to teleport was found
				Teleportal.getMessager().sendMessage(sender, Sounds.PLING_LOW.get(),
						Prefix.NEGATIVE +
						Teleportal.getDefaultMsgs().playerNotFound(sender, args[1]));
			} else if(teleported.size() == 1) {
				// teleported only one player
				Teleportal.getMessager().sendMessage(sender, Sounds.PLING_HIGH.get(), 
						Prefix.NEUTRAL +
						Teleportal.getTranslator().getTranslation(plugin, sender, "warp.teleport.other-by-self")
						.replace("%player%", teleported.get(0).getDisplayName())
						.replace("%warp%", warp.getDisplayName()));
			} else {
				// teleported multiple players
				// format list of players to a string
				String players = ArrayFormat.joinSort(ArrayFormat.playersToArray(teleported), ", ");
				Teleportal.getMessager().sendMessage(sender, Sounds.PLING_HIGH.get(), 
						Prefix.NEUTRAL +
						Teleportal.getTranslator().getTranslation(plugin, sender, "warp.teleport.other-by-self")
						.replace("%player%", players)
						.replace("%warp%", warp.getDisplayName()));
			}
		}
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(plugin, 
				"warp.teleport.self-by-other", 
				"You have been warped to %warp%");
		Teleportal.getTranslator().registerTranslation(plugin, 
				"warp.teleport.self", 
				"You warped to %warp%");
		Teleportal.getTranslator().registerTranslation(plugin, 
				"warp.teleport.other-by-self", 
				"You warped %player% to %warp%");
		Teleportal.getTranslator().registerTranslation(plugin, 
				"warp.teleport.others-by-self", 
				"You warped players %player%§f to %warp%");
		Teleportal.getTranslator().registerTranslation(plugin, 
				"warp.no-permission", 
				"You don't have permission to use warp %warp% or the warp is unsafe to use");
	}
}
