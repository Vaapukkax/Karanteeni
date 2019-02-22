package net.karanteeni.currency.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.currency.KCurrency;

/**
 * A class for the /balance [<player>] command
 * @author Matti
 *
 */
public class Bal extends AbstractCommand implements TranslationContainer{

	public Bal(String command, String usage, String description) {
		super(KCurrency.getPlugin(KCurrency.class), command, usage, description);
		registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		KCurrency plugin = KCurrency.getPlugin(KCurrency.class);
		
		if(args.length == 1)
		{
			//Does the player have permission for this
			if(!sender.hasPermission("kcurrency.bal.other"))
			{
				KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), KCurrency.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			UUID uuid = KCurrency.getPlayerHandler().getUUID(args[0]);
			
			if(uuid == null)
			{
				KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), KCurrency.getDefaultMsgs().playerNotFound(sender, args[0]));
				return true;
			}
			
			KCurrency.getMessager().sendMessage(sender, Sounds.NONE.get(),
				plugin.getConfigHandler().getPrefix() + 
				KCurrency.getTranslator().getTranslation(plugin, sender, "check-balance-other")
				.replace("%amount%", KCurrency.getBalances().getBalance(uuid).toString())
				.replace("%unit%", plugin.getConfigHandler().getCurrencyUnit())
				.replace("%player%", KCurrency.getPlayerHandler().getOfflineName(uuid))
			);
		}
		else if(args.length == 0)
		{
			//Does the player have permission for this
			if(!sender.hasPermission("kcurrency.bal.own"))
			{
				KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + KCurrency.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			if(!(sender instanceof Player))
			{
				KCurrency.getMessager().sendMessage(
						sender, 
						Sounds.NO.get(), 
						KCurrency.getDefaultMsgs().defaultNotForConsole());
				return true;
			}
			
			KCurrency.getMessager().sendMessage(sender, Sounds.NONE.get(),
				plugin.getConfigHandler().getPrefix() + 
				KCurrency.getTranslator().getTranslation(plugin, (Player)sender, "check-balance-own")
					.replace("%amount%", KCurrency.getBalances().getBalance(((Player)sender).getUniqueId()).toString())
					.replace("%unit%", plugin.getConfigHandler().getCurrencyUnit())
			);
		}
		
		return true;
	}

	@Override
	public void registerTranslations() {
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "check-balance-own", "Your balance is %amount%%unit%");
		KCurrency.getTranslator().registerTranslation(KCurrency.getPlugin(KCurrency.class), "check-balance-other", "Player %player%s balance is %amount%%unit%");
	}
}
