package net.karanteeni.foxet.movement;

import java.util.Deque;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.karanteeni.core.timers.KaranteeniTimer;
import net.karanteeni.foxet.Foxet;

/**
 * Manages the movement data collected from players
 * @author Nuubles
 *
 */
public class MoveManager 
{
	/** How ofter player movement is polled in ticks */
	private final int pollfreq;
	
	/**
	 * Initializes the movemanager and loads config
	 */
	public MoveManager()
	{
		//Check if movement poll frequency is set
		Foxet plugin = Foxet.getPlugin(Foxet.class);
		if(!plugin.getConfig().isSet("PollFrequency"))
		{
			plugin.getConfig().set("PollFrequency", "10");
			plugin.saveConfig();
		}
		
		pollfreq = plugin.getConfig().getInt("PollFrequency");
	}
}

/**
 * This class is used to poll the players and their movements onto a hashmap
 * @author Nuubles
 */
final class Poller implements KaranteeniTimer 
{
	/** Locations of player over time */
	private HashMap<UUID,Deque<Location>> locations = new HashMap<UUID,Deque<Location>>();
	
	@Override
	public void runTimer() {
		//Loop all players
		for(Player player : Bukkit.getOnlinePlayers())
		{
			//Check that player is not an npc
			if(player.getMetadata("NPC") != null && !player.getMetadata("NPC").isEmpty())
				continue;
			
			
		}
	}

	@Override
	public void timerStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timerWait() {
		// TODO Auto-generated method stub
		
	}
}
