package net.karanteeni.core.command.defaultcomponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class StringCombinerLoader extends CommandLoader {
	public static final String STRING_COMBINER_RESULT = "core.stringcombiner";
	
	
	public StringCombinerLoader(boolean before) {
		super(before);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onRegister() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}
