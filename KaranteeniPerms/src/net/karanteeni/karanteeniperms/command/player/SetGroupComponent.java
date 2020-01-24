package net.karanteeni.karanteeniperms.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.command.group.GroupLoader;
import net.karanteeni.karanteeniperms.groups.player.Group;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;

public class SetGroupComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2 || !this.chainer.hasData(PermissionPlayerLoader.PERMISSION_PLAYER_KEY) || !this.chainer.hasData(GroupLoader.CHAINER_GROUP_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		PermissionPlayer player = this.chainer.getObject(PermissionPlayerLoader.PERMISSION_PLAYER_KEY);
		Group group = this.chainer.getObject(GroupLoader.CHAINER_GROUP_KEY);
		
		Group orig = player.setGroup(group);
		if(!player.save())
			return new CommandResult(Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(
					this.chainer.getPlugin(), sender, "permissions.save-failed"), ResultType.ERROR, Sounds.NO.get());
		
		KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.set.group").replace("%player%", args[0])
				.replace("%original%", orig.getID())
				.replace("%group%", group.getID()));
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.set.group", 
				"Set the group of %player% from %original% to %group%");
	}
}
