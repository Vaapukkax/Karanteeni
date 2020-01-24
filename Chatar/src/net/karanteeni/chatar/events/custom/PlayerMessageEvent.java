package net.karanteeni.chatar.events.custom;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.players.KPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMessageEvent extends Event implements Cancellable {
	/* Event handlers */
	private static final HandlerList handlers = new HandlerList();
	private static final PlayerMessageEventListener listener = new PlayerMessageEventListener();
	private boolean cancelled = false;
	
	/* Event data */
	private final 	Player 	player;
	private 		String 	senMessage;
	private 		String 	recMessage;
	private final	String 	message; // original message
	private 		String 	senFormat;
	private 		String 	recFormat;
	private static	String 	DEFAULT_SENDER_FORMAT; // default format for sender format
	private static	String 	DEFAULT_RECEIVER_FORMAT; // default format for receiver message
	private 		Player	recipient;
	
	public PlayerMessageEvent(Player player, String message, String recFormat, String senFormat, Player receiver) {
		this.senFormat = senFormat;
		this.recFormat = recFormat;
		this.player = player;
		this.senMessage = message;
		this.recMessage = message;
		this.message = message;
		this.recipient = receiver;
	}
	
	
	public PlayerMessageEvent(Player player, String message, Player receiver) {
		this.senFormat = DEFAULT_SENDER_FORMAT;
		this.recFormat = DEFAULT_RECEIVER_FORMAT;
		this.player = player;
		this.senMessage = message;
		this.recMessage = message;
		this.message = message;
		this.recipient = receiver;
	}
	
	
	/**
	 * Returns the format of the message
	 * @return format in the form of '%rank% %player% %message%'
	 */
	public String getSenderFormat() { 
		return this.senFormat; 
	}

	
	/**
	 * Gets the player sending the message
	 * @return player who is sending the message
	 */
	public Player getSender() {
		return this.player;
	}
	
	
	/**
	 * Returns the format of the message
	 * @return format in the form of '%rank% %player% %message%'
	 */
	public String getReceiverFormat() { 
		return this.recFormat; 
	}
	
	
	/**
	 * Returns the message of this event
	 * @return
	 */
	public String getMessage() { 
		return this.message; 
	}

	
	/**
	 * Returns the message of this event
	 * @return
	 */
	public String getReceiverMessage() { 
		return this.recMessage; 
	}
	
	
	/**
	 * Returns the message of this event
	 * @return
	 */
	public String getSenderMessage() { 
		return this.senMessage; 
	}
	
	
	/**
	 * Sets the message of this event
	 * @param message
	 */
	public void setReceiverMessage(String message) { 
		this.recMessage = message; 
	}

	
	/**
	 * Sets the message of this event
	 * @param message
	 */
	public void setSenderMessage(String message) { 
		this.senMessage = message; 
	}
	
	
	/**
	 * Sets the format of this event
	 * @param format
	 */
	public void setReceiverFormat(String format) { 
		this.recFormat = format; 
	}

	
	/**
	 * Sets the format of this event
	 * @param format
	 */
	public void setSenderFormat(String format) { 
		this.senFormat = format; 
	}
	
	
	/**
	 * Returns the recipients of this message
	 * @return recipients of this message
	 */
	public Player getRecipient() {
		return this.recipient;
	}

	
	/**
	 * Registers this event to be used
	 * @param plugin plugin to which this will be registered
	 */
	public static void register(Chatar plugin) {
		plugin.registerFormat("message.send", "§7YOU§6 > %player%§e : §r%msg%");
		plugin.registerFormat("message.receive", "%player%§6 >§7 YOU§e : §r%msg%");
		DEFAULT_SENDER_FORMAT = plugin.getRawFormat("message.send");
		DEFAULT_RECEIVER_FORMAT = plugin.getRawFormat("message.receive");
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
	private static class PlayerMessageEventListener implements Listener {
		Chatar plugin;
		
		/**
		 * Generate a default format for chat messages
		 */
		public PlayerMessageEventListener() {
			plugin = Chatar.getPlugin(Chatar.class);
		}
		
		
		/**
		 * This event is called when the message is just about to be send
		 * @param event
		 */
		@EventHandler(priority = EventPriority.MONITOR)
		private void onPlayerChat(PlayerMessageEvent event) {
			// return if event is cancelled
			if(event.cancelled)
				return;
			
			// format the recipient message
			HashMap<Player, BaseComponent> recipient = new HashMap<Player, BaseComponent>();
			recipient.put(event.recipient, new TextComponent(TextComponent.fromLegacyText(event.message)));
			recipient = plugin.getFormattedMessage(event.recFormat, event.player, recipient);
			
			// format the sender message
			HashMap<Player, BaseComponent> sender = new HashMap<Player, BaseComponent>();
			sender.put(event.player, new TextComponent(TextComponent.fromLegacyText(event.message)));
			sender = plugin.getFormattedMessage(event.senFormat, event.recipient, sender);
			
			// send message to recipient
			for(Entry<Player, BaseComponent> pb : recipient.entrySet()) {
				pb.getKey().spigot().sendMessage(pb.getValue());
				
				// send the message received sound
				Chatar.getSoundHandler().playSound(pb.getKey(), Sounds.PLING_HIGH.get(), SoundCategory.PLAYERS);
			}
			
			// send message to sender
			for(Entry<Player, BaseComponent> pb : sender.entrySet()) {
				pb.getKey().spigot().sendMessage(pb.getValue());
				
				// send the message sent sound
				Chatar.getSoundHandler().playSound(pb.getKey(), Sounds.NONE.get(), SoundCategory.PLAYERS);
			}
			
			// store the receiver and sender to KPlayers to allow /reply usage
			KPlayer msender = KPlayer.getKPlayer(event.player);
			KPlayer mreceiver = KPlayer.getKPlayer(event.recipient);
			msender.setCacheData(plugin, "message", mreceiver.getPlayer().getUniqueId());
			mreceiver.setCacheData(plugin, "message", msender.getPlayer().getUniqueId());
		}
	}
}
