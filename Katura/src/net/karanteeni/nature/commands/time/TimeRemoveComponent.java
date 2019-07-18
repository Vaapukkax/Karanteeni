package net.karanteeni.nature.commands.time;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

public class TimeRemoveComponent extends CommandComponent implements TranslationContainer {

	public TimeRemoveComponent(TickParser tp) {
		super(tp);
	}

	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"time.remove", 
				"Rolled world time back %ticks% ticks");
	}

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		// get the default world
		long tickTime = this.chainer.getObject("ticks");
		tickTime = (Bukkit.getWorlds().get(0).getFullTime() - tickTime);
		Bukkit.getWorlds().get(0).setFullTime(Math.max(tickTime, 0)); 
		
		Katura.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
				Katura.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "time.remove")
				.replace("%ticks%", Long.toString(Math.max(tickTime, 0l))));
		return CommandResult.SUCCESS;
	}
}
