package net.karanteeni.chatar.command.ignore;

import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.command.CallBackComponent;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

public class IgnoreRemoveComponent extends CommandComponent implements TranslationContainer {

	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), "ignore.remove", "You no longer ignore the player %player%");
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), "ignore.not-ignoring", "You are not ignoring the player %player%");
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		
		UUID uuid = null;
		if(this.chainer.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE))
			uuid = this.chainer.getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE);
		else {			
			Player player = this.chainer.getObject(PlayerLoader.PLAYER_KEY_SINGLE);
			uuid = player.getUniqueId();
		}
		
		// if no player was found the given arguments were invalid
		if(uuid == null)
			return CommandResult.INVALID_ARGUMENTS;
		
		// effectively final variable
		UUID uid = uuid;
		Chatar plugin = Chatar.getPlugin(Chatar.class);
		String playerName = args[0];
		
		// create a callback component as the addition requires database connection
		new CallBackComponent(this.chainer.getPlugin(), sender, true) {
			CommandResult result = CommandResult.SUCCESS;
			
			@Override
			public void run() {
				IgnoreData data = plugin.getIgnoreData();
				Player player = (Player)sender;
				QueryState result = data.removeIgnore(player.getUniqueId(), uid);
				
				// create the commandresult
				if(result == QueryState.REMOVAL_FAIL_NO_DATA)
					this.result = new CommandResult(
							Chatar.getTranslator().getTranslation(plugin, sender, "ignore.not-ignoring")
							.replace("%player%", playerName),
							ResultType.INVALID_ARGUMENTS,
							Sounds.NO.get());
				else if(result == QueryState.REMOVAL_FAIL_OTHER)
					this.result = CommandResult.ERROR;
			}

			
			@Override
			public CommandResult callback() {
				if(!this.result.equals(CommandResult.SUCCESS))
					return result;
				
				// message about the addition
				Chatar.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
						Prefix.NEUTRAL + Chatar.getTranslator().getTranslation(plugin, sender, "ignore.remove")
						.replace("%player%", playerName));
				return result;
			}
		}.execute();
		
		return CommandResult.ASYNC_CALLBACK;
	}
}
