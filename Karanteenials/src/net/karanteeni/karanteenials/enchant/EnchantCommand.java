package net.karanteeni.karanteenials.enchant;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.karanteenials.Karanteenials;

public class EnchantCommand extends CommandChainer {
	public static final String ENCHANT_KEY = "ench";
	public static final String LEVEL_KEY = "ench_lvl";
	
	public EnchantCommand() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"enchant", 
				"/enchant give/take <enchant> [<level>]", 
				"Enchant or disenchant enchantment from item in hand", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}

	@Override
	protected CommandResult runCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
