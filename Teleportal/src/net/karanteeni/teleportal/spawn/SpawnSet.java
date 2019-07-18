package net.karanteeni.teleportal.spawn;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class SpawnSet extends CommandChainer implements TranslationContainer {

	public SpawnSet() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"setspawn", 
				"/setspawn", 
				"Sets the /spawn location", 
				Teleportal.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}


	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				plugin, 
				"spawn.set", 
				"Set the /spawn location");
	}


	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			return CommandResult.NOT_FOR_CONSOLE;
		}
		
		// set the spawn location to config
		SpawnManager sm = new SpawnManager((Teleportal)plugin);
		sm.setSpawn(((Player)sender).getLocation());
		
		// message about the set
		Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL +
				Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.set"));
		return CommandResult.SUCCESS;
	}
}
