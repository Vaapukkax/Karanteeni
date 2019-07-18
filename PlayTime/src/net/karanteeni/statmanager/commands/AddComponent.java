package net.karanteeni.statmanager.commands;

import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.command.defaultcomponent.TimeComponent;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.time.TimeData;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.statmanager.StatManager;
import net.karanteeni.statmanager.Time;

public class AddComponent extends CommandComponent implements TranslationContainer {
	
	@Override
	protected void onRegister() { 
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		UUID uuid = null;
		if(chainer.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE))
			uuid = chainer.getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE);
		else if(chainer.hasData(PlayerLoader.PLAYER_KEY_SINGLE))
			uuid = chainer.<Player>getObject(PlayerLoader.PLAYER_KEY_SINGLE).getUniqueId();
		
		TimeData timeToSet = this.chainer.getObject(TimeComponent.TIME_KEY);
		String playerName = StatManager.getPlayerHandler().getName(uuid);
		
		Time playerTime = ((StatManager)chainer.getPlugin()).getManager().forceLoadTime(uuid);
		
		playerTime.add(timeToSet.getMilliseconds());
		if(!playerTime.save()) return CommandResult.ERROR;
		
		StatManager.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL +
				StatManager.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "add-time")
				.replace("%player%", playerName)
				.replace("%time%", ((PlayTimeCommand)chainer).formatTime(timeToSet)));
		
		return CommandResult.SUCCESS;
	}
	

	@Override
	public void registerTranslations() {
		StatManager.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"add-time", "Gave %time% more play time to %player%");
	}
}
