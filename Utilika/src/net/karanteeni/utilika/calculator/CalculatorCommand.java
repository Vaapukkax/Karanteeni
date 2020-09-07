package net.karanteeni.utilika.calculator;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.utilika.Utilika;

public class CalculatorCommand extends BareCommand {
	public CalculatorCommand(KaranteeniPlugin plugin) {
		super(plugin,
				"calculate",
				"/calculate <expr>",
				"Runs a simple calculation",
				Utilika.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1)
			return CommandResult.INVALID_ARGUMENTS;
		
		StringBuilder calculation = new StringBuilder();
		for(String val : args)
			calculation.append(val);
		
		try {
			String result = Calculator.eval(calculation.toString()).toString();
			sender.sendMessage(Prefix.NEUTRAL + result);
		} catch(Exception e) {
			sender.sendMessage(Prefix.NEGATIVE + e.getMessage());
		}
		
		return CommandResult.ASYNC_CALLBACK;
	}
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
}
