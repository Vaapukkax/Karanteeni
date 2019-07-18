package net.karanteeni.nature.commands.time;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

public class TimeSetComponent extends CommandComponent implements TranslationContainer {

	public TimeSetComponent(TickParser tp) {
		super(tp);
	}

	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"time.set", 
				"Set the time to %ticks%");
	}

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		// get the default world
		long tickTime = this.chainer.getObject("ticks");
		Bukkit.getWorlds().get(0).setTime(tickTime);
		
		Katura.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
				Katura.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "time.set")
				.replace("%ticks%", Long.toString(tickTime)));
		return CommandResult.SUCCESS;
	}
}
