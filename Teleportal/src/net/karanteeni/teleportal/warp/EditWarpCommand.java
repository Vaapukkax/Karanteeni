package net.karanteeni.teleportal.warp;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.teleportal.Teleportal;

public class EditWarpCommand extends CommandChainer {
	public EditWarpCommand() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"editwarp", 
				"editwarp <type> <name> [<name>]", 
				"modifies or creates a warp", 
				Teleportal.getDefaultMsgs().defaultIncorrectParameters(), 
				Arrays.asList("create", "delete", "move", "rename", "permission"));
	}

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// TODO Auto-generated method stub
		return CommandResult.INVALID_ARGUMENTS;
	}
}
