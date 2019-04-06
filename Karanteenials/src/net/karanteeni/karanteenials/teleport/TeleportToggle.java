package net.karanteeni.karanteenials.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class TeleportToggle extends AbstractCommand implements TranslationContainer {
	
	public TeleportToggle(Karanteenials plugin) {
		super(plugin, "tptoggle", "/tptoggle", "Disabled tp and tpa from other players", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		//Is this player
		if(!(sender instanceof Player)) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		
		//Does player have the permission required
		if(!sender.hasPermission("karanteenials.teleport.toggle")) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		Player player = (Player)sender;
		
		//Switch the tptoggle of player
		boolean tptoggle = ((Karanteenials)plugin).getPlayerData().getTpToggle().isTpToggleOn(player);
		((Karanteenials)plugin).getPlayerData().getTpToggle().setTpToggle(player, !tptoggle);
		
		if(tptoggle) //tptoggle was turned off
		{
			KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, "tptoggle.off"));
		}
		else //tptoggle was turned on
		{
			KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, "tptoggle.on"));
		}
		return true;
	}

	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "tptoggle.on", "Lower level players cannot now teleport to you");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "tptoggle.off", "Players can now teleport to you");
	}
	
}
