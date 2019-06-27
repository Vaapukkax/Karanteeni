package net.karanteeni.karanteenials.player;

import java.util.Collection;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class HealComponent extends CommandComponent implements TranslationContainer {

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
			if(!sender.hasPermission("karanteenials.player.command.heal.self")) {
				this.noPermission(sender);
				return false;
			}
			
			((Player)sender).setExhaustion(0);
			((Player)sender).setFoodLevel(20);
			((Player)sender).setHealth(20);
			Collection<PotionEffect> effects = ((Player)sender).getActivePotionEffects();
			for(PotionEffect effect : effects)
				((Player)sender).removePotionEffect(effect.getType());
			
			Karanteenials.getMessager().sendActionBar(
					sender, 
					Sounds.NONE.get(), 
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, "player-command.heal-self"));
		} else {
			// feed multiple players
			// does sender have the required permission
			if(!sender.hasPermission("karanteenials.player.command.heal.other")) {
				this.noPermission(sender);
				return false;
			}
			
			// loop and feed each player
			for(Player player : players) {
				player.setExhaustion(0);
				player.setFoodLevel(20);
				player.setHealth(20);
				Collection<PotionEffect> effects = player.getActivePotionEffects();
				for(PotionEffect effect : effects)
					player.removePotionEffect(effect.getType());
				
				Karanteenials.getMessager().sendActionBar(
						player, 
						Sounds.NONE.get(), 
						Karanteenials.getTranslator().getTranslation(
								this.chainer.getPlugin(), 
								sender, "player-command.healed"));
			}
			
			// message about the feeding to the sender
			Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"player-command.heal-others")
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
				"player-command.heal-self", 
				"You feel now healthier");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.healed", 
				"You have been healed");
		Karanteenials.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"player-command.heal-others", 
				"You have healed the following players: %players%");
	}
}
