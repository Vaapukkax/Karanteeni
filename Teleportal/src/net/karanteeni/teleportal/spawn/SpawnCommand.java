package net.karanteeni.teleportal.spawn;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.functionality.Back;
import net.karanteeni.teleportal.Teleportal;

public class SpawnCommand extends CommandChainer implements TranslationContainer {

	public SpawnCommand() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"spawn", 
				"/spawn [<players>]", 
				"Teleports to the spawn location of the world", 
				Teleportal.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(plugin, "spawn.not-set", "Spawn has not been set");
		Teleportal.getTranslator().registerTranslation(plugin, "spawn.teleport-self", "You teleported to spawn");
		Teleportal.getTranslator().registerTranslation(plugin, "spawn.teleport-others", "You teleported [%players%] to spawn");
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!this.isSet("core.players")) { // teleport self
			if(!(sender instanceof Player))
				return CommandResult.NOT_FOR_CONSOLE;
			if(!sender.hasPermission("teleportal.spawn.teleport.self"))
				return CommandResult.NO_PERMISSION;
			
			SpawnManager sm = new SpawnManager((Teleportal)plugin);
			Location prev = ((Player)sender).getLocation();
			Location loc = sm.teleportToSpawn((Player)sender);
			
			if(loc == null) {
				return new CommandResult(
						Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.not-set"),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get());
			} else {
				// create a new back location for player
				Back back = new Back((Player)sender);
				back.setBackLocation(prev); //Set the back location
				Teleportal.getMessager().sendActionBar(
						sender, 
						Sounds.TELEPORT.get(), 
						Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.teleport-self"));
				return CommandResult.SUCCESS;
			}
			
		} else { // teleport others
			if(!sender.hasPermission("teleportal.spawn.teleport.other"))
				return CommandResult.NO_PERMISSION;
			
			SpawnManager sm = new SpawnManager((Teleportal)plugin);
			List<Player> players = getObject("core.players");
			boolean success = sm.teleportToSpawn(players);
			
			if(players.size() == 0)
				return new CommandResult(
						Teleportal.getDefaultMsgs().playerNotFound(sender, args[0]), 
						ResultType.INVALID_ARGUMENTS, 
						Sounds.NO.get());
			
			if(!success) {
				return new CommandResult(
						Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.not-set"),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get());
			} else {
				// message which all players were teleported
				String playerNames = ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ");
				Teleportal.getMessager().sendMessage(
						sender, 
						Sounds.SETTINGS.get(), 
						Prefix.NEUTRAL +
						Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.teleport-others")
						.replace("%players%", playerNames));
				
				// message about the teleportation to each player
				for(Player player : players) {
					Back back = new Back(player);
					back.setBackLocation(player.getLocation()); //Set the back location
					Teleportal.getMessager().sendActionBar(
							player, 
							Sounds.TELEPORT.get(), 
							Teleportal.getTranslator().getTranslation(plugin, player, "spawn.teleport-self"));
				}
			}
		}
		
		
		return CommandResult.SUCCESS;
	}
}
