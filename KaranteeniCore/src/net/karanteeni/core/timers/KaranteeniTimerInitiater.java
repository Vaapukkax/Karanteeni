package net.karanteeni.core.timers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.karanteeni.core.KaranteeniCore;

public class KaranteeniTimerInitiater {
	private final Map<KaranteeniTimer, Integer> listeners = new HashMap<KaranteeniTimer, Integer>();
	private final BukkitRunnable timer;
	private int tickCount = 0;
	private Plugin plugin = KaranteeniCore.getPlugin(KaranteeniCore.class);
	
	/**
	 * Creates and starts the timer
	 */
	public KaranteeniTimerInitiater()
	{
		timer = new BukkitRunnable() {
			@Override
			public void run() {
				runTimer();
			}
		};
		
		timer.runTaskTimerAsynchronously(plugin, 1, 1);
	}
	
	/**
	 * Register a timer to be runned
	 * @param timer timer which is called
	 * @param tickLimit tickes between each call
	 */
	public void registerTimer(KaranteeniTimer timer, int tickLimit) throws IllegalArgumentException
	{
		if(tickLimit > 0)
		{
			listeners.put(timer, tickLimit);
		}
		else
			throw new IllegalArgumentException("Illegal time! Has to be 1-N. " + tickLimit + " given.");
	}
	
	/**
	 * Otetaan ajastin pois rekisteräinnistä
	 * @param timer
	 */
	public void unregisterTimer(KaranteeniTimer timer)
	{
		timer.timerStopped();
		listeners.remove(timer);
	}
	
	/**
	 * Runs all timers once
	 */
	public void runTimer()
	{
		for (Entry<KaranteeniTimer, Integer> entry : listeners.entrySet())
		{
			//Has there been X ticks since last call
			if(tickCount % entry.getValue() == 0)			
			{
				//The main function of the timer
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable() 
					{
						@Override
						public void run()
						{
							try
							{
								entry.getKey().runTimer();
							}
							catch(Exception e)
							{
								plugin.getLogger().log(Level.WARNING, "An Error happened in timer runnable", e);
							}
						}
					});
			}
			else
			{
				//Runs the timerWait() function
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable() 
					{
						@Override
						public void run()
						{
							try
							{
								entry.getKey().timerWait();
							}
							catch(Exception e)
							{
								plugin.getLogger().log(Level.WARNING, "An Error happened in timer waiter runnable", e);
							}
						}
					});
			}
		}
		
		if(tickCount == Integer.MAX_VALUE)
			tickCount = 0;
		++tickCount;
	}
}
