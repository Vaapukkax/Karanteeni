package net.karanteeni.bungee.event;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLogin implements Listener {
	
	@EventHandler
	public void postLoginEvent(PostLoginEvent event)
	{
		ProxiedPlayer player = event.getPlayer();
		player.sendMessage(new TextComponent(ChatColor.GOLD + "Welcome to the server!"));
	}
}
