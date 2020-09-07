package net.karanteeni.chatar.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.karanteeni.core.information.ChatColor;

public class ChatFormatter implements Listener {
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.getPlayer().hasPermission("chatar.chat.rgb")) {
			event.setMessage(ChatColor.translateHexColorCodes(event.getMessage()));
		}
		
		if(event.getPlayer().hasPermission("chatar.chat.color")) {
			event.setMessage(ChatColor.translateColor(event.getMessage()));
		}
		
		if(event.getPlayer().hasPermission("chatar.chat.format")) {
			event.setMessage(ChatColor.translateFormat(event.getMessage()));
		}
		
		if(event.getPlayer().hasPermission("chatar.chat.magic")) {
			event.setMessage(ChatColor.translateMagic(event.getMessage()));
		}
	}
}
