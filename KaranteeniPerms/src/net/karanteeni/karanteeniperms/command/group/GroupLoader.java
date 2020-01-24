package net.karanteeni.karanteeniperms.command.group;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class GroupLoader extends CommandLoader implements TranslationContainer {
	public static final String CHAINER_GROUP_KEY = "group";
	
	public GroupLoader(boolean before) {
		super(before);
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1) return CommandResult.INVALID_ARGUMENTS;
		
		KaranteeniPerms plugin = (KaranteeniPerms)this.chainer.getPlugin();
		Group group = plugin.getGroupList().getGroup(args[0]);
		
		// if no groups were found give error
		if(group == null) {
			return new CommandResult(
					Prefix.NEGATIVE +
					KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "permissions.group.does-not-exist")
					.replace("%group%", args[0]),
					ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		}
		
		this.chainer.setObject(CHAINER_GROUP_KEY, group);
		return CommandResult.SUCCESS;
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1)
			return null;
		KaranteeniPerms plugin = (KaranteeniPerms)this.chainer.getPlugin();
		List<String> groupIDs = new LinkedList<String>();
		for(Group group : plugin.getGroupList().getGroups())
			groupIDs.add(group.getID());
		return this.filterByPrefix(groupIDs, args[0], true);
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.group.does-not-exist", "No group with ID %group% could be found!");
	}
}
