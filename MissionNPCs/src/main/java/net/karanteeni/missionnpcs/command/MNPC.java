package net.karanteeni.missionnpcs.command;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.missionnpcs.MissionNPCs;

public class MNPC extends CommandChainer {

	public MNPC(KaranteeniPlugin plugin) {
		super(plugin,
				"mnpc",
				"/mnpc <args>",
				"MissionNPC main command",
				MissionNPCs.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
	}

	@Override
	protected CommandResult runCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return CommandResult.INVALID_ARGUMENTS;
	}

}
