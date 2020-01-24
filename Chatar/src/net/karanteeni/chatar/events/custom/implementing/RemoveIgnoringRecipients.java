package net.karanteeni.chatar.events.custom.implementing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.chatar.command.ignore.IgnoreData;
import net.karanteeni.chatar.events.custom.PlayerChatEvent;
import net.karanteeni.chatar.events.custom.PlayerMessageEvent;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

/**
 * Removes the recipients based on ignore status
 * @author Nuubles
 *
 */
public class RemoveIgnoringRecipients implements Listener, TranslationContainer {
	Chatar plugin = null;
	
	public RemoveIgnoringRecipients(Chatar plugin) {
		this.plugin = plugin;
		registerTranslations();
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void chatReceive(PlayerChatEvent event) {
		Set<Player> recipients = event.getRecipients();
		
		// check the block status of recipients. If someone has blocked the sender dont send their message
		Player sender = event.getSender();
		UUID senderID = sender.getUniqueId();
		IgnoreData data = plugin.getIgnoreData();
		
		Iterator<Player> iter = recipients.iterator();
		LinkedList<Player> ignoreList = new LinkedList<Player>();
		
		// loop all players and remove the ones who should not receive the message
		while(iter.hasNext()) {
			Player player = iter.next();
			if(data.isIgnoredOnline(player.getUniqueId(), senderID))
				ignoreList.add(player);
		}
		
		// remove the recipients who ignore
		for(Player player : ignoreList)
			event.removeRecipient(player);
	}

	
	@EventHandler(priority = EventPriority.LOW)
	public void messageReceive(PlayerMessageEvent event) {
		Player recipient = event.getRecipient();
		Player sender = event.getSender();
		
		IgnoreData data = plugin.getIgnoreData();
		// if the recipient is ignoring, cancel the event and message the other player
		if(data.isIgnoredOnline(recipient.getUniqueId(), sender.getUniqueId())) {
			event.setCancelled(true);
			
			// message the player that you can't message them
			Chatar.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + 
					Chatar.getTranslator().getTranslation(plugin, sender, "ignore.cannot-send-messages")
					.replace("%player%", recipient.getName()));
		}
	}


	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerTranslation(plugin, 
				"ignore.cannot-send-messages",
				"You cannot send messages to %player%");
	}
}
