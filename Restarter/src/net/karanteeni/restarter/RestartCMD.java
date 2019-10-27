package net.karanteeni.restarter;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;

public class RestartCMD extends BareCommand implements TranslationContainer {

	public RestartCMD() {
		super(Restarter.getPlugin(Restarter.class), 
				"restart", 
				"restart", 
				"Restarts the server", 
				Restarter.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// no autofill
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// starting restart
		Restarter plugin = (Restarter)(this.plugin);
		
		if(plugin.restart()) {
			Restarter.getMessager().sendActionBar(sender, Sounds.NOTIFICATION.get(), 
					Restarter.getTranslator().getTranslation(plugin, sender, "started-restart"));
		} else {
			Restarter.getMessager().sendActionBar(sender, Sounds.NO.get(), 
					Restarter.getTranslator().getTranslation(plugin, sender, "restart-already-active"));
		}
		
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Restarter.getTranslator().registerTranslation(plugin, "started-restart", "Restarting server");
		Restarter.getTranslator().registerTranslation(plugin, "restart-already-active", "Server is already restarting");
	}
}
