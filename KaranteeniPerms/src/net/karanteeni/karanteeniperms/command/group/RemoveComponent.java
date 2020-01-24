package net.karanteeni.karanteeniperms.command.group;

import java.util.LinkedList;
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

public class RemoveComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2 || !this.chainer.hasData(GroupLoader.CHAINER_GROUP_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		Group group = (Group)this.chainer.getData(GroupLoader.CHAINER_GROUP_KEY);
		
		LinkedList<String> removedPermissions = new LinkedList<String>();
		LinkedList<String> doesNotContain = new LinkedList<String>();
		
		// remove the permissions
		for(int i = 1; i < args.length; ++i)
			if(group.removePermission(args[i], false))
				removedPermissions.add(args[i]);
			else
				doesNotContain.add(args[i]);
		
		// save the group
		if(!group.saveGroup())
			return new CommandResult(Prefix.NEGATIVE + 
					KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "permissions.save-failed"),
					ResultType.ERROR, Sounds.ERROR.get());
		
		// say which permissions were removed
		if(!removedPermissions.isEmpty()) {
			this.chainer.getPlugin();
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), 
							sender, 
							"permissions.group.permission-removed")
					.replace("%group%", group.getID())
					.replace("%permissions%", ArrayFormat.join(removedPermissions.toArray(new String[removedPermissions.size()]), ", ")));
		}
		
		// say which permissions did not exist
		if(!doesNotContain.isEmpty()) {
			this.chainer.getPlugin();
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), 
							sender, 
							"permissions.group.does-not-have-permission")
					.replace("%group%", group.getID())
					.replace("%permissions%", ArrayFormat.join(doesNotContain.toArray(new String[doesNotContain.size()]), ", ")));
		}
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.group.does-not-have-permission", 
				"Group %group% does not have permissions [%permissions%]");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.group.permission-removed", 
				"Removed permissions [%permissions%] to group %group%");
	}

}
