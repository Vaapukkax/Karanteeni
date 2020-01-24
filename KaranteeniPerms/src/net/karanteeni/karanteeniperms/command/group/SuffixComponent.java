package net.karanteeni.karanteeniperms.command.group;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class SuffixComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2 || !this.chainer.hasData(GroupLoader.CHAINER_GROUP_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		Group group = (Group)this.chainer.getData(GroupLoader.CHAINER_GROUP_KEY);
		
		// build the prefix from arguments
		StringBuffer buffer = new StringBuffer();
		for(int i = 1; i < args.length; ++i) {
			buffer.append(args[i]);
			buffer.append(" ");
		}
		buffer.deleteCharAt(buffer.length()-1);
		String groupName = buffer.toString().replace('&', '§');
		
		// set and save prefix
		if(!group.setSuffix(groupName))
			return new CommandResult(Prefix.NEGATIVE + 
					KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "permissions.save-failed"),
					ResultType.ERROR, Sounds.ERROR.get());
		
		// message player about the new suffix
		KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), Prefix.POSITIVE +
				KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "permissions.group.set.suffix")
				.replace("%group%", group.getID())
				.replace("%suffix%", groupName));
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.group.set.suffix", 
				"Suffix of group %group% was set to §r%suffix%");
	}
}
