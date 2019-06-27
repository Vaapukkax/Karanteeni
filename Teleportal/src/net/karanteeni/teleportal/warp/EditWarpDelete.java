package net.karanteeni.teleportal.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class EditWarpDelete extends CommandComponent implements TranslationContainer {
	
	public EditWarpDelete(CommandLoader component) {
		super(component);
	}
	
	
	@Override
	protected boolean runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		Warp warp = this.chainer.<Warp>getObject("warp");
		if(!warp.delete())
			return false;
		
		Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.POSITIVE +
				Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), 
						sender, 
						"warp.deleted")
				.replace("%warpname%", warp.getDisplayName())
				.replace("%warp%", warp.getName()));
		
		return true;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), "warp.deleted", 
				"You deleted warp %warpname% (%warp%)");
	}
}
