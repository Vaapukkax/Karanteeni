package net.karanteeni.teleportal.respawn;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.teleportal.Teleportal;

public class RespawnCommand extends CommandChainer {

	public RespawnCommand() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"respawn", 
				"/respawn <set/remove>", 
				"Sets or removes the respawn location", 
				Teleportal.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {		
		// you cannot teleport to respawn location
		return CommandResult.INVALID_ARGUMENTS;
	}
}
