package net.karanteeni.karanteenials.player;

import java.util.List;
import org.bukkit.Bukkit;
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

public class GodComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected boolean runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		List<Player> players = this.chainer.getList("core.players");
		Bukkit.broadcastMessage("main");
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
			if(!sender.hasPermission("karanteenials.player.command.god.self")) {
				this.noPermission(sender);
				return false;
			}
			
			((Player)sender).setExhaustion(0);
			((Player)sender).setFoodLevel(20);
			
			SoundType sound = new SoundType(
					new Sound[] { Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, Sound.ENTITY_PLAYER_BURP }, 
					new Float[] { 1f, 1f }, 
					new Float[] { 0.0f, 1.8f });
			Karanteenials.getMessager().sendActionBar(
					sender, 
					sound, 
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, "player-command.god-self"));
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.god.other")) {
				this.noPermission(sender);
				return false;
			}
			
			// loop and feed each player
			for(Player player : players) {
				player.setExhaustion(0);
				player.setFoodLevel(20);
				
				SoundType sound = new SoundType(
						new Sound[] { Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, Sound.ENTITY_PLAYER_BURP }, 
						new Float[] { 1f, 1f }, 
						new Float[] { 0.0f, 1.8f });
				Karanteenials.getMessager().sendActionBar(
						player, 
						sound, 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.godded"));
			}
			
			// message about the feeding to the sender
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.god-others")
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
				"player-command.god-self-enabled", 
				"You are now a god");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.god-self-disabled", 
				"You are no longer a god");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.godded-enabled", 
				"You have gained some strength");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.godded-disabled", 
				"You have lost some strength");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.god-others-enabled", 
				"You made the following players gods: %players%");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.god-others-disabled", 
				"You have taken the divinity away from the following players: %players%");
	}
}
