package net.karanteeni.teleportal.respawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;
import net.karanteeni.teleportal.spawn.SpawnManager;

public class RespawnDelete extends CommandComponent implements TranslationContainer {
	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"respawn.delete", 
				"Deleted the respawn location");
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"respawn.not-set", 
				"Respawn has not been set");
	}



	@Override
	protected void onRegister() {
		registerTranslations();
	}


	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		SpawnManager sm = new SpawnManager((Teleportal)this.chainer.getPlugin());
		
		// if spawn has not been set message about it
		if(!sm.isRespawnSet()) {
			return new CommandResult(Teleportal.getTranslator().getTranslation(
						this.chainer.getPlugin(), 
						sender, 
						"respawn.not-set"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		// delete the spawn
		sm.deleteRespawn();
		
		// message about the spawn deletion
		Teleportal.getMessager().sendMessage(sender, 
				Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + 
				Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "respawn.delete"));
		
		return CommandResult.SUCCESS;
	}
}
