package net.karanteeni.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandLoader extends CommandComponent {
	/**
	 * Chain and always execute this component
	 * @param sender command sender
	 * @param cmd command
	 * @param label command label
	 * @param args command args
	 * @return true if success, false if invalid args
	 */
	public boolean exec(CommandSender sender, Command cmd, String label, String[] args) {
		// run the loader before continuing forward
		boolean retValue = runComponent(sender, cmd, label, args);
		
		// run the possible chain if there is anything to chain
		if(args != null && args.length > 0) {
			Boolean chainResult = chainComponents(args[0], sender, cmd, label, cutArgs(args));
			if(chainResult != null)
				return chainResult;
		}
		
		// run the code of this component
		return retValue;
	}
}
