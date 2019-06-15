package net.karanteeni.karanteenials.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.karanteenials.Karanteenials;

public class GameModeCommand extends AbstractCommand {
	private static final HashMap<String, GameMode> modeMap = new HashMap<String, GameMode>();
	
	public GameModeCommand(Karanteenials plugin) {
		super(plugin, 
				"gamemode", 
				"/gamemode <gamemode>", 
				"Change gamemode", 
				Karanteenials.getDefaultMsgs().defaultNoPermission());
		
		// add values to mode map
		for(GameMode mode : GameMode.values())
			modeMap.put(mode.name().toLowerCase(), mode);
		
		modeMap.put("0", GameMode.SURVIVAL);
		modeMap.put("1", GameMode.CREATIVE);
		modeMap.put("2", GameMode.ADVENTURE);
		modeMap.put("3", GameMode.SPECTATOR);
		modeMap.put("s", GameMode.SURVIVAL);
		modeMap.put("c", GameMode.CREATIVE);
		modeMap.put("a", GameMode.ADVENTURE);
		modeMap.put("sp", GameMode.SPECTATOR);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		// only gamemode was entered. Changing own gamemode
		if(args.length == 1) {
			// is the sender player
			if(!(sender instanceof Player)) {
				Karanteenials.getMessager().sendMessage(sender, 
						Sounds.NO.get(), 
						Prefix.NEGATIVE +
						Karanteenials.getDefaultMsgs().defaultNotForConsole());
				return true;
			}
			
			// get the requested gamemode
			GameMode mode = modeMap.get(args[0].toLowerCase());
			
			// no gm was found
			if(mode == null) {
				sendIncorrectParameters(sender);
				return true;
			}
			
			// check if player has permissions to change gamemode
			if(!sender.hasPermission("karanteenials.player.gamemode."+mode.name().toLowerCase()+".self")) {
				Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE +
						Karanteenials.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			// change and save the gamemode
			((Karanteenials)plugin).getPlayerData().getGameModeModule().setGamemode((Player)sender, mode, true);
			
		} else if(args.length == 2) { // another player was entered, change multiple players gamemode
			// get the requested gamemode
			GameMode mode = modeMap.get(args[1].toLowerCase());
			
			// no gm was found
			if(mode == null) {
				sendIncorrectParameters(sender);
				return true;
			}
			
			// check if player has permissions to change gamemode
			if(!sender.hasPermission("karanteenials.player.gamemode."+mode.name().toLowerCase()+".other")) {
				Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE +
						Karanteenials.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			((Karanteenials)plugin).getPlayerData().getGameModeModule().setGamemode(sender, 
					args[0], 
					mode, 
					true, 
					args[0].contains("@"), // multiple players only if there's @ in the params
					true);
		} else {
			sendIncorrectParameters(sender);
		}
		
		return true;
	}
	
	
	/**
	 * Send incorrect params message to command sender
	 * @param sender to whom should the message be sent
	 */
	private void sendIncorrectParameters(CommandSender sender) {
		KaranteeniPlugin.getMessager().sendMessage(sender, 
				Sounds.NO.get(), 
				Prefix.NEGATIVE +
				KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
	}
	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		List<String> gamemodes = new ArrayList<String>();
		
		for(GameMode mode : GameMode.values())
			gamemodes.add(mode.toString().toLowerCase());
		
		if(args.length == 1) {
			List<String> gms = filterByPrefix(gamemodes, args[0]);
			if(!gms.isEmpty())
				return gms;
			
			return getPlayerNames(args[0]);
		} else if(args.length == 2) {
			return filterByPrefix(gamemodes, args[1]);
		}
		
		return null;
	}
}
