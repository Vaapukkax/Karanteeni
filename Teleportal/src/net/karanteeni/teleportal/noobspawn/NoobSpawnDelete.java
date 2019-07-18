package net.karanteeni.teleportal.noobspawn;

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
import net.karanteeni.teleportal.spawn.SpawnManager;

public class NoobSpawnDelete extends CommandChainer implements TranslationContainer {

	public NoobSpawnDelete() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"delnoobspawn", 
				"/delnoobspawn", 
				"Deletes the set noobspawn location", 
				Teleportal.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(plugin, "noobspawn.delete", "Deleted the noobspawn location");
		Teleportal.getTranslator().registerTranslation(plugin, "noobspawn.not-set", "NoobSpawn has not been set");
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		SpawnManager sm = new SpawnManager((Teleportal)plugin);
		
		// if spawn has not been set message about it
		if(!sm.isNoobSpawnSet()) {
			return new CommandResult(Teleportal.getTranslator().getTranslation(plugin, sender, "noobspawn.not-set"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		// delete the spawn
		sm.deleteNoobSpawn();
		
		// message about the spawn deletion
		Teleportal.getMessager().sendMessage(sender, 
				Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + 
				Teleportal.getTranslator().getTranslation(plugin, sender, "noobspawn.delete"));
		
		return CommandResult.SUCCESS;
	}
}
