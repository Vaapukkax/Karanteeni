package net.karanteeni.core.commands;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;

public class TranslationCommand extends CommandChainer {

	public TranslationCommand(KaranteeniPlugin plugin) {
		super(plugin, "translation",
				"/translation",
				"Modify translations",
				KaranteeniCore.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
