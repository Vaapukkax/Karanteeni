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
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;

public class ResetLongRanknameComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1 || !this.chainer.hasData(PermissionPlayerLoader.PERMISSION_PLAYER_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		PermissionPlayer player = this.chainer.getObject(PermissionPlayerLoader.PERMISSION_PLAYER_KEY);
		player.resetGroupName();
		if(!player.save())
			return new CommandResult(Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(
					this.chainer.getPlugin(), sender, "permissions.save-failed"), ResultType.ERROR, Sounds.NO.get());
		
		KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.reset.name.long").replace("%player%", args[0]));
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.reset.name.long", 
				"Resetted the long rankname of player %player%");
	}
}
