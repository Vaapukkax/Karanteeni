package net.karanteeni.core.commands;

import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class LanguageComponent extends CommandLoader {
	public static final String LANGUAGE_KEY = "core.language";
	
	public LanguageComponent() {
		super(true);
	}

	
	@Override
	protected void onRegister() {
		
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1)
			return CommandResult.INVALID_ARGUMENTS;
		
		List<Locale> locales = KaranteeniCore.getTranslator().getLocales();
		
		if(args[0].equalsIgnoreCase("automatic")) 
			return CommandResult.SUCCESS;
		
		for(Locale locale : locales) {
			if(locale.toLanguageTag().toLowerCase().equals(args[0].toLowerCase())) {
				this.chainer.setObject(LANGUAGE_KEY, locale);
				return CommandResult.SUCCESS;
			}
		}
		
		return CommandResult.SUCCESS;
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> locales = KaranteeniCore.getTranslator().getStringLocales();
		locales.add("automatic");
		
		return filterByPrefix(locales, args[0], false);
	}
}
