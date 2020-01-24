package net.karanteeni.karanteeniperms.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;

public class TestComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2 || !this.chainer.hasData(PermissionPlayerLoader.PERMISSION_PLAYER_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		PermissionPlayer player = this.chainer.getObject(PermissionPlayerLoader.PERMISSION_PLAYER_KEY);
		
		// test if the player has the given permission
		if(player.hasPermission(args[1])) {
			KaranteeniPerms.getMessager().sendMessage(sender, Sounds.PLING_HIGH.get(), 
					Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(
							this.chainer.getPlugin(), sender, "permissions.player.test-true")
					.replace("%player%", args[0])
					.replace("%permission%", args[1].toLowerCase()));
		} else {
			KaranteeniPerms.getMessager().sendMessage(sender, Sounds.PLING_LOW.get(), 
					Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(
							this.chainer.getPlugin(), sender, "permissions.player.test-false")
					.replace("%player%", args[0])
					.replace("%permission%", args[1].toLowerCase()));
		}
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.test-true", 
				"%player% can use the permission %permission%");
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.test-false", 
				"%player% cannot use the permission %permission%");
	}
}
