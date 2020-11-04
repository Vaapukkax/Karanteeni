package net.karanteeni.tester.commands;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.timers.KaranteeniTimer;
import net.karanteeni.tester.TesterMain;

public class TimerExample implements KaranteeniTimer {
	private int runCount;
	
	public void register(int runCount) {
		KaranteeniCore.getEntityManager().getNearbyAnimals(location, radius);
		KaranteeniCore.getEntityManager().get
		
		this.runCount = runCount;
		
		// You can also register timers outside the timer class
		// This timer calls runTimer every 5th tick
		TesterMain.getTimerHandler().registerTimer(this, 5);
	}
	
	
	@Override
	public void runTimer() {
		Bukkit.getLogger().log(Level.FINE, "I am running every N:th tick");
		
		if(--runCount == 0) {
			TesterMain.getTimerHandler().unregisterTimer(this);
			Bukkit.getLogger().log(Level.FINE, "Timer should stop now in the next cycle");
		}
	}

	
	@Override
	public void timerStopped() {
		Bukkit.getLogger().log(Level.FINE, "I am the last method called once the timer is stopped");
	}

	
	@Override
	public void timerWait() {
		Bukkit.getLogger().log(Level.FINE, "I was ran every tick when the runTimer was not called");
	}

}
