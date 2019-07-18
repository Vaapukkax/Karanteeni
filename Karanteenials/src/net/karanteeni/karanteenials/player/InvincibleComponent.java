package net.karanteeni.karanteenials.player;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

public class InvincibleComponent extends CommandComponent implements TranslationContainer {

	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		List<Player> players = this.chainer.getList("core.players");
		Object invincible_ = this.chainer.getData("core.binary");
		boolean invincible = false;
		
		// incorrect param given
		if(invincible_ == null) {
			return CommandResult.INVALID_ARGUMENTS;
		} else {
			invincible = ((Boolean)invincible_).booleanValue();
		}
		
		// no players given, feed self
		if(players == null) {
			// if console, prevent
			if(!(sender instanceof Player)) {
				return CommandResult.NOT_FOR_CONSOLE;
			}
			
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.invincible.self")) {
				return CommandResult.NO_PERMISSION;
			}
			
			KPlayer.getKPlayer((Player)sender).setInvincible(invincible);
			
			if(invincible) // enable invincibility
				Karanteenials.getMessager().sendActionBar(
					sender, 
					Sounds.SETTINGS.get(), 
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, "player-command.invincible-self-enabled"));
			else // disable invincibility
				Karanteenials.getMessager().sendActionBar(
						sender, 
						Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.invincible-self-disabled"));
			return CommandResult.SUCCESS;
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.invincible.other")) {
				return CommandResult.NO_PERMISSION;
			}
			
			// loop and feed each player
			for(Player player : players) {
				KPlayer.getKPlayer(player).setInvincible(invincible);
				
				if(invincible) // play enabled message
					Karanteenials.getMessager().sendActionBar(
						player, 
						Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.invincible-enabled"));
				else // play disabled message
					Karanteenials.getMessager().sendActionBar(
						player, 
						Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.invincible-disabled"));
			}
			
			// message about the feeding to the sender
			if(invincible) // invincibility enabled message
				Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.invincible-others-enabled")
					.replace("%players%", ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ")));
			else // invincibility disabled message
				Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.invincible-others-disabled")
					.replace("%players%", ArrayFormat.joinSort(ArrayFormat.playersToArray(players), ", ")));
			return CommandResult.SUCCESS;
		}
	}
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.invincible-self-enabled", 
				"You are now invincible");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.invincible-self-disabled", 
				"You are no longer invincible");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.invincible-enabled", 
				"You have gained invincibility");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.invincible-disabled", 
				"You have lost your invincibility");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.invincible-others-enabled", 
				"You made the following players invincible: %players%");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.invincible-others-disabled", 
				"You have taken the invincibility away from the following players: %players%");
	}
}
