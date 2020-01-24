package net.karanteeni.core.command.defaultcomponent;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

public class LanguageLoader extends CommandLoader implements TranslationContainer {
	public final static String LANGUAGE_KEY = "core.language";
	
	public LanguageLoader(boolean before) {
		super(before);
	}

	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.invalid-language", 
				"The given language \"%language%\" is not valid");
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return CommandResult.INVALID_ARGUMENTS;
		
		// loop all locales and get the matching one
		Collection<Locale> locales = KaranteeniCore.getTranslator().getLocales();
		for(Locale locale : locales) {
			if(locale.toLanguageTag().equals(args[0])) {
				this.chainer.setObject(LANGUAGE_KEY, locale);
				return CommandResult.SUCCESS;
			}
		}
		
		// could not determine the given language
		return new CommandResult(Prefix.NEGATIVE + KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), sender, "command.component-error.invalid-language")
				.replace("%language%", args[0]),
				ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1)
			return this.filterByPrefix(KaranteeniCore.getTranslator().getStringLocales(), args[0], true);
		else
			return null;
	}
}
