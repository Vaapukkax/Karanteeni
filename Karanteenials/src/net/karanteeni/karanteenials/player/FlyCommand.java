package net.karanteeni.karanteenials.player;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;
import net.karanteeni.karanteenials.functionality.Fly;

public class FlyCommand extends AbstractCommand implements TranslationContainer {

	public FlyCommand(Karanteenials plugin) {
		super(plugin, "fly", "/fly [player] [on/off]", "Sets the fly on or off", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), Arrays.asList("on","off"));
		registerTranslations();
	}

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "fly-on", 
				"You can now fly");
		Karanteenials.getTranslator().registerTranslation(plugin, "fly-off", 
				"You cannot fly anymore");
		Karanteenials.getTranslator().registerTranslation(plugin, "fly-on-other", 
				"You set the flight of %player% to true");
		Karanteenials.getTranslator().registerTranslation(plugin, "fly-off-other", 
				"You set the flight of %player% to false");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 0) { //Toggle own fly
			if(!(sender instanceof Player)) {
				sendOnlyPlayerMessage(sender);
				return true;
			}
			if(!sender.hasPermission("karanteenials.player.fly.self")) {
				sendNoPermissionMessage(sender);
				return true;
			}
			Fly fly = new Fly((Karanteenials)plugin);
			Player player = (Player)sender;
			boolean flyOn;
			if(!fly.setFly(player.getUniqueId(), flyOn = !fly.isFlyOn(player.getUniqueId()))) {
				sendDatabaseError(sender);
				return true;
			}
			
			if(flyOn)
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
					Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-on"));
			else
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-off"));
			
		} else if(args.length == 1) { //Set own fly to on or off
			if(!(sender instanceof Player)) {
				sendOnlyPlayerMessage(sender);
				return true;
			}
			if(!sender.hasPermission("karanteenials.player.fly.self")) {
				sendNoPermissionMessage(sender);
				return true;
			}
			
			Fly fly = new Fly((Karanteenials)plugin);
			Player player = (Player)sender;
			boolean flyOn;
			if(!fly.setFly(player.getUniqueId(), flyOn = getRealParam(args[0]).equals("on"))) {
				sendDatabaseError(sender);
				return true;
			}
			
			if(flyOn)
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
					Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-on"));
			else
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-off"));
			
		} else if(args.length == 2) { //Set someone elses fly to on or off
			if(!sender.hasPermission("karanteenials.player.fly.other")) {
				sendNoPermissionMessage(sender);
				return true;
			}
			
			Player player = Bukkit.getPlayer(args[0]);
			if(player == null) {
				Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
						Prefix.NEGATIVE+
						Karanteenials.getDefaultMsgs().playerNotFound(sender, args[0]));
				return true;
			}
			
			Fly fly = new Fly((Karanteenials)plugin);
			boolean flyOn;
			if(!fly.setFly(player.getUniqueId(), flyOn = getRealParam(args[1]).equals("on"))) {
				sendDatabaseError(sender);
				return true;
			}
			
			if(flyOn) {
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
					Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-on"));
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-on-other")
						.replace("%player%", player.getDisplayName()));
			} else {
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-off"));
				Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
						Karanteenials.getTranslator().getTranslation(plugin, sender, "fly-off-other")
						.replace("%player%", player.getDisplayName()));
			}
		} else {
			sendInvalidArgsMessage(sender);
		}
		
		return true;
	}
	
	private void sendDatabaseError(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE+
				Karanteenials.getDefaultMsgs().databaseError(sender));
	}
	
	private void sendOnlyPlayerMessage(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE+
				Karanteenials.getDefaultMsgs().defaultNotForConsole());
	}
	
	private void sendNoPermissionMessage(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE+
				Karanteenials.getDefaultMsgs().noPermission(sender));
	}
	
	/**
	 * Sends the invalid arguments message to the given command sender
	 * @param sender writer of the invalid arguments
	 */
	private void sendInvalidArgsMessage(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().incorrectParameters(sender));
	}
}
