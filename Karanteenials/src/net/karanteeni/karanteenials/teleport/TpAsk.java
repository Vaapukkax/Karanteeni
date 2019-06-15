package net.karanteeni.karanteenials.teleport;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;
import net.karanteeni.karanteenials.teleport.TeleportRequest.TeleportType;

public class TpAsk extends AbstractCommand implements TranslationContainer {
	public TpAsk(Karanteenials plugin) {
		super(plugin, "tpa", "/tpa <player>", "Asks for permission to teleport from player", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
		
		registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		//Does player have permission to ask tpa
		if(!sender.hasPermission("karanteenials.teleport.ask")) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(),
					KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		if(!(sender instanceof Player)) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		
		//Check if there was enough arguments
		if(args.length != 1) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
			return true;
		}
		
		Player player = (Player)sender;
		//Get the player to teleport to
		Player destination = Bukkit.getPlayer(args[0]);
		
		//If player could not be found send the message that it could not be found
		if(destination == null) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + KaranteeniPlugin.getDefaultMsgs().playerNotFound(sender, args[0]));
			return true;
		}
		
		//Check that player is not trying to teleport to itself
		if(player.getUniqueId().equals(destination.getUniqueId())) {
			Karanteenials.getMessager().sendActionBar(player, Sounds.NO.get(),
					Karanteenials.getTranslator().getTranslation(plugin, player, "teleport-ask.not-to-self"));
			return true;
		}
		
		//Check if tptoggle allows
		boolean allowed = ((Karanteenials)plugin).getPlayerData().getTpToggle().doesTeleportToggleAllow(player, destination);
		if(allowed) { //Check if player is blocked
			allowed = !((Karanteenials)plugin).getPlayerData().getBlockPlayer().isBlocked(player, destination);
		}
		
		//If teleport was not allowed,
		if(!allowed) {
			Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + 
					Karanteenials.getTranslator().getTranslation(plugin, sender, "teleport-ask.receiver.blocked")
					.replace("%player%", destination.getName()));
			return true;
		}
		
		//Send message to sender that the teleport request has been sent
		Karanteenials.getMessager().sendMessage(player, Sounds.NONE.get(),
				Prefix.NEUTRAL+
				Karanteenials.getTranslator().getTranslation(plugin, player, "teleport-ask.asker.there.sent")
				.replace("%player%", destination.getName()));
		//Send message to receiver that there is a new teleport request
		Karanteenials.getMessager().sendMessage(destination, Sounds.NONE.get(),
				Prefix.NEUTRAL+
				Karanteenials.getTranslator().getTranslation(plugin, destination, "teleport-ask.receiver.there.received")
				.replace("%player%", player.getName()));
		
		TeleportRequest req = new TeleportRequest(KPlayer.getKPlayer(player), KPlayer.getKPlayer(destination), TeleportType.THERE);
		req.setAcceptedMethod(this::tpAccepted);
		req.setDeniedMethod(this::tpDenied);
		req.setTimeoutMethod(this::tpTimeout);
		req.setOverwritten(this::tpOverwritten);
		
		return true;
	}
	
	/**
	 * Called when the tp request is accepted
	 * @param asker Request sender
	 * @param rec Request receiver
	 */
	private void tpAccepted(Player asker, Player rec)
	{
		if(asker != null && asker.isOnline())
			KaranteeniPlugin.getMessager().sendMessage(asker, Sounds.TELEPORT.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(plugin, asker, "teleport-ask.asker.accepted")
					.replace("%player%", rec.getName()));
		if(rec != null && rec.isOnline())
			KaranteeniPlugin.getMessager().sendMessage(rec, Sounds.TELEPORT.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(plugin, rec, "teleport-ask.receiver.accepted")
					.replace("%player%", asker.getName()));
	}
	
	/**
	 * Called when the tp request is denied
	 * @param asker Request sender
	 * @param rec Request receiver
	 */
	private void tpDenied(Player asker, Player rec)
	{
		if(asker != null && asker.isOnline())
			KaranteeniPlugin.getMessager().sendMessage(asker, Sounds.NO.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(plugin, asker, "teleport-ask.asker.denied")
					.replace("%player%", rec.getName()));
		if(rec != null && rec.isOnline())
			KaranteeniPlugin.getMessager().sendMessage(rec, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					Karanteenials.getTranslator().getTranslation(plugin, rec, "teleport-ask.receiver.denied")
					.replace("%player%", asker.getName()));
	}
	
	/**
	 * Sends a message to the player that teleport has expired
	 * @param player
	 */
	private void tpTimeout(Player asker, Player rec) {
		if(rec != null && rec.isOnline())
			KaranteeniPlugin.getMessager().sendMessage(rec, Sounds.NO.get(), 
				Prefix.NEGATIVE + 
				KaranteeniPlugin.getTranslator().getTranslation(plugin, rec, "teleport-ask.receiver.expired"));
		if(asker != null && asker.isOnline())
			KaranteeniPlugin.getMessager().sendMessage(asker, Sounds.NO.get(), 
				Prefix.NEGATIVE + 
				KaranteeniPlugin.getTranslator().getTranslation(plugin, rec, "teleport-ask.asker.expired"));
	}
	
	/**
	 * Sends a message to the player that a previous teleport has been overwritten
	 * @param player
	 */
	private void tpOverwritten(Player player) {
		
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender.hasPermission("karanteenials.teleport.ask") && args.length == 1)
			return getPlayerNames(args[0]);
		return null;
	}

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.asker.there.sent", 
				"You sent a teleport request to %player% to teleport to them");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.receiver.there.received", 
				"%player% wants to teleport to you, /tpaccept to accept and /tpdeny to deny. You have 60 seconds to reply");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.asker.accepted", 
				"%player% accepted your tp request");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.asker.denied", 
				"%player% declined your tp request");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.receiver.accepted", 
				"You accepted the tp request sent by %player%");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.receiver.denied", 
				"You denied the tp request sent by %player%");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.not-to-self", 
				"You cannot send teleport requests to yourself!");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.asker.expired", 
				"A previous teleport request has expired");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.receiver.expired", 
				"Your previous teleport request has expired");
		Karanteenials.getTranslator().registerTranslation(plugin, "teleport-ask.receiver.blocked", 
				"%player% has blocked you from sending teleport requests");
	}
}
