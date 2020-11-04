package net.karanteeni.core.command.defaultcomponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class StringCombinerLoader extends CommandLoader {
	public static final String STRING_COMBINER_RESULT = "core.stringcombiner";
	
	
	public StringCombinerLoader(boolean before) {
		super(before);
	}

	
	@Override
	protected void onRegister() {
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return CommandResult.INVALID_ARGUMENTS;
		this.chainer.setObject(STRING_COMBINER_RESULT, String.join(" ", args));
		return CommandResult.SUCCESS;
	}
}
