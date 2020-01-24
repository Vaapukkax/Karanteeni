package net.karanteeni.karanteeniperms.command.group;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class PermissionsLoader extends CommandLoader {

	public PermissionsLoader(boolean before) {
		super(before);
	}

	
	@Override
	protected void onRegister() {
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}
}
