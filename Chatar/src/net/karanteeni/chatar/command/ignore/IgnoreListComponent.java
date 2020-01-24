package net.karanteeni.chatar.command.ignore;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.command.CallBackComponent;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

public class IgnoreListComponent extends CommandComponent implements TranslationContainer {
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Chatar plugin = (Chatar)this.chainer.getPlugin();
		IgnoreData data = plugin.getIgnoreData();
		
		// create a callback component as this is database heavy method
		new CallBackComponent(this.chainer.getPlugin(), sender, true) {
			List<String> names = new LinkedList<String>();
			CommandResult result = CommandResult.SUCCESS;
			
			@Override
			public void run() {
				// load all the ignored players from the database
				Player player = (Player)sender;
				List<UUID> ignored = data.getPlayersIgnoredByPlayer(player.getUniqueId());
				
				// if no data loaded there was an error loading
				if(ignored == null) {
					result = CommandResult.ERROR;
					return;
				}
				
				// get the names of all players ignored
				for(UUID uuid : ignored) {
					String name = Chatar.getPlayerHandler().getName(uuid);
					names.add(name != null ? name : "<unknown>");
				}
			}

			
			@Override
			public CommandResult callback() {
				// send the ignored players to the player
				if(CommandResult.SUCCESS.equals(result)) {
					if(names.size() > 0)
						Chatar.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
								Chatar.getTranslator().getTranslation(plugin, sender, "ignore.list")
								.replace("%names%", ArrayFormat.joinSort(names.toArray(new String[names.size()]), ", ")));
					else
						Chatar.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
								Prefix.NEUTRAL + Chatar.getTranslator().getTranslation(plugin, sender, "ignore.list-empty"));					
				}
				
				return result;
			}
		}.execute();
		
		return CommandResult.ASYNC_CALLBACK;
	}


	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"ignore.list", "========== Ignored players ==========\n%names%\n========== Ignored players ==========");
		Chatar.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"ignore.list-empty", "You are not ignoring anyone");
	}
}
