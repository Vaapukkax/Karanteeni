package net.karanteeni.karanteeniperms.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;

public class PlayerComponent extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.INVALID_ARGUMENTS;
	}
}
