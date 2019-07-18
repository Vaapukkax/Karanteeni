package net.karanteeni.karanteenials.player;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class LightningComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		List<Player> players = this.chainer.getList("core.players");
		
		// no players given, feed self
		if(players == null) {
			// if console, prevent
			if(!(sender instanceof Player)) {
				return CommandResult.NOT_FOR_CONSOLE;
			}
			
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.lightning.self")) {
				return CommandResult.NO_PERMISSION;
			}
			
			Location loc = ((Player)sender).getLocation();
			loc.getWorld().strikeLightning(loc);
			return CommandResult.SUCCESS;
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.lightning.other")) {
				return CommandResult.NO_PERMISSION;
			}
			
			// loop and feed each player
			int i = 0;
			
			while(i < players.size()) {
				// if player is burning players, check their power levels
				if(sender instanceof Player) {
					// if the power level of burned is higher than the burner
					if(((Karanteenials)this.chainer.getPlugin()).getPlayerData().getPowerLevel().getPowerLevel(players.get(i)) > 
							((Karanteenials)this.chainer.getPlugin()).getPlayerData().getPowerLevel().getPowerLevel((Player)sender)) {
						players.remove(i);
						continue;
					}
				}
				
				
				Location loc = players.get(i++).getLocation();
				loc.getWorld().strikeLightning(loc);
			}
			
			// message about the feeding to the sender
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.lightning-others")
					.replace("%players%", ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ")));
			return CommandResult.SUCCESS;
		}
	}
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.lightning-others", 
				"You have struck the following players: %players%");
	}
}
