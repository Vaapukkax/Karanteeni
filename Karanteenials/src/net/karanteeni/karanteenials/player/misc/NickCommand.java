package net.karanteeni.karanteenials.player.misc;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.command.bare.BarePlayerComponent;
import net.karanteeni.core.information.ChatColor;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.karanteenials.Karanteenials;

/**
 * This class is responsible for managing the player's nickname through commands
 * @author Nuubles
 *
 */
public class NickCommand extends BareCommand {
	private static char[] COLORS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static char[] FORMATS = {'o', 'n', 'm', 'r', 'l'};
	private static char RANDOM = 'k';
	private static char[] ILLEGAL_CHARACTERS = {'@', ',', ':', ';', '%'};
	private static int NICK_LENGTH_LIMIT = 128;
	private BarePlayerComponent playerComponent = new BarePlayerComponent(false);
	
	public NickCommand() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"nick", 
				"/nick <nick>", 
				"sets and modifies nicknames", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 2)
			return CommandResult.INVALID_ARGUMENTS;
		
		// nick <player> off OR nick <player> <nick>
		if(args.length == 2) {
			List<Player> players = playerComponent.loadData(sender, cmd, label, args[0]);
			if(players == null || players.isEmpty()) {
				return new CommandResult(
						Karanteenials.getDefaultMsgs().playerNotFound(sender, args[0]),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get());
			}
			
			boolean isOff = args[1].toLowerCase().equals("off");
			
			if(isOff) {
				if(!sender.hasPermission("karanteenials.nick.change.self")) return CommandResult.NO_PERMISSION;
				
				// reset nickname
				NickSetEvent event = new NickSetEvent(players.get(0).getUniqueId(), players.get(0).getName(), sender);
				Bukkit.getPluginManager().callEvent(event);
			} else {
				if(!sender.hasPermission("karanteenials.nick.change.other")) return CommandResult.NO_PERMISSION;
				String nick = formatNick(sender, args[1]);
				
				// Limit length to 128 characters
				if(nick.length() > NICK_LENGTH_LIMIT)
					nick = nick.substring(0, NICK_LENGTH_LIMIT);
				
				// set a new nickname
				NickSetEvent event = new NickSetEvent(players.get(0).getUniqueId(), nick, sender);
				Bukkit.getPluginManager().callEvent(event);
			}
			
			return CommandResult.SUCCESS;
		} else if(args.length == 1) {
			if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
			if(!sender.hasPermission("karanteenials.nick.change.self")) return CommandResult.NO_PERMISSION;
			
			// set or reset own nick
			if(args[0].toLowerCase().equals("off")) {
				// reset nickname
				NickSetEvent event = new NickSetEvent(((Player)sender).getUniqueId(), ((Player)sender).getName(), sender);
				Bukkit.getPluginManager().callEvent(event);
			} else {
				// set new nickname
				NickSetEvent event = new NickSetEvent(((Player)sender).getUniqueId(), formatNick(sender, args[0]), sender);
				Bukkit.getPluginManager().callEvent(event);
			}
			
			return CommandResult.SUCCESS;
		}
		
		return CommandResult.INVALID_ARGUMENTS;
	}

	
	/**
	 * Formats the nick to have colors
	 * @param name
	 * @return
	 */
	private String formatNick(CommandSender sender, String name) {
		if(sender.hasPermission("karanteenials.nick.rgb"))
			name = ChatColor.translateHexColorCodes(name);
		if(sender.hasPermission("karanteenials.nick.color"))
			name = ChatColor.translateColor(name);
		if(sender.hasPermission("karanteenials.nick.format"))
			name = ChatColor.translateFormat(name);
		if(sender.hasPermission("karanteenials.nick.scramble"))
			name = ChatColor.translateMagic(name);
		
		
		// remove illegal characters
		for(char c : ILLEGAL_CHARACTERS)
			name = name.replace(c+"", "");
		return name;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> results = null;
		if(args.length == 1) {
			results = playerComponent.autofill(sender, cmd, label, args[0]);
			results.add("off");
			return filterByPrefix(results, args[0], false);
		}
		
		return null;
	}
}
