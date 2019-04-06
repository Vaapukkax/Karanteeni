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

public class TeleportAccept extends AbstractCommand implements TranslationContainer {
	private boolean safe;
	
	public TeleportAccept(Karanteenials plugin) {
		super(plugin, "tpaccept", "/tpaccept", "Accepts an existing teleport request", 
				Karanteenials.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
		
		if(!plugin.getConfig().isSet("teleport.request-safe")) {
			plugin.getConfig().set("teleport.request-safe", true);
			plugin.saveConfig();
		}
		safe = plugin.getConfig().getBoolean("teleport.request-safe");
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
		req.acceptRequest(safe, this::teleportFailed);
		
		return true;
	}

	/**
	 * Run if the teleport was unsuccessful
	 * @param asker sender of teleport request
	 * @param rec receiver of teleport request
	 */
	private void teleportFailed(Player asker, Player rec)
	{
		if(asker != null && asker.isOnline())
			Karanteenials.getMessager().sendMessage(asker, Sounds.NO.get(), 
					Prefix.NEGATIVE+Karanteenials.getTranslator().getTranslation(plugin, asker, "teleport-ask.asker.failed")
					.replace("%player%", rec.getName()));
		if(rec != null && rec.isOnline())
			Karanteenials.getMessager().sendMessage(rec, Sounds.NO.get(), 
					Prefix.NEGATIVE+Karanteenials.getTranslator().getTranslation(plugin, rec, "teleport-ask.receiver.failed")
					.replace("%player%", asker.getName()));
	}
	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.no-existing-request", 
				"You don't have any existing teleport requests!");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.asker.failed", 
				"Teleportation with %player% failed!");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.receiver.failed", 
				"Teleportation with %player% failed!");
	}
}
