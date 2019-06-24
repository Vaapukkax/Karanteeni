package net.karanteeni.core.command;

import org.bukkit.command.CommandSender;

public interface ChainerInterface {
	/**
	 * Run when the given argument does not match the required arguments
	 * @param sender command sender
	 */
	public abstract void invalidArguments(CommandSender sender);
	
	/**
	 * Command sender does not have the required permission to this command
	 * @param sender sender with missing permission
	 */
	public abstract void noPermission(CommandSender sender);
	
	/**
	 * Checks if player has the permission to use this component
	 * @param sender command sender
	 * @return true if has permission, false otherwise
	 */
	public boolean hasPermission(CommandSender sender);
}
