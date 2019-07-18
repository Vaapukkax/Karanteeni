package net.karanteeni.core.information.text;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.translation.TranslationContainer;

public class DefaultMessages implements TranslationContainer{
	
	private static final String noPermission = "no-permission";
	private static final String playerNotFound = "player-not-found";
	private static final String incorrectParameters = "incorrect-command-parameters";
	private static final String databaseError = "database-error-occurred";
	private static final String onlyConsole = "only-console";
	private static final String errorHappened = "error-happened";
	
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
		KaranteeniCore.getTranslator().registerRandomTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				incorrectParameters, "Incorrect parameters in command!");
		KaranteeniCore.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				databaseError, "A database error occurred, please contact staff and try again later.");
		KaranteeniCore.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				onlyConsole, "Only the console may execute this command!");
		KaranteeniCore.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				errorHappened, "An error has happened. Please contact staff as soon as possible.");
	}
	
	
	/**
	 * Returns the default message for no permission
	 * @return
	 */
	public String defaultNoPermission() {
		return "You don't have permissions to this command!";
	}
	
	
	public String defaultNotForConsole() {
		return "Console cannot execute this command (with these arguments)!";
	}
	
	
	public String defaultDatabaseError() {
		return "A database error occurred, please contact staff and try again later.";
	}
	
	
	public String defaultIncorrectParameters() {
		return "Incorrect parameters in command!";
	}
	
	
	public String defaultOnlyForConsole() {
		return "Only the console may execute this command!";
	}

	
	public String defaultErrorHappened() {
		return "An error has happened. Please contact staff as soon as possible.";
	}
	
	
	/**
	 * Return the no permission message
	 * @param player
	 * @return
	 */
	public String noPermission(Locale locale) {
		return KaranteeniCore.getTranslator().getRandomTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, noPermission);
	}
	
	/**
	 * Return the default permission of not found player
	 * @param player
	 * @param notFoundName
	 * @return
	 */
	/*public String playerNotFound(Player player, String notFoundName)
	{
		return KaranteeniCore.getTranslator().getRandomTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), player, playerNotFound).replace("%player%", notFoundName);
	}*/
	
	/**
	 * Return the default permission of not found player
	 * @param player
	 * @param notFoundName
	 * @return
	 */
	public String playerNotFound(Locale locale, String notFoundName) {
		return KaranteeniCore.getTranslator().getRandomTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, playerNotFound).replace("%player%", notFoundName);
	}
	
	/**
	 * Return the no permission message
	 * @param sender Sender of the command
	 * @return
	 */
	public String noPermission(CommandSender sender) {
		if(sender instanceof Player)
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, noPermission);
		else
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), 
					KaranteeniPlugin.getTranslator().getDefaultLocale(), noPermission);
	}
	
	/**
	 * Return the default permission of not found player
	 * @param sender Sender of the command
	 * @param notFoundName
	 * @return
	 */
	public String playerNotFound(CommandSender sender, String notFoundName) {
		if(sender instanceof Player)
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, playerNotFound).replace("%player%", notFoundName);
		else
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), 
					KaranteeniPlugin.getTranslator().getDefaultLocale(), playerNotFound).replace("%player%", notFoundName);
	}
	
	/**
	 * Returns the translated message for incorrect parameters in command
	 * @param sender
	 * @return
	 */
	public String incorrectParameters(CommandSender sender) {
		if(sender instanceof Player)
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, incorrectParameters);
		else
			return KaranteeniCore.getTranslator().getRandomTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), 
					KaranteeniPlugin.getTranslator().getDefaultLocale(), incorrectParameters);
	}
	
	
	/**
	 * Returns the translated message for incorrect parameters in command
	 * @param locale
	 * @return
	 */
	public String incorrectParameters(Locale locale) {
		return KaranteeniCore.getTranslator().getRandomTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, incorrectParameters);
	}
	
	
	/**
	 * Returns the default message for database error events
	 * @param sender
	 * @return
	 */
	public String databaseError(CommandSender sender) {
		if(sender instanceof Player)
			return KaranteeniCore.getTranslator().getTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, databaseError);
		else
			return KaranteeniCore.getTranslator().getTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), 
					KaranteeniPlugin.getTranslator().getDefaultLocale(), databaseError);
	}
	
	
	/**
	 * Returns the default message for database error events
	 * @param locale
	 * @return
	 */
	public String databaseError(Locale locale) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, databaseError);
	}
	
	
	/**
	 * Returns the default message for events where player tries to execute a console command
	 * @param locale
	 * @return
	 */
	public String onlyConsole(CommandSender sender) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, onlyConsole);
	}

	
	/**
	 * Returns the default message for events where player tries to execute a console command
	 * @param locale
	 * @return
	 */
	public String onlyConsole(Locale locale) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, onlyConsole);
	}
	
	
	/**
	 * Returns the default message for error events
	 * @param locale
	 * @return
	 */
	public String errorHappened(CommandSender sender) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), (Player)sender, errorHappened);
	}

	
	/**
	 * Returns the default message for error events
	 * @param locale
	 * @return
	 */
	public String errorHappened(Locale locale) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), locale, errorHappened);
	}
}
