package net.karanteeni.karanteeniperms.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;

public class ResetComponent extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
