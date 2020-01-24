package net.karanteeni.karanteeniperms.command;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

public class PermissionsCommand extends CommandChainer {

	public PermissionsCommand(KaranteeniPlugin plugin) {
		super(plugin, 
				"permissions", 
				"/permissions ...", 
				"Modifies player groups and permissions", 
				KaranteeniPerms.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
