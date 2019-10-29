package net.karanteeni.karanteenials.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;
import net.karanteeni.karanteenials.functionality.Back;

public class Teleport extends AbstractCommand implements TranslationContainer {
	private boolean safe;
	
	public Teleport(KaranteeniPlugin plugin) {
		super(plugin, "tp", 
				"/tp <player> OR /tp [<player>] [<player>] <x> <y> <z> [<yaw>] [<pitch>]", 
				"Teleports to a given location or another player", 
				Karanteenials.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
		
		if(!plugin.getConfig().isSet("teleport.precise-safe")) {
			plugin.getConfig().set("teleport.precise-safe", true);
			plugin.saveConfig();
		}
		safe = plugin.getConfig().getBoolean("teleport.precise-safe");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(args.length == 1) 								//Teleports sender to given player
		{	
			if(!sender.hasPermission("karanteenials.teleport.itself.player")) { //Check permission
				printNoPermissionError(sender);
				return true;
			}
			
			if(!(sender instanceof Player)) {
				Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE+Karanteenials.getDefaultMsgs().defaultNotForConsole());
				return true;
			}
			teleportToPlayer((Player)sender, args);
			return true;
		}
		else if(args.length == 2) { 							//teleports given player to given player
			if(!sender.hasPermission("karanteenials.teleport.others.player")) { //Check permission
				printNoPermissionError(sender);
				return true;
			}
			
			teleportPlayerToPlayer(sender, args);
			return true;
		}
		else if(args.length == 3 || args.length == 5) 		//teleports sender to given location
		{	
			if(!sender.hasPermission("karanteenials.teleport.itself.location")) { //Check permission
				printNoPermissionError(sender);
				return true;
			}
			
			if(!(sender instanceof Player)) {
				Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE+Karanteenials.getDefaultMsgs().defaultNotForConsole());
				return true;
			}
			teleportToLocation((Player)sender, args);
			return true;
		}
		else if(args.length == 4 || args.length == 6) {		//teleports another player to given location
			if(!sender.hasPermission("karanteenials.teleport.others.player")) { //Check permission
				printNoPermissionError(sender);
				return true;
			}
			teleportPlayerToLocation(sender, args);
			return true;
		}
		printInvalidArgsError(sender);
		return true;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> players = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers())
			players.add(player.getName());
        
		if(args.length == 1) { // tp Nuubles/~
			players.addAll(Arrays.asList("~","~ ~","~ ~ ~"));
        	return filterByPrefix(players, args[0]);
        } else if(args.length == 2) { // /tp Nuubles Nuubles/~
        	players.addAll(Arrays.asList("~","~ ~"));
        	return filterByPrefix(players, args[1]);
        } else if(args.length == 3) { // /tp Nuubles ~ ~   /tp ~ ~ ~
        	return filterByPrefix(Arrays.asList("~"), args[2]);
        } else if(args.length == 4) { // /tp Nuubles ~ ~ ~   /tp ~ ~ ~ ~
        	return filterByPrefix(Arrays.asList("~"), args[3]);
        } else if(args.length == 5) { // /tp Nuubles ~ ~ ~ ~ ~
        	return filterByPrefix(Arrays.asList("~","~ ~"), args[4]);
        } else if(args.length == 6) {
        	return filterByPrefix(Arrays.asList("~"), args[5]);
        }
		
		return null;
    }
	
	
	//tp player					-1
	private void teleportToPlayer(Player player, String[] args) {
		Player dest = Bukkit.getPlayer(args[0]);
		if(dest == null) {
			printPlayerNotFoundError(player, args[0]);
			return;
		}
		
		boolean allowed = ((Karanteenials)plugin).getPlayerData().getTpToggle().doesTeleportToggleAllow(player, dest);
		if(allowed) { //Check if player is blocked
			allowed = !((Karanteenials)plugin).getPlayerData().getBlockPlayer().isBlocked(player, dest);
		}
		
		//If teleport was not allowed,
		if(!allowed) {
			Karanteenials.getMessager().sendMessage(player, Sounds.NO.get(), 
					Prefix.NEGATIVE + 
					Karanteenials.getTranslator().getTranslation(plugin, player, "teleport-ask.receiver.blocked")
					.replace("%player%", dest.getName()));
			return;
		}
		
		// create a new back location for player
		Back back = new Back(player);
		back.setBackLocation(player.getLocation()); //Set the back location
		Teleporter teleporter = new Teleporter(dest.getLocation());
		if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
			teleporter.teleport(player, safe, false, true, TeleportCause.COMMAND);
		else
			teleporter.teleport(player, false, false, true, TeleportCause.COMMAND);
		
		Karanteenials.getMessager().sendMessage(player, Sounds.TELEPORT.get(), 
				Prefix.NEUTRAL+
				Karanteenials.getTranslator().getTranslation(plugin, player, "teleport.normal.self.player")
				.replace("%player%", dest.getName()));
	}
	
	//tp player player			-2
	private void teleportPlayerToPlayer(CommandSender sender, String[] args) {
		boolean tagged = false;
		for(String arg : args)
			if(arg.contains("@"))
				tagged = true;
		
		List<Player> players = new ArrayList<Player>(); //List of players to teleport
		
		//If tagged, sender has to be player
		if(tagged)
			if(!(sender instanceof Player))
			{
				printInvalidArgsError(sender);
				return;
			}
		
		//Add all players found
		players.addAll(Karanteenials.getPlayerHandler().getOnlinePlayers(sender, args[0]));
		
		Player dest = Bukkit.getPlayer(args[1]);
		//Player player = Bukkit.getPlayer(args[0]);
		if(players.isEmpty()) {
			printPlayerNotFoundError(sender, args[0]);
			return;
		} else if(dest == null) {
			printPlayerNotFoundError(sender, args[1]);
			return;
		}
		
		for(Player player : players) {
			// create a new back location for player
			Back back = new Back(player);
			back.setBackLocation(player.getLocation()); //Set the back location
			Teleporter teleporter = new Teleporter(dest.getLocation());
			if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
				teleporter.teleport(player, safe, false, true, TeleportCause.COMMAND);
			else
				teleporter.teleport(player, false, false, true, TeleportCause.COMMAND);
			
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					Karanteenials.getTranslator().getTranslation(plugin, sender, "teleport.normal.other.sender.player")
					.replace("%player%", player.getName())
					.replace("%destination%", dest.getName()));
			Karanteenials.getMessager().sendMessage(player, Sounds.TELEPORT.get(), 
					Prefix.NEUTRAL+
					Karanteenials.getTranslator().getTranslation(plugin, player, "teleport.normal.other.receiver.player")
					.replace("%player%", dest.getName()));
		}
	}
	
	//tp x y z					-3 YY
	//tp x y z yaw pitch		-5 YY
	private void teleportToLocation(Player player, String[] args) {
		Location destination = formatLocation(player.getLocation(), args);
		if(destination == null) {
			printInvalidArgsError(player);
			return;
		}
		
		// create a new back location for player
		Back back = new Back(player);
		back.setBackLocation(player.getLocation()); //Set the back location
		Teleporter teleporter = new Teleporter(destination);
		if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
			teleporter.teleport(player, safe, false, true, TeleportCause.COMMAND);
		else
			teleporter.teleport(player, false, false, true, TeleportCause.COMMAND);
		
		Karanteenials.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL+
				Karanteenials.getTranslator().getTranslation(plugin, player, "teleport.normal.self.location")
				.replace("%x%", String.format("%.3f", destination.getX()))
				.replace("%y%", String.format("%.3f", destination.getY()))
				.replace("%z%", String.format("%.3f", destination.getZ()))
				.replace("%yaw%", String.format("%.3f", destination.getYaw()))
				.replace("%pitch%", String.format("%.3f", destination.getPitch())));
	}
	
	//tp player x y z			-4 X
	//tp player x y z yaw pitch	-6 X
	private void teleportPlayerToLocation(CommandSender sender, String[] args) {
		boolean tagged = false;
		for(String arg : args)
			if(arg.contains("@"))
				tagged = true;
		
		List<Player> players = new ArrayList<Player>(); //List of players to teleport
		
		//If tagged, sender has to be player
		if(tagged)
			if(!(sender instanceof Player))
			{
				printInvalidArgsError(sender);
				return;
			}
		
		//Add all players found
		players.addAll(Karanteenials.getPlayerHandler().getOnlinePlayers(sender, args[0]));
			
		if(players.isEmpty()) {
			printPlayerNotFoundError(sender, args[0]);
			return;
		}
		
		for(Player player : players) {
			Location destination = formatLocation(player.getLocation(), args);
			if(destination == null) {
				printInvalidArgsError(sender);
				return;
			}
			
			// create a new back location for player
			Back back = new Back(player);
			back.setBackLocation(player.getLocation()); //Set the back location
			Teleporter teleporter = new Teleporter(destination);
			
			if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
				teleporter.teleport(player, safe, false, true, TeleportCause.COMMAND);
			else
				teleporter.teleport(player, false, false, true, TeleportCause.COMMAND);
			
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					Karanteenials.getTranslator().getTranslation(plugin, sender, "teleport.normal.other.sender.location")
					.replace("%x%", String.format("%.3f", destination.getX()))
					.replace("%y%", String.format("%.3f", destination.getY()))
					.replace("%z%", String.format("%.3f", destination.getZ()))
					.replace("%yaw%", String.format("%.3f", destination.getYaw()))
					.replace("%pitch%", String.format("%.3f", destination.getPitch())));
			
			Karanteenials.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					Karanteenials.getTranslator().getTranslation(plugin, player, "teleport.normal.other.receiver.location")
					.replace("%x%", String.format("%.3f", destination.getX()))
					.replace("%y%", String.format("%.3f", destination.getY()))
					.replace("%z%", String.format("%.3f", destination.getZ()))
					.replace("%yaw%", String.format("%.3f", destination.getYaw()))
					.replace("%pitch%", String.format("%.3f", destination.getPitch())));
		}
	}
	
	
	/**
	 * Formats the given args to location in given location.
	 * @param loc location to be used as helper
	 * @param args possible coordinates/yaw/pitch. Must be in "<x> <y> <z> [<yaw> <pitch>]" format
	 * @return Location if new location found or null if not found or invalid parameters
	 */
	private Location formatLocation(Location location, String[] args) {
		char[] prefixPossibilities = {'~','^'};
		double[] coords = new double[3];
		char[] prefixValues = new char[5];
		float yaw = Float.NaN, pitch = Float.NaN;
		
		//Incorrect parameter length, return null
		if(args.length != 3 && args.length != 5)
			return null;
		
		//x y z
		for(int i = 0; i < coords.length; ++i)
		{
			String arg = args[i];
			//Get and remove possible prefixes from coordinate
			for(char c : prefixPossibilities)
				if(arg.contains(c+"")) {
					prefixValues[i] = c;
					arg = arg.replace(c+"", "");
				}
			try {
				coords[i] = Double.parseDouble(arg);
			} catch(Exception e) {
				return null;
			}
		}
		
		//remaining yaw pitch
		for(int i = 3; i < args.length; ++i)
		{
			String arg = args[i];
			//Get and remove possible prefixes from coordinate
			for(char c : prefixPossibilities)
				if(arg.contains(c+"")) {
					prefixValues[i] = c;
					arg = arg.replace(c+"", "");
				}
			try {
				if(i == 3)
					yaw = Float.parseFloat(arg);
				else
					pitch = Float.parseFloat(arg);
			} catch(Exception e) {
				return null;
			}
		}
		
		//Calculate possible offsets or modifiers for x y z
		for(int i = 0; i < coords.length; ++i) {
			if(prefixValues[i] == '~') {
				if(i == 0)
					coords[0] = location.getX()+coords[0];
				else if(i == 1)
					coords[1] = location.getY()+coords[1];
				else if(i == 2)
					coords[2] = location.getZ()+coords[2];
			} else if(prefixValues[i] == '^') { //incorrect
				if(i == 0)
					coords[0] = location.getX()+coords[0];
				else if(i == 1)
					coords[1] = location.getY()+coords[1];
				else if(i == 2)
					coords[2] = location.getZ()+coords[2];
			}
		}
		
		if(!Float.isNaN(yaw)) {
			if(prefixValues[3] == '~') {
				yaw = location.getYaw()+yaw;
			} else if(prefixValues[3] == '^') {
				yaw = location.getYaw()+yaw; //incorrect
			}
		}
		if(!Float.isNaN(pitch)) {
			if(prefixValues[4] == '~') {
				pitch = location.getPitch()+pitch;
			} else if(prefixValues[4] == '^') {
				pitch = location.getPitch()+pitch; //incorrect
			}
		}
		return new Location(location.getWorld(), 
				coords[0], 
				coords[1], 
				coords[2], 
				Float.isNaN(yaw)?location.getYaw():yaw, 
				Float.isNaN(pitch)?location.getPitch():pitch);
	}
	
	
	/**
	 * Prints the invalid arguments error to sender
	 * @param sender
	 */
	private void printInvalidArgsError(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
				Prefix.NEGATIVE+Karanteenials.getDefaultMsgs().incorrectParameters(sender));
	}
	
	
	/**
	 * Prints the player not found error to a given commandsender
	 * @param sender
	 * @param playerName
	 */
	private void printPlayerNotFoundError(CommandSender sender, String playerName) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
				Prefix.NEGATIVE + 
				Karanteenials.getDefaultMsgs().playerNotFound(sender, playerName));
	}
	
	
	/**
	 * Prints the no permission message to sender
	 * @param sender
	 */
	private void printNoPermissionError(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Karanteenials.getDefaultMsgs().noPermission(sender));
	}
	
	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport.normal.self.location", 
				"You teleported to new location '%x%' '%y%' '%z%' '%yaw%' '%pitch%'");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport.normal.self.player", 
				"You teleported to player %player%");
		
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport.normal.other.sender.location", 
				"You teleported %player% to new location '%x%' '%y%' '%z%' '%yaw%' '%pitch%'");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport.normal.other.receiver.location", 
				"You have been teleported to new location '%x%' '%y%' '%z%' '%yaw%' '%pitch%'");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport.normal.other.sender.player", 
				"You teleported %player% to player %destination%");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport.normal.other.receiver.player", 
				"You have been teleported to player %player%");
	}
}
