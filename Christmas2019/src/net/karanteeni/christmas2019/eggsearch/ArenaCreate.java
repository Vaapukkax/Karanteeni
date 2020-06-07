package net.karanteeni.christmas2019.eggsearch;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;

public class ArenaCreate extends CommandChainer {

	public ArenaCreate(KaranteeniPlugin plugin) {
		super(plugin, 
				"egg", 
				"/egg", 
				"create, reset and delete game data", 
				KaranteeniCore.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
