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

public class SetSuffixComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2 || !this.chainer.hasData(PermissionPlayerLoader.PERMISSION_PLAYER_KEY))
			return CommandResult.INVALID_ARGUMENTS;
		
		PermissionPlayer player = this.chainer.getObject(PermissionPlayerLoader.PERMISSION_PLAYER_KEY);
		
		// build the prefix
		StringBuffer buffer = new StringBuffer();
		for(int i = 1; i < args.length; ++i) {
			buffer.append(args[i]);
			buffer.append(" ");
		}
		buffer.deleteCharAt(buffer.length()-1);
		String name = buffer.toString().replace("&", "§");
		
		// set and save the group name
		player.setSuffix(name);
		if(!player.save())
			return new CommandResult(Prefix.NEGATIVE + KaranteeniPerms.getTranslator().getTranslation(
					this.chainer.getPlugin(), sender, "permissions.save-failed"), ResultType.ERROR, Sounds.NO.get());
		
		KaranteeniPerms.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.POSITIVE + KaranteeniPerms.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
						"permissions.player.set.suffix").replace("%player%", args[0]).replace("%suffix%", name));
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.player.set.suffix", 
				"Set the suffix of %player% to %suffix%");
		KaranteeniPerms.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"permissions.save-failed", "Failed to save modifications!");
	}
}
