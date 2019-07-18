package net.karanteeni.core.command.bare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;

public class BarePlayerComponent implements BareComponent<List<Player>> {
	private boolean showBukkit = false;
	
	public BarePlayerComponent(boolean showBukkit) {
		this.showBukkit = showBukkit;
	}
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String arg) {
		// split the string into multiple parts
		String[] parts = arg.split(",");
		Character lastChar = (arg.length() != 0) ? arg.charAt(arg.length()-1) : null;
		String res = arg;
		
		// get all player names
		@SuppressWarnings("unchecked")
		Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
		Set<String> playerNames = new HashSet<String>();
		for(Player player : players)
			playerNames.add(player.getName());
		playerNames.addAll(Arrays.asList("@a", "@r", "@p"));
		
		if(showBukkit) {
			// TODO BUKKIT NAMES
		}
		
		// at the beginning of arg or comma so requires special handling
		if(lastChar == null || lastChar.charValue() == ',') {
			List<String> results = new ArrayList<String>();
			// add all compatible names to the results array
			for(String name : playerNames)
				results.add(res + name);
			return results;
		}
		
		// get the last part of players
		String lastPart = parts[parts.length-1].toLowerCase();
		
		// replace the last letters with nothing from the result to allow gluing
		res = res.substring(0, res.length()-lastPart.length());
		
		List<String> results = new ArrayList<String>();
		
		// add all compatible names to the results array
		for(String name : playerNames)
			if(name.toLowerCase().startsWith(lastPart))
				results.add(res + name);
		
		return results;
	}

	@Override
	public List<Player> loadData(CommandSender sender, Command cmd, String label, String arg) {
		// get requested players
		return KaranteeniCore.getPlayerHandler().getOnlinePlayers(sender, arg);
	}

	
	/**
	 * Get the commandresult for too many players error
	 * @param sender sender to which the command is translated to
	 * @return commandresult error
	 */
	public CommandResult getTooManyPlayersError(CommandSender sender) {
		return new CommandResult(
				KaranteeniCore.getTranslator().getTranslation(
						KaranteeniCore.getPlugin(KaranteeniCore.class), 
						sender, 
						"players.too-many"),
				ResultType.INVALID_ARGUMENTS,
				Sounds.NO.get());
	}
}
