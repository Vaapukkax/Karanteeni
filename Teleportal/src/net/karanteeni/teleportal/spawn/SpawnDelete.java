package net.karanteeni.teleportal.spawn;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class SpawnDelete extends CommandChainer implements TranslationContainer {

	public SpawnDelete() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"delspawn", 
				"/delspawn", 
				"Deletes the set spawn location", 
				Teleportal.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(plugin, "spawn.delete", "Deleted the spawn location");
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		SpawnManager sm = new SpawnManager((Teleportal)plugin);
		
		// if spawn has not been set message about it
		if(!sm.isSpawnSet()) {
			return new CommandResult(Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.not-set"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		// delete the spawn
		sm.deleteSpawn();
		
		// message about the spawn deletion
		Teleportal.getMessager().sendMessage(sender, 
				Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + 
				Teleportal.getTranslator().getTranslation(plugin, sender, "spawn.delete"));
		
		return CommandResult.SUCCESS;
	}
}
