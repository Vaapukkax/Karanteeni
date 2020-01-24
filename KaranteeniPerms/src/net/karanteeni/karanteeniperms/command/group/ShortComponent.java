package net.karanteeni.karanteeniperms.command.group;

import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.LanguageLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class ShortComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 3 || 
				!this.chainer.hasData(GroupLoader.CHAINER_GROUP_KEY) || 
				!this.chainer.hasData(LanguageLoader.LANGUAGE_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		Group group = (Group)this.chainer.getData(GroupLoader.CHAINER_GROUP_KEY);
		Locale locale = (Locale)this.chainer.getData(LanguageLoader.LANGUAGE_KEY);
		
		// build the group name
		StringBuffer buffer = new StringBuffer();
		for(int i = 2; i < args.length; ++i) {
			buffer.append(args[i]);
			buffer.append(" ");
		}
		buffer.deleteCharAt(buffer.length()-1);
		String name = buffer.toString().replace('&', '§');
		
		// set group name
		group.setName(locale, name, false);
		
		// message about change
		KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), Prefix.POSITIVE +
				KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "permissions.group.set.rankname-short")
				.replace("group", group.getID())
				.replace("%language%", locale.toLanguageTag())
				.replace("%name%", name));
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.group.set.rankname-short", 
				"Set the short name of group %group% in language %language% to §r%name%");
	}
}
