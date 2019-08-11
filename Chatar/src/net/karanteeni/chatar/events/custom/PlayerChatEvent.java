package net.karanteeni.chatar.events.custom;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.karanteeni.chatar.Chatar;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerChatEvent extends Event implements Cancellable {
	/* Event handlers */
	private static final HandlerList handlers = new HandlerList();
	private static final PlayerChatEventListener listener = new PlayerChatEventListener();
	private boolean cancelled = false;
	
	/* Event data */
	//private static final 	Pattern TAG_PATTERN = Pattern.compile("\\%[^%]+\\%");
	private final 			Player 	player;
	private 				String 	message;
	private 				String 	format;
	
	private 		HashMap<Player, BaseComponent>			recipients;
	//private 		HashMap<Player, BaseComponent>	formattedMessages = new HashMap<Player, BaseComponent>();
	
	PlayerChatEvent(Player player, String message, String format, Set<Player> players) {
		this.format = format;
		this.player = player;
		this.message = message;
		this.recipients = new HashMap<Player, BaseComponent>();
		
		// map players and message to the map for each player
		for(Player r : players)
			recipients.put(r, new TextComponent(TextComponent.fromLegacyText(message)));
	}
	
	
	/**
	 * Returns the format of the message
	 * @return format in the form of '%rank% %player% %message%'
	 */
	public String getFormat() {
		return this.format;
	}
	
	
	/**
	 * Returns the message of this event
	 * @return
	 */
	public String getMessage() {
		return this.message;
	}
	
	
	/**
	 * Sets the message of this event
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	/**
	 * Sets the custom message of given player
	 * @param player player to whom the message will be set
	 * @param message message to set for the player
	 */
	public void setMessage(Player player, BaseComponent message) {
		this.recipients.put(player, message);
	}
	
	
	/**
	 * Returns the custom message for this player
	 * @param player
	 * @return
	 */
	public BaseComponent getMessage(Player player) {
		return recipients.get(player);
	}
	
	
	/**
	 * Sets the format of this event
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	
	/**
	 * Returns the recipients of this message
	 * @return recipients of this message
	 */
	public Set<Player> getRecipients() {
		return this.recipients.keySet();
	}

	
	/**
	 * Registers this event to be used
	 * @param plugin plugin to which this will be registered
	 */
	public static void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	
	/**
	 * This class will fire the PlayerChatEvent with the necessary information
	 * @author Nuubles
	 */
	private static class PlayerChatEventListener implements Listener {
		private final String chatFormat;
		Chatar plugin;
		
		/**
		 * Generate a default format for chat messages
		 */
		public PlayerChatEventListener() {
			plugin = Chatar.getPlugin(Chatar.class);
			if(!plugin.getSettings().isSet("FORMAT.chat")) {
				plugin.getSettings().set("FORMAT.chat", "§6[§7%player%§6]§f: %msg%");
				plugin.saveSettings();
			}
			
			// load the message format to memory
			chatFormat = plugin.getSettings().getString("FORMAT.chat");
		}
		
		
		@EventHandler(priority = EventPriority.MONITOR)
		private void onChat(AsyncPlayerChatEvent e) {
			// cancel the normal chat event
			e.setCancelled(true);
			
			// create and call the custom event
			PlayerChatEvent event = new PlayerChatEvent(
										e.getPlayer(), 
										e.getMessage(), 
										chatFormat, 
										e.getRecipients());
			// run task synchronously. TODO CHANGE TO ASYNC
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(event);
				}
			});
		}
		
		
		@EventHandler(priority = EventPriority.MONITOR)
		private void onPlayerChat(PlayerChatEvent event) {
			// return if event is cancelled
			if(event.cancelled)
				return;
			
			// map of all components for each player
			/*HashMap<Player, BaseComponent> components = new HashMap<Player, BaseComponent>();
			
			// add all players to the components map along with a base for string addition
			for(Player player : event.recipients.keySet())
				components.put(player, new TextComponent());
			
			// generate and loop all keywords from format
			Matcher m = TAG_PATTERN.matcher(event.format);
			boolean matchFound = false;
			int lastMatchPos = 0;
			while(m.find()) {
				matchFound = true;
				String group = m.group();
				// cut the % % away
				group = group.substring(1, group.length()-1);
				
				// get the string in between this and previous match
				String notMatchPart = event.format.substring(lastMatchPos, m.start());
			
				// if the group is %msg% just replace it with the message and don't care more
				HashMap<Player, BaseComponent> formattedComponents = null;
				// get and format the components with the keyword in the config
				ChatComponent component = plugin.getComponent(group);
				if(component != null)
						formattedComponents = component.asBaseComponent(event.player, event.recipients.keySet());
			
				// add the results to the messages
				for(Player player : event.recipients.keySet()) {
					BaseComponent pc = components.get(player);
					// add the string before the keyword to the result
					pc.addExtra(new TextComponent(TextComponent.fromLegacyText(notMatchPart)));
					
					// add the formatted chatcomponent to the message to be sent
					if(formattedComponents != null)
						pc.addExtra(formattedComponents.get(player));
					else {
						// if no formatted component was found, check if this is the msg tag
						if(!group.equals("msg")) { // if not, add the tag name as it was mentioned
							pc.addExtra(new TextComponent(TextComponent.fromLegacyText("%"+group+"%")));
						} else {
							// was the message tag. set either the custom message OR default message
							if(event.recipients.containsKey(player))
								pc.addExtra(new TextComponent(TextComponent.fromLegacyText(event.recipients.get(player))));
							else
								pc.addExtra(new TextComponent(TextComponent.fromLegacyText(event.message)));
						}
					}
				}
			
				// update the position of the latest match position
				lastMatchPos = m.end();
			}
			
			// if no matches, just put the format there
			if(!matchFound) {
				// TODO
			}*/
			HashMap<Player, BaseComponent> components = plugin.getFormattedMessage(event.format, event.player, event.recipients);
			
			// loop each recipient and send the message
			for(Entry<Player, BaseComponent> pb : components.entrySet())
				pb.getKey().spigot().sendMessage(pb.getValue());
			
			// print the original message to the console
			Bukkit.getLogger().log(Level.INFO, event.player.getName() + " > " + event.message);
		}
	}
}
