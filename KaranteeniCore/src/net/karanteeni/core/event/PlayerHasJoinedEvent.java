package net.karanteeni.core.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.karanteeni.core.KaranteeniCore;

/**
 * This event is fired when no action is done when clicking a block or
 * clicking air. May fire multiple times, refer to PlayerInteractEvent.
 * 
 * You MUST register this in the main class with
 * <i>NoActionEvent.register(Plugin);</i>
 * 
 * @author Nuubles
 *
 */
public class PlayerHasJoinedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private static final PlayerHasJoinedEventListener listener = new PlayerHasJoinedEventListener();
	
	/* Custom class variables */
	private Player player;
	private String joinMessage;

	/**
	 * Initialize the delayed player join event
	 * @param player player who joined the server
	 * @param joinMessage join message of player
	 */
	public PlayerHasJoinedEvent(Player player, String joinMessage)
	{
		this.player = player;
		this.joinMessage = joinMessage;
	}
	
	/**
	 * Returns the joined player 
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	
	
	/**
	 * Returns the join message
	 * @return the join message of the event
	 */
	public String getJoinMessage() {
		return this.joinMessage;
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
	 * Listens to the necessary events to make this event possible
	 * @author Nuubles
	 *
	 */
	private static class PlayerHasJoinedEventListener implements Listener {		
		@EventHandler (priority = EventPriority.MONITOR)
		private void onJoin(PlayerJoinEvent event) {
			// delay the event by 3 ticks
			Bukkit.getScheduler().runTaskLater(KaranteeniCore.getPlugin(KaranteeniCore.class), new Runnable() {
				@Override
				public void run() {
					if(event.getPlayer().isOnline()) {
						Bukkit.getPluginManager().callEvent(
							new PlayerHasJoinedEvent(
									event.getPlayer(),
									event.getJoinMessage()
								)
							);
					}
				}
			}, 3);
		}
	}
}
