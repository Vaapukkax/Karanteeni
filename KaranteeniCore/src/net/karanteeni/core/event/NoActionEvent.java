package net.karanteeni.core.event;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
public class NoActionEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private static final NoActionEventListener listener = new NoActionEventListener();
	private boolean cancelled = false;
	
	/* Custom class variables */
	private Player player;
	private EquipmentSlot hand;
	private Block block;
	private Action action;
	private ItemStack item;

	/**
	 * Initializes this class with all variables, true only when clicking air
	 * @param player
	 * @param block
	 * @param hand
	 * @param action
	 * @param blockFace
	 * @param item
	 */
	public NoActionEvent(Player player, EquipmentSlot hand, Action action, ItemStack item, Block block)
	{
		this.player = player;
		this.hand = hand;
		this.action = action;
		this.item = item;
		this.block = block;
	}
	
	/**
	 * Gets the player who did this action. 
	 * @return
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Gets the clicked block in this event
	 * @return
	 */
	public Block getBlock()
	{
		return block;
	}
	
	/**
	 * Gets the hand used in this event
	 * @return
	 */
	public EquipmentSlot getHand()
	{
		return hand;
	}
	
	/**
	 * Gets the action done
	 * @return
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * Gets the itemstack used by player
	 * @return
	 */
	public ItemStack getItem()
	{
		return item;
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
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
	
	/**
	 * Listenes to the necessary events to make this event possible
	 * @author Nuubles
	 *
	 */
	private static class NoActionEventListener implements Listener
	{
		private boolean blockBroken = false;
		private Player player;
		
		@EventHandler (priority = EventPriority.MONITOR)
		private void blockBreak(BlockBreakEvent event)
		{
			blockBroken = true;
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		private void playerInteractEvent(PlayerInteractEvent event)
		{
			if(event.getAction().equals(Action.PHYSICAL) || blockBroken)
			{
				blockBroken = false;
				return;
			}
			
			if(event.isCancelled())
				Bukkit.getPluginManager().callEvent(
						new NoActionEvent(
								event.getPlayer(), 
								event.getHand(), 
								event.getAction(), 
								event.getItem(),
								event.getClickedBlock())
						);
			
			this.player = event.getPlayer();
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		private void blockCanBuildEvent(BlockCanBuildEvent event)
		{
			//Gets the players in a radius of 100
			/*Collection<Entity> entities = 
					event.getBlock().getWorld().getNearbyEntities(
							event.getBlock().getLocation(), 20, 20, 20, (e -> (e instanceof Player)));
			
			Player player = null;
			Double minDist = null;
			
			//Get the closest players
			if(!entities.isEmpty()) {
				for(Entity p : entities) {
					double dist = p.getLocation().distanceSquared(event.getBlock().getLocation());
					if(minDist == null) {
						player = (Player)p;
						minDist = dist;
					}
					else if(minDist > dist) {
						player = (Player)p;
						minDist = dist;
					}
				}
			}*/
					
			if(!event.isBuildable() && player != null)
			{
				Bukkit.getPluginManager().callEvent(
						new NoActionEvent( 
								player, 
								EquipmentSlot.HAND, 
								Action.RIGHT_CLICK_BLOCK, 
								player.getInventory().getItemInMainHand(),
								event.getBlock()) );
				player = null;
			}
		}
	}
}
