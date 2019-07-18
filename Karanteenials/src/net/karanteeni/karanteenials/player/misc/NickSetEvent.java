package net.karanteeni.karanteenials.player.misc;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class NickSetEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private static final NickSetListener listener = new NickSetListener();
	private final UUID player;
	private String nick;
	private CommandSender setter;
	private boolean cancelled = false;
	
	
	public NickSetEvent(UUID player, String nickname, CommandSender setter) {
		this.player = player;
		this.nick = nickname;
		this.setter = setter;
	}
	
	
	/**
	 * Returns the being who changed the nickname
	 * @return sender who changed nickname
	 */
	public CommandSender getSetter() {
		return this.setter;
	}
	
	
	/**
	 * Returns the current nick which is being set to the player
	 * @return nick of the player
	 */
	public String getNick() {
		return this.nick;
	}
	
	
	/**
	 * Sets the new nick for player in this event
	 * @param nick nick to set to the player
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	
	/**
	 * Returns the player whose nick is being set
	 * @return uuid of player
	 */
	public UUID getUUID() {
		return this.player;
	}
	
	
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
	
	
	private static class NickSetListener implements Listener, TranslationContainer {		
		Karanteenials plugin;
		public NickSetListener() {
			plugin = Karanteenials.getPlugin(Karanteenials.class);
			registerTranslations();
		}
		
		
		@EventHandler (priority = EventPriority.MONITOR)
		private void onNickSet(NickSetEvent event) {
			if(event.cancelled) return;
			
			boolean setOwn = false;
			Player set = Bukkit.getPlayer(event.player);
			
			// check if the event setter modified their own nick name
			if(event.setter instanceof Player && ((Player)event.setter).getUniqueId().equals(event.player))
				setOwn = true;
			
			// send the message to the player whose name has been changed
			if(set != null && set.isOnline())
				Karanteenials.getMessager().sendMessage(set, Sounds.SETTINGS.get(),
						Prefix.NEUTRAL +
						Karanteenials.getTranslator().getTranslation(plugin, set, "nick.set")
						.replace("%old%", set.getDisplayName())
						.replace("%new%", event.nick));
			
			// send the message to the command sender
			if(!setOwn && event.setter != null) {
				String name = Karanteenials.getPlayerHandler().getOfflineName(event.player);
				String dname = Karanteenials.getPlayerHandler().getOfflineDisplayName(event.player);
				
				Karanteenials.getMessager().sendMessage(set, Sounds.SETTINGS.get(),
						Prefix.NEUTRAL +
						Karanteenials.getTranslator().getTranslation(plugin, set, "nick.set-other")
						.replace("player", name + "")
						.replace("%old%", dname)
						.replace("%new%", event.nick));
			}
			
			// run the code to set the player nick
			if(!KaranteeniCore.getPlayerHandler().setDisplayName(event.player, event.nick)) {
				Karanteenials.getMessager().sendMessage(event.setter, Sounds.ERROR.get(), 
						Prefix.ERROR +
						Karanteenials.getDefaultMsgs().databaseError(event.setter));
			}
		}

		
		@Override
		public void registerTranslations() {
			Karanteenials.getTranslator().registerTranslation(plugin, "nick.set", 
					"Your nickname %old% has been changed to %new%");
			Karanteenials.getTranslator().registerTranslation(plugin, "nick.set-other", 
					"You set the nickname of %player% from %old% to %new%");
		}
	}

	
	/**
	 * Registers this event to be used
	 * @param plugin plugin to which this will be registered
	 */
	public static void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
	

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
