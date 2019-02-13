package net.karanteeni.core.information.text;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.translation.TranslationContainer;

public class DefaultMessages implements TranslationContainer{
	
	private static final String noPermission = "no-permission";
	private static final String playerNotFound = "player-not-found";
	
	/**
	 * Register this class' translations
	 */
	@Override
	public void registerTranslations()
	{
		KaranteeniCore.getTranslator().registerRandomTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				noPermission, "You don't have permissions to this command!");
		KaranteeniCore.getTranslator().registerRandomTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				playerNotFound, "Couldn't find a player named %player%!");
	}
	
	/**
	 * Returns the default message for no permission
	 * @return
	 */
	public String defaultNoPermission()
	{
		return "You don't have permissions to this command!";
	}
	
	public String defaultNotForConsole()
	{
		return "Console cannot execute this command (with these arguments)!";
	}
	
	/**
	 * Return the no permission message
	 * @param player
	 * @return
	 */
	public String noPermission(Locale locale)
	{
		return KaranteeniCore.getTranslator().getRandomTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), locale, noPermission);
	}
	
	/**
	 * Return the default permission of not found player
	 * @param player
	 * @param notFoundName
	 * @return
	 */
	public String playerNotFound(Player player, String notFoundName)
	{
		return KaranteeniCore.getTranslator().getRandomTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), player, playerNotFound).replace("%player%", notFoundName);
	}
	
	/**
	 * Return the default permission of not found player
	 * @param player
	 * @param notFoundName
	 * @return
	 */
	public String playerNotFound(Locale locale, String notFoundName)
	{
		return KaranteeniCore.getTranslator().getRandomTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, playerNotFound).replace("%player%", notFoundName);
	}
	
	/**
	 * Return the no permission message
	 * @param sender Sender of the command
	 * @return
	 */
	public String noPermission(CommandSender sender)
	{
		if(sender instanceof Player)
			return KaranteeniCore.getTranslator().getRandomTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, noPermission);
		else
			return KaranteeniCore.getTranslator().getRandomTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), new Locale("en", "US"), noPermission);
	}
	
	/**
	 * Return the default permission of not found player
	 * @param sender Sender of the command
	 * @param notFoundName
	 * @return
	 */
	public String playerNotFound(CommandSender sender, String notFoundName)
	{
		if(sender instanceof Player)
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, playerNotFound).replace("%player%", notFoundName);
		else
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), new Locale("en", "US"), playerNotFound).replace("%player%", notFoundName);
	}
}
