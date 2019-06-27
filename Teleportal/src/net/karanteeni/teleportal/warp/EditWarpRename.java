package net.karanteeni.teleportal.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class EditWarpRename extends CommandComponent implements TranslationContainer {

	public EditWarpRename(CommandLoader component) {
		super(component);
	}
	
	
	@Override
	protected boolean runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length != 2) {
			invalidArguments(sender);
			return true;
		}
		
		Warp warp = this.chainer.getObject("warp");
		String oldName = warp.getDisplayName();
		warp.setDisplayName(args[1].replace('_', ' '));
		
		if(warp.save())
			Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "warp.rename")
					.replace("%warp%", warp.getName())
					.replace("%warpname%", oldName)
					.replace("%newname%", warp.getDisplayName()));
		else
			Teleportal.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.ERROR +
					Teleportal.getDefaultMsgs().databaseError(sender));
		
		return true;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), "warp.rename", 
				"Renamed warp %warpname% (%warp%) to %newname%");
	}
}
