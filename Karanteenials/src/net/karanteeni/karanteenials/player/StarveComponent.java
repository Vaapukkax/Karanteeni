package net.karanteeni.karanteenials.player;

import java.util.List;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class StarveComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected boolean runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		List<Player> players = this.chainer.getList("core.players");
		
		// no players given, feed self
		if(players == null) {
			// if console, prevent
			if(!(sender instanceof Player)) {
				Karanteenials.getMessager().sendMessage(
						sender, 
						Sounds.NO.get(),
						Prefix.NEGATIVE +
						Karanteenials.getDefaultMsgs().defaultNotForConsole());
				return false;
			}
			
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.starve.self")) {
				this.noPermission(sender);
				return false;
			}
			
			((Player)sender).setExhaustion(10);
			((Player)sender).setFoodLevel(1);
			
			SoundType sound = new SoundType(Sound.ENTITY_SQUID_AMBIENT, 1f, 0.5f);
			Karanteenials.getMessager().sendActionBar(
					sender, 
					sound, 
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, "player-command.starve-self"));
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.starve.other")) {
				this.noPermission(sender);
				return false;
			}
			
			// loop and feed each player
			for(Player player : players) {
				player.setExhaustion(10);
				player.setFoodLevel(1);
				
				SoundType sound = new SoundType(Sound.ENTITY_SQUID_AMBIENT, 1f, 0.5f);
				Karanteenials.getMessager().sendActionBar(
						player, 
						sound, 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.starved"));
			}
			
			// message about the feeding to the sender
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.starve-others")
					.replace("%players%", ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ")));
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public void invalidArguments(CommandSender sender) { }
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.starve-self", 
				"Your tummy is now empty");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.starved", 
				"You have been starved");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.starve-others", 
				"You have starved the following players: %players%");
	}
}
