package net.karanteeni.karanteeniperms.command.player;

import java.util.LinkedList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;

public class RemoveComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2)
			return CommandResult.INVALID_ARGUMENTS;
		if(!this.chainer.hasData(PermissionPlayerLoader.PERMISSION_PLAYER_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		PermissionPlayer player = this.chainer.getObject(PermissionPlayerLoader.PERMISSION_PLAYER_KEY);
		LinkedList<String> removed = new LinkedList<String>();
		LinkedList<String> invalidPermissions = new LinkedList<String>();
		
		// remove all the given permissions
		for(int i = 1; i < args.length; ++i) {
			if(player.hasPrivatePermission(args[i])) {
				if(player.removePermission(args[i]) != QueryState.REMOVAL_SUCCESSFUL)
					return CommandResult.ERROR;
				removed.add(args[i]);
			} else {
				invalidPermissions.add(args[i]);
			}
		}
		
		// message about the added permissions
		if(!removed.isEmpty())
			KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.permission-removed")
				.replace("%player%", args[0])
				.replace("%permissions%", ArrayFormat.join(removed.toArray(new String[removed.size()]), ", ")));
		
		// message about the not added permissions
		if(!invalidPermissions.isEmpty())
			KaranteeniPerms.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.does-not-have-permission")
				.replace("%player%", args[0])
				.replace("%permissions%", ArrayFormat.join(invalidPermissions.toArray(new String[invalidPermissions.size()]), ", ")));
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.permission-removed", 
				"Removed private permissions [%permissions%] from player %player%");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.player.does-not-have-permission", 
				"Player %player% does not have the following permissions: [%permissions%]");
	}
}
