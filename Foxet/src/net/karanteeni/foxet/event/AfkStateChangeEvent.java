package net.karanteeni.foxet.event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.data.math.MineMath;
import net.karanteeni.core.timers.KaranteeniTimer;
import net.karanteeni.foxet.Foxet;
import net.karanteeni.foxet.data.AfkState;

/**
 * This event is called when 
 * @author Matti
 *
 */
public class AfkStateChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private static final AfkStateChangeListener listener = new AfkStateChangeListener();
	private boolean cancelled = false;
	
	/* Custom class variables */
	private Player player;
	private AfkState newafkstate;
	private AfkState oldafkstate;
	private List<Block> structureBlocks = new ArrayList<Block>();
	private List<Entity> entities = new ArrayList<Entity>();
	
	/**
	 * Create the event with player and afk states
	 * @param player
	 * @param oldState
	 * @param newState
	 */
	public AfkStateChangeEvent(Player player, AfkState oldState, AfkState newState)
	{
		this.player = player;
		this.newafkstate = newState;
		this.oldafkstate = oldState;
	}
	
	/**
	 * Returns the player whos afk state has changed
	 * @return
	 */
	public Player getPlayer()
	{ return this.player; }
	
	/**
	 * Has player tried to prevent AFK with cheaty methods?
	 * @return
	 */
	public boolean isCheat()
	{ return (newafkstate == AfkState.CHEAT_MOVE || newafkstate == AfkState.CHEAT_STRUCTURE); }
	
	/**
	 * Get the old afk state of player
	 * @return old afk state of player
	 */
	public AfkState getOldAfkState()
	{ return this.oldafkstate; }
	
	/**
	 * Get the new afk state of player
	 * @return new afk state of player
	 */
	public AfkState getNewAfkState()
	{ return this.newafkstate; }
	
	/**
	 * Get the blocks player has used to prevent afk state change.
	 * For example, water loop blocks
	 * @return
	 */
	public List<Block> getAfkCheatStructure()
	{ return this.structureBlocks; }
	
	/**
	 * Get the entities player has used to prevent afk state change.
	 * For example, horses, boats or minecarts
	 * @return
	 */
	public List<Entity> getAfkCheatEntities()
	{ return this.entities; }
	
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
	 * Registers this event to be used
	 * @param plugin plugin to which this will be registered
	 */
	public static void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        //Register timer if it is not registered
        if(!listener.isTimerRegistered())
        	listener.registerTimer();
    }
	
	/**
	 * Listenes to the necessary events to make this event possible
	 * @author Nuubles
	 *
	 */
	private static class AfkStateChangeListener implements Listener, KaranteeniTimer
	{
		/** Locations over time of players */
		private HashMap<Player,Deque<Location>> players = new HashMap<Player,Deque<Location>>();
		private HashMap<Player,Boolean> afkPlayers = new HashMap<Player,Boolean>();
		
		/** Has this timer been registered already */
		private boolean registered = false;
		private double afkDeviationLimit;
		private int moveAmount;
		/** How often is the location checked */
		private int checkFrequence;
		
		/**
		 * Register config values necessary for this event
		 */
		public AfkStateChangeListener()
		{
			Foxet f = Foxet.getPlugin(Foxet.class);
			createConfig(f);
			loadConfig(f);
			//Load the config values
			
		}
		
		private void createConfig(Foxet f)
		{
			//Save the afklimit if not set
			if(!f.getConfig().isSet("afk.movementlimit"))
			{
				f.getConfig().set("afk.movementlimit", 0.6);
				f.saveConfig();
			}
			
			//Save the afklimit if not set
			if(!f.getConfig().isSet("afk.moves-in-memory"))
			{
				f.getConfig().set("afk.moves-in-memory", 180);
				f.saveConfig();
			}
			
			//Save the afklimit if not set
			if(!f.getConfig().isSet("afk.check-frequency"))
			{
				f.getConfig().set("afk.check-frequency", 20);
				f.saveConfig();
			}
		}
		
		private void loadConfig(Foxet f)
		{
			//Set the deviation limit for AFK in movement
			afkDeviationLimit = f.getConfig().getDouble("afk.movementlimit");
			//Set the amount of moves stored in memory
			moveAmount = f.getConfig().getInt("afk.moves-in-memory");
			checkFrequence = f.getConfig().getInt("afk.check-frequency");
		}
		
		/**
		 * Is this timer already registered
		 * @return
		 */
		public boolean isTimerRegistered() {
			return registered;
		}
		
		/**
		 * Set this event as registered
		 * @param registered
		 */
		public void registerTimer() {
			//Register timer to run once every second
			KaranteeniPlugin.getTimerHandler().registerTimer(this, checkFrequence);
		}
		
		/**
		 * Calculates the deviation change in players movement and if over limit then broadcast afk
		 * @param player
		 * @param locs
		 */
		private void checkDeviationChange(Player player, Collection<Location> locs)
		{
			double deviation = MineMath.calculateStandardDeviationXZ(locs);
			
			if(!afkPlayers.get(player) && deviation < afkDeviationLimit) { //Player went to afk
				afkPlayers.put(player, true);
				Bukkit.getPluginManager().callEvent(
						new AfkStateChangeEvent(player, AfkState.NOT_AFK, AfkState.AFK));
			} else if(afkPlayers.get(player) && deviation > afkDeviationLimit) { //Player came away from afk
				afkPlayers.put(player, false);
				Bukkit.getPluginManager().callEvent(
						new AfkStateChangeEvent(player, AfkState.AFK, AfkState.NOT_AFK));
			}
		}
		
		/**
		 * Save player's locations over time
		 */
		@Override
		public void runTimer() {
			//Loop all players
			for(Player player : Bukkit.getOnlinePlayers()) {
				//Player is not in playerlist, insert to list
				if(!players.containsKey(player))
					players.put(player, new ArrayDeque<Location>(
							Arrays.asList(new Location(Bukkit.getWorlds().get(0), 99999999,99999999,99999999))));
				if(!afkPlayers.containsKey(player))
					afkPlayers.put(player, false);
			}
			
			//Loop all players in hashset
			for(Player player : players.keySet()) {
				//If player is not online remove from checklist
				if(!player.isOnline()) {
					players.remove(player);
					afkPlayers.remove(player);
					continue;
				}
				
				//Add location to list
				Deque<Location> locs = players.get(player);
				
				locs.addFirst(player.getLocation());
				if(locs.size() > moveAmount) //If the timer has gone over 3 minutes, start removing excess values
					locs.removeLast();
				
				checkDeviationChange(player, locs);
			}
		}

		@Override
		public void timerStopped() { }
		@Override
		public void timerWait() { }
	}
}
