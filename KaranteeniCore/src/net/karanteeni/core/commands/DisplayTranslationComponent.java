package net.karanteeni.core.commands;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;

public class DisplayTranslationComponent extends CommandComponent {
	
	@Override
	protected void onRegister() {
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return CommandResult.INVALID_ARGUMENTS;
		if(!this.chainer.hasData(TranslationKeyLoader.TRANSLATION_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		String translationKey = this.chainer.getObject(TranslationKeyLoader.TRANSLATION_KEY);
		
		String[] parts = translationKey.split("\\.");
		if(parts.length < 3)
			return CommandResult.INVALID_ARGUMENTS;

		// Get plugin
		KaranteeniPlugin plugin = null;
		for(KaranteeniPlugin plugin_ : KaranteeniCore.getPluginInstances()) {
			if(plugin_.getName().equals(parts[0])) {
				plugin = plugin_;
				break;
			}
		}

		if(plugin == null)
			return CommandResult.INVALID_ARGUMENTS;
		// Get locale
		Locale locale = KaranteeniCore.getTranslator().getLocale(parts[1]);
		if(locale == null)
			return CommandResult.INVALID_ARGUMENTS;
		// Get key
		String key = translationKey.substring(parts[0].length() + parts[1].length() + 2);
		
		switch(KaranteeniCore.getTranslator().getTranslationType(translationKey)) {
		case STATIC:
			sender.sendMessage(KaranteeniCore.getTranslator().getTranslation(plugin, locale, key));
			return CommandResult.SUCCESS;
		case RANDOM:
			int i = 0;
			List<String> values = new LinkedList<String>();
			for(String translation : KaranteeniCore.getTranslator().getRandomTranslations(plugin, locale, key)) {
				values.add(i++ + ": " + translation);
			}
			sender.sendMessage(String.join("§r\n", values));
			return CommandResult.SUCCESS;
		default:
			return CommandResult.INVALID_ARGUMENTS;
		}
	}
}
