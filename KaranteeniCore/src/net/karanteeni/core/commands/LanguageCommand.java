package net.karanteeni.core.commands;

import java.util.Arrays;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.text.Prefix;

public class LanguageCommand extends CommandChainer {

	public LanguageCommand() {
		super(KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"language", 
				"/languge <lang>", 
				"Change the language to custom language", 
				KaranteeniCore.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		
		Locale locale = null;
		if(this.hasData(LanguageComponent.LANGUAGE_KEY))
			locale = this.getObject(LanguageComponent.LANGUAGE_KEY);
		
		if(KaranteeniCore.getTranslator().setLocale((Player)sender, locale)) {
			if(locale != null)
				sender.sendMessage(Prefix.PLUSPOSITIVE + "Non translated message: Changed language tag to " + 
						locale.toLanguageTag() + " or otherwise known as " + locale.getCountry());
			else
				sender.sendMessage(Prefix.PLUSPOSITIVE + "Non translated message: Changed language tag to automatic. The translations will now follow the your client");
		} else {
			sender.sendMessage(Prefix.PLUSPOSITIVE + "Non translated message: Failed to save language change! After logging off your language will RESET");
		}
		
		return CommandResult.SUCCESS;
	}
}
