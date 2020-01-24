package net.karanteeni.karanteeniperms.command.group;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class AddComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2 || !this.chainer.hasData(GroupLoader.CHAINER_GROUP_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		Group group = (Group)this.chainer.getData(GroupLoader.CHAINER_GROUP_KEY);
		List<String> alreadyContains = new LinkedList<String>();
		List<String> addedPermissions = new LinkedList<String>();
		
		// add permissions which do not yet exist
		for(int i = 1; i < args.length; ++i)
			if(group.addPermission(args[i], false))
				addedPermissions.add(args[i]);
			else
				alreadyContains.add(args[i]);
		
		// save the group
		if(!group.saveGroup())
			return new CommandResult(Prefix.NEGATIVE + 
					KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "permissions.save-failed"),
					ResultType.ERROR, Sounds.ERROR.get());
		
		// say which permissions were added
		if(!addedPermissions.isEmpty()) {
			this.chainer.getPlugin();
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), 
							sender, 
							"permissions.group.permission-added")
					.replace("%group%", group.getID())
					.replace("%permissions%", ArrayFormat.join(addedPermissions.toArray(new String[addedPermissions.size()]), ", ")));
		}
		
		// say which permissions were not added
		if(!alreadyContains.isEmpty()) {
			this.chainer.getPlugin();
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), 
							sender, 
							"permissions.group.has-permission")
					.replace("%group%", group.getID())
					.replace("%permissions%", ArrayFormat.join(alreadyContains.toArray(new String[alreadyContains.size()]), ", ")));
		}
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.group.has-permission", 
				"Group %group% already has permissions [%permissions%]");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.group.permission-added", 
				"Added permissions [%permissions%] to group %group%");
	}

}
