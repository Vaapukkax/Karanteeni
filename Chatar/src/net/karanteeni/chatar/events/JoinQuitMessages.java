package net.karanteeni.chatar.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.karanteeni.chatar.Chatar;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class JoinQuitMessages implements Listener {
	private Chatar plugin;
	private static String JOIN_MESSAGE;
	private static String QUIT_MESSAGE;
	
	public JoinQuitMessages(Chatar plugin) {
		this.plugin = plugin;
		plugin.registerFormat("join-message", "§e%player% §ejoined the game");
		plugin.registerFormat("quit-message", "§e%player% §eleft the game");
		
		JOIN_MESSAGE = plugin.getRawFormat("join-message");
		QUIT_MESSAGE = plugin.getRawFormat("quit-message");
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	private void playerHasJoined(PlayerJoinEvent event) {
		Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " joined the game");
		event.setJoinMessage(null);
		
		// generate components for each player
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		HashMap<Player, BaseComponent> messages = new HashMap<Player, BaseComponent>();
		
		for(Player player : players)
			messages.put(player, new TextComponent());
		
		// format the components
		messages = plugin.getFormattedMessage(JOIN_MESSAGE, event.getPlayer(), messages);
		
		
		// message the components to the players
		for(Entry<Player, BaseComponent> entry : messages.entrySet())
		if(entry.getKey().isOnline())
			entry.getKey().spigot().sendMessage(entry.getValue());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	private void playerLeft(PlayerQuitEvent event) {
		Bukkit.getConsoleSender().sendMessage(event.getPlayer().getName() + " left the game");
		event.setQuitMessage(null);
		
		// generate components for each player
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		HashMap<Player, BaseComponent> messages = new HashMap<Player, BaseComponent>();
		
		for(Player player : players)
			messages.put(player, new TextComponent());
		
		// format the components
		messages = plugin.getFormattedMessage(QUIT_MESSAGE, event.getPlayer(), messages);
		
		// message the components to the players
		for(Entry<Player, BaseComponent> entry : messages.entrySet())
		if(entry.getKey().isOnline())
			entry.getKey().spigot().sendMessage(entry.getValue());
	}
}
