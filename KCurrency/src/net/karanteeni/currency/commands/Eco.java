package net.karanteeni.currency.commands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.text.TextUtil;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.currency.KCurrency;

public class Eco extends AbstractCommand implements CommandExecutor,TranslationContainer {
	
	public Eco(String command, String usage, String description) {
		super(KCurrency.getPlugin(KCurrency.class), command, usage, description, Arrays.asList("give","set","take"));
		registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		//Check that all arguments exist
		if(args.length == 3)
		{
			if(this.getRealParam(args[0]).equalsIgnoreCase("give"))
			{
				giveCMD(sender, args);
			}
			else if(this.getRealParam(args[0]).equalsIgnoreCase("take"))
			{
				takeCMD(sender, args);
			}
			else if(this.getRealParam(args[0]).equalsIgnoreCase("set"))
			{
				setCMD(sender, args);
			}
		}
		else
		{
			//Player typed command incorrectly, send help to the command
			sendHelp(sender);
		}
		
		return true;
	}
	
	private void giveCMD(CommandSender sender, String[] args) 
	{
		if(!sender.hasPermission("kcurrency.eco.give"))
		{
			KCurrency.getMessager().sendMessage(
					sender, 
					Sounds.NO.get(), 
					Prefix.NEGATIVE+KCurrency.getDefaultMsgs().noPermission(sender));
			return;
		}
		
		UUID uuid = KCurrency.getPlayerHandler().getUUID(args[1]);
		//Check if player was found
		if(uuid == null)
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().playerNotFound(sender, args[1]));
			return;
		}
		
		double amount = TextUtil.parseDouble(args[2]);
		
		//Check if the double is valid
		if(Double.isNaN(amount) || amount < 0.001)
		{
			sendInvalidNumber(sender);
			return;
		}
		
		//Perform the actual command action
		Double result = ecoGive(uuid, amount);
		
		//Player does not exist
		if(result == null)
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().playerNotFound(sender, args[1]));
		}
		else if(Double.isNaN(result))
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + 
					KCurrency.getTranslator().getTranslation(KCurrency.getPlugin(KCurrency.class), sender, "payment-failed"));
		}
		else
		{
			//Send the confirmation message to the command executor that the command succeeded
			KCurrency.getMessager().sendMessage(sender, KCurrency.MONEY_RECEIVED, 
					Prefix.POSITIVE + 
					KCurrency.getTranslator().getTranslation(KCurrency.getPlugin(KCurrency.class), sender, "give-balance-of")
					.replace("%player%", KCurrency.getPlayerHandler().getOfflineName(uuid))
					.replace("%amount%", String.format("%.3f", amount))
					.replace("%newbal%", String.format("%.3f", result))
					.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit()));
			
			if(Bukkit.getPlayer(uuid).isOnline())
			{
				BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getTranslation(
						KCurrency.getPlugin(KCurrency.class), 
						Bukkit.getPlayer(uuid), "you-received")
						.replace("%amount%", String.format("%.3f", amount))
						.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit())
						, BarColor.GREEN, BarStyle.SOLID);
				
				KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(uuid), KCurrency.MONEY_RECEIVED, 3, 1, true, bar);
			}
		}
	}
	
	private void takeCMD(CommandSender sender, String[] args) 
	{
		if(!sender.hasPermission("kcurrency.eco.take"))
		{
			KCurrency.getMessager().sendMessage(
					sender, 
					Sounds.NO.get(), 
					Prefix.NEGATIVE+KCurrency.getDefaultMsgs().noPermission(sender));
			return;
		}
		
		UUID uuid = KCurrency.getPlayerHandler().getUUID(args[1]);
		//Check if player was found
		if(uuid == null)
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().playerNotFound(sender, args[1]));
			return;
		}
		
		double amount = TextUtil.parseDouble(args[2]);
		
		//Check if the double is valid
		if(Double.isNaN(amount) || amount < 0.001)
		{
			sendInvalidNumber(sender);
			return;
		}
		
		//Perform the actual command action
		Double result = ecoTake(uuid, amount);
		
		//Player does not exist
		if(result == null)
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().playerNotFound(sender, args[1]));
		}
		else if(Double.isNaN(result))
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + KCurrency.getTranslator().getTranslation(KCurrency.getPlugin(KCurrency.class), sender, "payment-failed"));
		}
		else
		{
			//Send the confirmation message to the command executor that the command succeeded
			KCurrency.getMessager().sendMessage(sender, KCurrency.MONEY_LOST, 
					Prefix.POSITIVE + 
					KCurrency.getTranslator().getTranslation(KCurrency.getPlugin(KCurrency.class), sender, "take-balance-of")
					.replace("%player%", KCurrency.getPlayerHandler().getOfflineName(uuid))
					.replace("%amount%", String.format("%.3f", amount))
					.replace("%newbal%", String.format("%.3f", result))
					.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit()));
			
			if(Bukkit.getPlayer(uuid).isOnline())
			{
				BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getTranslation(
						KCurrency.getPlugin(KCurrency.class), 
						Bukkit.getPlayer(uuid), "you-lost")
						.replace("%amount%", String.format("%.3f", amount))
						.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit())
						, BarColor.GREEN, BarStyle.SOLID);
				
				KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(uuid), KCurrency.MONEY_LOST, 3, 1, true, bar);
			}
		}
	}
	
	private void setCMD(CommandSender sender, String[] args) 
	{
		if(!sender.hasPermission("kcurrency.eco.set"))
		{
			KCurrency.getMessager().sendMessage(
					sender, 
					Sounds.NO.get(), 
					Prefix.NEGATIVE+KCurrency.getDefaultMsgs().noPermission(sender));
			return;
		}
		
		UUID uuid = KCurrency.getPlayerHandler().getUUID(args[1]);
		//Check if player was found
		if(uuid == null)
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().playerNotFound(sender, args[1]));
			return;
		}
		
		double amount = TextUtil.parseDouble(args[2]);
		
		//Check if the double is valid
		if(Double.isNaN(amount) || amount < 0.001)
		{
			sendInvalidNumber(sender);
			return;
		}
		
		//Perform the actual command action
		boolean result = ecoSet(uuid, amount);
		
		//Player does not exist
		if(!result)
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().playerNotFound(sender, args[1]));
		}
		else
		{
			//Send the confirmation message to the command executor that the command succeeded
			KCurrency.getMessager().sendMessage(sender, KCurrency.MONEY_RECEIVED,
					Prefix.POSITIVE + 
					KCurrency.getTranslator().getTranslation(KCurrency.getPlugin(KCurrency.class), sender, "set-balance-of")
					.replace("%player%", KCurrency.getPlayerHandler().getOfflineName(uuid))
					.replace("%amount%", String.format("%.3f", amount))
					.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit()));
		}
		
		if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
		{
			BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getTranslation(
					KCurrency.getPlugin(KCurrency.class), 
					Bukkit.getPlayer(uuid), "your-balance-set")
					.replace("%amount%", String.format("%.3f", amount))
					.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit())
					, BarColor.GREEN, BarStyle.SOLID);
			KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(uuid), KCurrency.MONEY_RECEIVED, 3, 1, true, bar);
		}
	}
	
	/**
	 * Player typed an invalid number. Sends the error message
	 * @param sender
	 */
	private void sendInvalidNumber(CommandSender sender)
	{
		KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + 
			KCurrency.getTranslator().getTranslation(
					KCurrency.getPlugin(KCurrency.class), (Player)sender, "min-amount").replace("%unit%", 
					KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit()));
	}
	
	/**
	 * Sends the help message to player
	 * @param sender
	 */
	private void sendHelp(CommandSender sender)
	{
		KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + "/eco <give/take/set> <player> <amount>");
	}
	
	/**
	 * Gives money to player
	 * @param player
	 * @param amount
	 */
	private Double ecoGive(final UUID uuid, double amount)
	{
		return KCurrency.getBalances().addToBalance(uuid, amount);
	}
	
	/**
	 * Removes money from players balance 
	 * @param uuid
	 * @param amount
	 * @return
	 */
	private Double ecoTake(final UUID uuid, double amount)
	{
		return KCurrency.getBalances().removeFromBalance(uuid, amount);
	}
	
	private boolean ecoSet(final UUID uuid, double amount)
	{
		return KCurrency.getBalances().setBalance(uuid, amount);
	}

	@Override
	public void registerTranslations() {
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "set-balance-of", "Set the balance of %player% to %amount%%unit%");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "give-balance-of", "You gave %player% %amount%%unit%. New balance is %newbal%%unit%");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "take-balance-of", "You took %amount%%unit% from %player%. New balance is %newbal%%unit%");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 1) {
			return filterByPrefix(Arrays.asList("give","set","take"), args[0]);
		}
		else if(args.length == 2) {
			//"kcurrency.eco.give"
			String realParam = getRealParam(args[0]);
			if(realParam.equals("give") && sender.hasPermission("kcurrency.eco.give")) {
				return getPlayerNames(args[1]);
			}
			else if(realParam.equals("take") && sender.hasPermission("kcurrency.eco.take")) {
				return getPlayerNames(args[1]);			
			}
			else if(realParam.equals("set") && sender.hasPermission("kcurrency.eco.set")) {
				return getPlayerNames(args[1]);
			}
		}
		
		return null;
	}
}
