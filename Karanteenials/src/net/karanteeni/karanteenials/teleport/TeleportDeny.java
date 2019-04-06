package net.karanteeni.karanteenials.teleport;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

public class TeleportDeny extends AbstractCommand implements TranslationContainer {
	public TeleportDeny(Karanteenials plugin) {
		super(plugin, "tpdeny", "/tpdeny", "Denies an existing teleport request", 
				Karanteenials.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(!(sender instanceof Player)) {
			Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Karanteenials.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		Player player = (Player)sender;
		KPlayer kp = KPlayer.getKPlayer(player);
		
		if(!kp.dataExists(new NamespacedKey(plugin, "teleport-request")))
		{
			Karanteenials.getMessager().sendMessage(player, Sounds.NO.get(), 
					Prefix.NEGATIVE+
					Karanteenials.getTranslator().getTranslation(plugin, player, "teleport-ask.no-existing-request"));
			return true;
		}
		
		TeleportRequest req = (TeleportRequest)kp.getData(new NamespacedKey(plugin, "teleport-request"));
		req.declineRequest();
		
		return true;
	}

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.no-existing-request", 
				"You don't have any existing teleport requests!");
	}
}
