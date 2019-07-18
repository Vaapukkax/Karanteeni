package net.karanteeni.teleportal.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class EditWarpMove extends CommandComponent implements TranslationContainer {

	public EditWarpMove(CommandLoader component) {
		super(component);
	}
	
	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		// verify that sender is player
		if(!(sender instanceof Player)) {
			return CommandResult.NOT_FOR_CONSOLE;
		}
		
		Warp warp = this.chainer.getObject("warp");
		warp.setLocation(((Player)sender).getLocation());
		
		if(warp.save())
			// message player about warp movement
			Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL +
				Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "warp.moved")
				.replace("%warp%", warp.getName()));
		else
			Teleportal.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
				Prefix.ERROR +
				Teleportal.getDefaultMsgs().databaseError(sender));
		
		return CommandResult.SUCCESS;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"warp.moved", 
				"Moved warp %warp% here");
	}
}
