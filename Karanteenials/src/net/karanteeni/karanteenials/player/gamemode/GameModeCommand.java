package net.karanteeni.karanteenials.player.gamemode;

import java.util.Arrays;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.karanteenials.Karanteenials;

public class GameModeCommand extends CommandChainer {
	
	public GameModeCommand(Karanteenials plugin) {
		super(plugin, 
				"gamemode", 
				"/gamemode <gamemode>", 
				"Change gamemode", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
		this.parameterLength = 0;
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!this.hasData("gamemode")) return CommandResult.INVALID_ARGUMENTS;
		
		// get the loaded game mode
		GameMode mode = this.getObject("gamemode");
		// get the possible other players
		List<Player> players = null;
		if(this.hasData(PlayerLoader.PLAYER_KEY_MULTIPLE))
			players = this.getObject(PlayerLoader.PLAYER_KEY_MULTIPLE);
		
		// if no players loaded, change own game mode
		if(players == null || players.size() == 0) {
			if(!(sender instanceof Player))
				return CommandResult.NOT_FOR_CONSOLE;
			if(!sender.hasPermission("karanteenials.player.gamemode."+mode.name().toLowerCase()+".self"))
				return CommandResult.NO_PERMISSION;
			
			// change and save the gamemode
			((Karanteenials)plugin).getPlayerData().getGameModeModule().setGamemode((Player)sender, mode, true);
		} else {
			// change gamemode of other players
			if(!sender.hasPermission("karanteenials.player.gamemode."+mode.name().toLowerCase()+".other"))
				return CommandResult.NO_PERMISSION;
			((Karanteenials)plugin).getPlayerData().getGameModeModule().setGamemode(sender, 
					players, 
					mode, 
					true, 
					true,
					true);
		}
		
		return null;
	}
}
