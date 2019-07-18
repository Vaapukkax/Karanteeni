package net.karanteeni.karanteenials.player;

import java.util.List;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class ExtinguishComponent extends CommandComponent implements TranslationContainer {

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
			if(!sender.hasPermission("karanteenials.player.command.extinguish.self")) {
				return CommandResult.NO_PERMISSION;
			}
			
			((Player)sender).setFireTicks(0); // extinguish the player
			
			Karanteenials.getMessager().sendActionBar(
					sender, 
					new SoundType(Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.4f),
					Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), sender, 
							"player-command.extinguish-self"));
			return CommandResult.SUCCESS;
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.extinguish.other")) {
				return CommandResult.NO_PERMISSION;
			}
			
			for(Player player : players) {
				player.setFireTicks(0);
				Karanteenials.getMessager().sendActionBar(
						player, 
						new SoundType(Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.4f),
						Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), player, 
								"player-command.extinguished"));
			}
			
			// message about the feeding to the sender
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.extinguish-others")
					.replace("%players%", ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ")));
			return CommandResult.SUCCESS;
		}
	}
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.extinguish-self", 
				"You extinguished yourself");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.extinguished", 
				"You have been extinguished");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.extinguish-others", 
				"You have extinguished the following players: %players%");
	}
}
