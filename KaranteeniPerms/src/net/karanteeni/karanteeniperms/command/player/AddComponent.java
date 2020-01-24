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

public class AddComponent extends CommandComponent implements TranslationContainer {

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
		LinkedList<String> addedPermissions = new LinkedList<String>();
		LinkedList<String> collidedPermissions = new LinkedList<String>();
		
		for(int i = 1; i < args.length; ++i) {
			if(player.hasPrivatePermission(args[i]))
				collidedPermissions.add(args[i]);
			else {
				QueryState result = player.addPermission(args[i]);
				if(result != QueryState.INSERTION_SUCCESSFUL && result != QueryState.INSERTION_FAIL_ALREADY_EXISTS)
					return CommandResult.ERROR;
				addedPermissions.add(args[i]);
			}
		}
		
		// did we add any permissions to the player, if so then save
		//if(!addedPermissions.isEmpty())
			//return CommandResult.ERROR;
		
		// message about the added permissions
		if(!addedPermissions.isEmpty())
			KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.permission-added")
				.replace("%player%", args[0])
				.replace("%permissions%", ArrayFormat.join(addedPermissions.toArray(new String[addedPermissions.size()]), ", ")));
		
		// message about the not added permissions
		if(!collidedPermissions.isEmpty())
			KaranteeniPerms.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.has-permission")
				.replace("%player%", args[0])
				.replace("%permissions%", ArrayFormat.join(collidedPermissions.toArray(new String[collidedPermissions.size()]), ", ")));
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.permission-added", 
				"Added private permissions [%permissions%] to player %player%");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), "permissions.player.has-permission", 
				"Player %player% already has private permissions [%permissions%]");
	}
}
