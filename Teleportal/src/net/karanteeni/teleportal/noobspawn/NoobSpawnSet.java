package net.karanteeni.teleportal.noobspawn;

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
import net.karanteeni.teleportal.spawn.SpawnManager;

public class NoobSpawnSet extends CommandChainer implements TranslationContainer {

	public NoobSpawnSet() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"setnoobspawn", 
				"/setnoobspawn", 
				"Sets the first join location", 
				Teleportal.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}


	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				plugin, 
				"noobspawn.set", 
				"Set the first spawn location");
	}


	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			return CommandResult.NOT_FOR_CONSOLE;
		}
		
		// set the spawn location to config
		SpawnManager sm = new SpawnManager((Teleportal)plugin);
		sm.setNoobSpawn(((Player)sender).getLocation());
		
		// message about the set
		Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL +
				Teleportal.getTranslator().getTranslation(plugin, sender, "noobspawn.set"));
		return CommandResult.SUCCESS;
	}
}
