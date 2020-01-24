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

public class IgnoreAddComponent extends CommandComponent implements TranslationContainer {

	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), "ignore.add", "You ignored the player %player%");
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), "ignore.cannot-ignore", "You cannot ignore %player%");
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), "ignore.not-yourself", "You cannot ignore yourself");
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), "ignore.already-ignored", "You have already ignored the player %player%");
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		
		UUID uuid = null;
		Player blocked_ = null;
		if(this.chainer.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE))
			uuid = this.chainer.getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE);
		else {			
			blocked_ = this.chainer.getObject(PlayerLoader.PLAYER_KEY_SINGLE);
			uuid = blocked_.getUniqueId();
		}
		
		// if no player was found the given arguments were invalid
		if(uuid == null)
			return CommandResult.INVALID_ARGUMENTS;
		Player p = (Player)sender;
		if(uuid.equals(p.getUniqueId()))
			return new CommandResult(
					Chatar.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "ignore.not-yourself"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		
		// effectively final variables
		UUID uid = uuid;
		Chatar plugin = Chatar.getPlugin(Chatar.class);
		String playerName = args[0];
		Player blocked = blocked_;
		
		// create a callback component as the addition requires database connection
		new CallBackComponent(this.chainer.getPlugin(), sender, true) {
			CommandResult result = CommandResult.SUCCESS;
			
			@Override
			public void run() {
				IgnoreData data = plugin.getIgnoreData();
				Player player = (Player)sender;
				
				// check if the player cannot be ignored
				if(plugin.getPermissionChecker() != null) {
					// if the player has this permission you may not block them
					if(plugin.getPermissionChecker().hasPermission(uid, "chatar.ignore.immune")) {
						this.result = new CommandResult(
								Chatar.getTranslator().getTranslation(plugin, sender, "ignore.cannot-ignore")
								.replace("%player%", playerName),
								ResultType.INVALID_ARGUMENTS,
								Sounds.NO.get());
						return;
					}
				} else {
					if(blocked != null && blocked.hasPermission("ignore.cannot-ignore")) {
						this.result = new CommandResult(
								Chatar.getTranslator().getTranslation(plugin, sender, "ignore.cannot-ignore")
								.replace("%player%", playerName),
								ResultType.INVALID_ARGUMENTS,
								Sounds.NO.get());
						return;
					}
				}
				
				QueryState result = data.addIgnore(player.getUniqueId(), uid);
				
				// create the commandresult
				if(result == QueryState.INSERTION_FAIL_ALREADY_EXISTS)
					this.result = new CommandResult(
							Chatar.getTranslator().getTranslation(plugin, sender, "ignore.already-ignored")
							.replace("%player%", playerName),
							ResultType.INVALID_ARGUMENTS,
							Sounds.NO.get());
				else if(result == QueryState.INSERTION_FAIL_OTHER)
					this.result = CommandResult.ERROR;
			}

			
			@Override
			public CommandResult callback() {
				if(!this.result.equals(CommandResult.SUCCESS))
					return result;
				
				// message about the addition
				Chatar.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
						Prefix.NEUTRAL + Chatar.getTranslator().getTranslation(plugin, sender, "ignore.add")
						.replace("%player%", playerName));
				return result;
			}
		}.execute();
		
		return CommandResult.ASYNC_CALLBACK;
	}
}
