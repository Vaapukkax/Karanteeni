package net.karanteeni.teleportal.respawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;
import net.karanteeni.teleportal.spawn.SpawnManager;

public class RespawnSet extends CommandComponent implements TranslationContainer {

	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"respawn.set", 
				"Set the respawn location");
	}


	@Override
	protected void onRegister() {
		registerTranslations();
	}


	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(!(sender instanceof Player)) {
			return CommandResult.NOT_FOR_CONSOLE;
		}
		
		// set the spawn location to config
		SpawnManager sm = new SpawnManager((Teleportal)this.chainer.getPlugin());
		sm.setRespawn(((Player)sender).getLocation());
		
		// message about the set
		Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL +
				Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "respawn.set"));
		return CommandResult.SUCCESS;
	}
}
