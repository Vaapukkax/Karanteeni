package net.karanteeni.chatar.command.ignore;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;

public class Ignore extends CommandChainer {

	public Ignore(KaranteeniPlugin plugin) {
		super(plugin, 
				"ignore", 
				"/ignore enable <name>", 
				"ignore or remove ignore from players", 
				Chatar.getDefaultMsgs().defaultNoPermission(), Arrays.asList());
		
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
