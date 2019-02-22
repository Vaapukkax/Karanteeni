package net.karanteeni.foxet.movement;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import net.karanteeni.core.timers.KaranteeniTimer;

public class MoveManager implements KaranteeniTimer 
{
	/** Locations of player over time */
	private HashMap<UUID,List<Location>> locations = new HashMap<UUID,List<Location>>();
	
	@Override
	public void runTimer() {
		// TODO Auto-generated method stub
		
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
