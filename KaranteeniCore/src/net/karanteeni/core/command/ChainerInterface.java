package net.karanteeni.core.command;

import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public interface ChainerInterface {
	
	/**
	 * Run when the given argument does not match the required arguments
	 * @param sender command sender
	 */
	default public void invalidArguments(CommandSender sender) {
		KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE + 
				KaranteeniCore.getDefaultMsgs().incorrectParameters(sender));
	}
	
	
	/**
	 * Command sender does not have the required permission to this command
	 * @param sender sender with missing permission
	 */
	default public void noPermission(CommandSender sender) {
		KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE + 
				KaranteeniCore.getDefaultMsgs().noPermission(sender));
	}
	
	/**
	 * Checks if player has the permission to use this component
	 * @param sender command sender
	 * @return true if has permission, false otherwise
	 */
	public boolean hasPermission(CommandSender sender);
}
