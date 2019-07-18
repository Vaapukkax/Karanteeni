package net.karanteeni.nature.commands.time;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.nature.Katura;

public class Time extends CommandChainer {

	public Time() {
		super(Katura.getPlugin(Katura.class), 
				"time", 
				"/time <set/add/remove/show> <ticks/day/night/midday/midnight>", 
				"Sets world time", 
				Katura.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
