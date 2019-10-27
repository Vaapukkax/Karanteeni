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

public class FeedComponent extends CommandComponent implements TranslationContainer {

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
			if(!sender.hasPermission("karanteenials.player.command.feed.self")) {
				return CommandResult.NO_PERMISSION;
			}
			
			((Player)sender).setExhaustion(0);
			((Player)sender).setFoodLevel(20);
			((Player)sender).setSaturation(20);
			
			SoundType sound = new SoundType(
					new Sound[] { Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, Sound.ENTITY_PLAYER_BURP }, 
					new Float[] { 1f, 1f }, 
					new Float[] { 0.0f, 1.8f });
			Karanteenials.getMessager().sendActionBar(
					sender, 
					sound, 
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, "player-command.feed-self"));
			return CommandResult.SUCCESS;
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.feed.other")) {
				return CommandResult.NO_PERMISSION;
			}
			
			// loop and feed each player
			for(Player player : players) {
				player.setExhaustion(0);
				player.setFoodLevel(20);
				player.setSaturation(20);
				
				SoundType sound = new SoundType(
						new Sound[] { Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, Sound.ENTITY_PLAYER_BURP }, 
						new Float[] { 1f, 1f }, 
						new Float[] { 0.0f, 1.8f });
				Karanteenials.getMessager().sendActionBar(
						player, 
						sound, 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.fed"));
			}
			
			// message about the feeding to the sender
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.feed-others")
					.replace("%players%", ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ")));
			return CommandResult.SUCCESS;
		}
	}
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.feed-self", 
				"Your tummy is now full");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.fed", 
				"You have been fed");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.feed-others", 
				"You have fed the following players: %players%");
	}
}
