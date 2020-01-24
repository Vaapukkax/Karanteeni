package net.karanteeni.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandResult.DisplayFormat;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.text.Prefix;

public interface ChainerInterface {

	/**
	 * command executor returned odd input
	 * @param sender sender with missing permission
	 */
	default public void onlyConsole(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		if(message == null)
			message = KaranteeniCore.getDefaultMsgs().onlyConsole(sender);
		
		// if no message has been set, use the default message
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
		}
	}
	
	
	/**
	 * command executor returned odd input
	 * @param sender sender with missing permission
	 */
	default public void notForConsole(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		if(message == null)
			message = KaranteeniCore.getDefaultMsgs().defaultNotForConsole();
		
		// if no message has been set, use the default message
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
		}
	}
	
	
	/**
	 * command executor returned odd input
	 * @param sender sender with missing permission
	 */
	default public void success(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		// if no message has been set, use the default message
		if(message != null && !message.isEmpty())
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.POSITIVE + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.POSITIVE + message);
				break;
		} else {
			KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
		}
	}
	
	
	/**
	 * command executor returned odd input
	 * @param sender sender with missing permission
	 */
	default public void other(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		// if no message has been set, use the default message
		if(message != null && !message.isEmpty())
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEUTRAL + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEUTRAL + message);
				break;
		} else {
			KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
		}
	}
	
	
	/**
	 * Command sender has put invalid arguments to the command
	 * @param sender sender with missing permission
	 */
	default public void invalidArguments(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		// if no message has been set, use the default message
		if(message == null)
			message = KaranteeniCore.getDefaultMsgs().incorrectParameters(sender);
		
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
		}
	}
	
	
	/**
	 * Command sender has put invalid arguments to the command
	 * @param sender sender with missing permission
	 */
	default public void error(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		// if no message has been set, use the default message
		if(message == null)
			message = KaranteeniCore.getDefaultMsgs().errorHappened(sender);
		
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.ERROR + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.ERROR + message);
				break;
		}
	}
	
	
	/**
	 * Command sender does not have the required permission to this command
	 * @param sender sender with missing permission
	 */
	default public void noPermission(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		// if no message has been set, use the default message
		if(message == null)
			message = KaranteeniCore.getDefaultMsgs().noPermission(sender);
		
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEGATIVE + message);
				break;
		}
	}
	
	
	/**
	 * Command sender does not have the required permission to this command
	 * @param sender sender with missing permission
	 */
	default public void callBack(CommandSender sender, SoundType sound, DisplayFormat format, String message) {
		// if no message has been set, use the default message
		if(message == null)
			message = KaranteeniCore.getDefaultMsgs().noPermission(sender);
		
		switch (format) {
			case MESSAGE:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEUTRAL + message);
				break;
			case ACTIONBAR:
				KaranteeniCore.getMessager().sendActionBar(sender, sound, message);
				break;
			case BOSSBAR:
				KaranteeniCore.getMessager().sendBossbar(sender, sound, 1.5f, 4, true, message);
				break;
			case SUBTITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, "", message, sound);
				break;
			case TITLE:
				KaranteeniCore.getMessager().sendTitle(0.1f, 0.1f, 1.5f, sender, message, "", sound);
				break;
			case NONE:
				if(sender instanceof Player)
					KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
				break;	
			default:
				KaranteeniCore.getMessager().sendMessage(sender, sound, Prefix.NEUTRAL + message);
				break;
		}
	}
	
	
	/**
	 * Checks if player has the permission to use this component
	 * @param sender command sender
	 * @return true if has permission, false otherwise
	 */
	public boolean hasPermission(CommandSender sender);
}
