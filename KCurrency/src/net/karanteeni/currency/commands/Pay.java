package net.karanteeni.currency.commands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.currency.KCurrency;
import net.karanteeni.currency.transactions.Transaction;
import net.karanteeni.currency.transactions.TransactionResult;

public class Pay extends AbstractCommand implements TranslationContainer{

	public Pay(String command, String usage, String description, String permissionMessage)
	{
		super(KCurrency.getPlugin(KCurrency.class), command, usage, description, permissionMessage);
		registerTranslations();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		//Does the player have permission for this
		if(!sender.hasPermission("kcurrency.pay")) {
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE+KCurrency.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		if(!(sender instanceof Player)) {
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+KCurrency.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		
		if(args.length == 2) {
			Player sender_ = (Player)sender;
			Player player = Bukkit.getPlayer(args[0]);
			UUID uuid = null;
			
			//Get player uuid
			if(player == null)			
				uuid = KCurrency.getPlayerHandler().getUUID(args[0]);
			else
				uuid = player.getUniqueId();
			
			//Player was not found
			if(uuid == null) {
				KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE+KCurrency.getDefaultMsgs().playerNotFound(sender, args[0]));
				return true;
			}
			
			//Player paid itself
			if(uuid.equals(sender_.getUniqueId())) {
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE + 
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, "cannot-pay-yourself"));
				return true;
			}
			
			double transferAmount;
			
			//Get the amount to be transferred to another player
			try{
				transferAmount = Double.parseDouble(args[1]);
			} catch(Exception e) {
				invalidArguments((Player)sender);
				return true;
			}
			
			if(transferAmount < 0.01)
			{
				KCurrency curr = KCurrency.getPlugin(KCurrency.class);
				KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), curr.getConfigHandler().getPrefix()+
						KCurrency.getTranslator().getTranslation(curr, (Player)sender, "min-amount")
						.replace("%unit%", curr.getConfigHandler().getCurrencyUnit()));
				return true;
			}
			
			//Player was found and money amount also
			transferMoney(((Player)sender).getUniqueId(), uuid, transferAmount);
		}
		else
		{
			invalidArguments((Player)sender);
		}
		
		return true;
	}
	
	/**
	 * Send the invalid arguments message to player
	 * @param sender
	 */
	private void invalidArguments(Player sender)
	{
		KCurrency.getMessager().sendMessage(
				sender, 
				Sounds.NO.get(), 
				Prefix.NEGATIVE+KCurrency.getPlugin(KCurrency.class).getConfigHandler().getPrefix() + 
					KCurrency.getTranslator().getTranslation(KCurrency.getPlugin(KCurrency.class), sender, "invalid-pay-arguments"));
	}
	
	/**
	 * Transfers a given amount of money to other player
	 * @param who from who will the money be taken
	 * @param to to whom will the money be given
	 * @param amount amount of money to be transferred
	 */
	public void transferMoney(UUID who, UUID to, Double amount)
	{
		Transaction trans = new Transaction(who, to, amount);
		TransactionResult result = trans.performTransaction();
		KCurrency plugin = KCurrency.getPlugin(KCurrency.class);
		String senderName = Bukkit.getPlayer(who).getName();
		Player receiver = Bukkit.getPlayer(to);
		String receiverName;
		
		//Get the name of the receiver
		if(receiver != null)
			receiverName = receiver.getName();
		else
			receiverName = KCurrency.getPlayerHandler().getOfflineName(to);
		
		//Transaction was successful
		if(result.equals(TransactionResult.SUCCESSFUL)) {
			BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getRandomTranslation(
					plugin, 
					Bukkit.getPlayer(who), "paid-x-money-to-y-player")
					.replace("%amount%", String.format("%.2f", amount))
					.replace("%unit%", plugin.getConfigHandler().getCurrencyUnit())
					.replace("%player%", receiverName), BarColor.GREEN, BarStyle.SOLID);
			
			//Send the message to the sender
			KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(who), KCurrency.MONEY_LOST, 3, 1, true, bar);
			
			//Send the message to the receiver
			if(receiver != null) {
				BossBar bar2 = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getRandomTranslation(
						plugin, 
						receiver, "player-x-paid-y-to-you")
						.replace("%amount%", String.format("%.2f", amount))
						.replace("%unit%", plugin.getConfigHandler().getCurrencyUnit())
						.replace("%player%", senderName), BarColor.GREEN, BarStyle.SOLID);
				
				KCurrency.getMessager().sendBossbar(receiver, KCurrency.MONEY_RECEIVED, 3, 1, true, bar2);
			}
		}
		//There was not enough money
		else if(result.equals(TransactionResult.INSUFFICIENT_CREDITS))
		{
			KCurrency.getMessager().sendMessage(Bukkit.getPlayer(who), Sounds.NO.get(), 
					plugin.getConfigHandler().getPrefix()
					+ KCurrency.getTranslator().getRandomTranslation(
					plugin, 
					Bukkit.getPlayer(who), 
					"not-enough-currency")
					.replace("%balance%", KCurrency.getBalances().getBalance(who).toString())
					.replace("%amount%",String.format("%.2f", amount))
					.replace("%unit%", plugin.getConfigHandler().getCurrencyUnit()));
			
			// %balance% is not enough for %amount%%unit% 
			return;
		}
		//There was an error while performing transaction
		else if(result.equals(TransactionResult.UNSUCCESSFUL))
		{
			KCurrency.getMessager().sendMessage(Bukkit.getPlayer(who), Sounds.NO.get(), 
					plugin.getConfigHandler().getPrefix() + 
					KCurrency.getTranslator().getTranslation(
					plugin, 
					Bukkit.getPlayer(who), 
					"payment-failed"));
			return;
		}
	}

	@Override
	public void registerTranslations() {
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "payment-failed", "Your payment failed");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "invalid-pay-arguments", "/pay <player> <amount>");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "cannot-pay-yourself", "You cannot transfer money to yourself!");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "min-amount", "The minimum amount you can use is 0.01%unit%!");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "you-received", "You received %amount%%unit%!");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "you-lost", "You lost %amount%%unit%!");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "your-balance-set", "Your balance was set to %amount%%unit%!");
		KCurrency.getTranslator().registerRandomTranslation(KCurrency.getPlugin(KCurrency.class), "paid-x-money-to-y-player", "You paid %amount%%unit% money to %player%");
		KCurrency.getTranslator().registerRandomTranslation(KCurrency.getPlugin(KCurrency.class), "player-x-paid-y-to-you", "You received %amount%%unit% money from %player%");
		KCurrency.getTranslator().registerRandomTranslation(KCurrency.getPlugin(KCurrency.class), "not-enough-currency", "Your balance %balance% is not enough for %amount%%unit% bill");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 1 && sender.hasPermission("kcurrency.pay")) {
			return getPlayerNames(args[0]);
		}
		else if(args.length == 2 && sender.hasPermission("kcurrency.pay")) {
			return Arrays.asList("10.0");
		}
		return null;
	}
}
