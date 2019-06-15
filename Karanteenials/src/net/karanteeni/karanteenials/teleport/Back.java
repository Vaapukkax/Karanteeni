package net.karanteeni.karanteenials.teleport;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

public class Back extends AbstractCommand implements TranslationContainer{
	private boolean safeback = true;
	
	public Back(KaranteeniPlugin plugin) {
		super(plugin, "back", 
				"/back", 
				"Teleports you to previous location", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
		
		if(!plugin.getConfig().isSet("teleport.back-safe")) {
			plugin.getConfig().set("teleport.back-safe", true);
			plugin.saveConfig();
		}
		
		safeback = plugin.getConfig().getBoolean("teleport.back-safe");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		//Check permissions
		if(!sender.hasPermission("karanteenials.teleport.back")) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+ KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		if(!(sender instanceof Player)) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+ KaranteeniPlugin.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		Player player = (Player)sender;
		
		net.karanteeni.karanteenials.functionality.Back back = new net.karanteeni.karanteenials.functionality.Back(player);
		Location loc = back.getBackLocation();
		
		if(loc == null) {
			KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.NO.get(), 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, 
							"location.no-previous"));
			return true;
		}
		
		Location ploc = player.getLocation();
		
		back.setBackLocation(ploc);
		Teleporter teleporter = new Teleporter(loc);
		teleporter.teleport(player, safeback);
		KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.TELEPORT.get(), 
				KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, 
						"location.teleported-to-previous"));
		
		return true;
	}

	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				"location.no-previous", 
				"You don't have a previous location");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				"location.teleported-to-previous", 
				"Teleported to previous location");
	}

}
