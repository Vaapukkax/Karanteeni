package net.karanteeni.core.information.bossbar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.timers.KaranteeniTimer;

public class TimedBossBar implements KaranteeniTimer{

	final List<Player> player;
	final BossBar bar;
	//final float aliveSeconds;
	final long removeTimeStamp; 
	final long startTimeStamp;
	final boolean animated;
	Iterator<String> text;
	final List<String> iteratorReset;
	
	/**
	 * Creates a bossbar which is removed after a certain amount of time
	 * @param player
	 * @param bar
	 * @param displayTime
	 */
	public TimedBossBar(Player player, BossBar bar, final float displayTime, final boolean animated)
	{
		this.player = Arrays.asList(player);
		this.bar = bar;
		this.startTimeStamp = System.currentTimeMillis();
		this.removeTimeStamp = System.currentTimeMillis() + (long)displayTime*1000;
		this.animated = animated;
		this.text = null;
		this.iteratorReset = null;
		bar.addPlayer(player);
	}
	
	/**
	 * Creates a bossbar which will be removed after a certain amount of time
	 * @param players
	 * @param bar
	 * @param displayTime
	 */
	public TimedBossBar(List<Player> players, BossBar bar, final float displayTime, final boolean animated)
	{
		this.player = players;
		this.bar = bar;
		this.startTimeStamp = System.currentTimeMillis();
		this.removeTimeStamp = System.currentTimeMillis() + (long)displayTime*1000;
		this.animated = animated;
		this.text = null;
		this.iteratorReset = null;
		for(Player player : players)
			bar.addPlayer(player);
	}
	
	/**
	 * Creates a bossbar which is removed after a certain amount of time
	 * @param player
	 * @param bar
	 * @param displayTime
	 */
	public TimedBossBar(Player player, BossBar bar, final float displayTime, List<String> animatedText, final boolean animated)
	{
		this.player = Arrays.asList(player);
		this.bar = bar;
		this.startTimeStamp = System.currentTimeMillis();
		this.removeTimeStamp = System.currentTimeMillis() + (long)displayTime*1000;
		this.animated = animated;
		this.text = animatedText.iterator();
		this.iteratorReset = animatedText;
		bar.addPlayer(player);
	}
	
	/**
	 * Creates a bossbar which will be removed after a certain amount of time
	 * @param players
	 * @param bar
	 * @param displayTime
	 */
	public TimedBossBar(List<Player> players, BossBar bar, final float displayTime, List<String> animatedText, final boolean animated)
	{
		this.player = players;
		this.bar = bar;
		this.startTimeStamp = System.currentTimeMillis();
		this.removeTimeStamp = System.currentTimeMillis() + (long)displayTime*1000;
		this.animated = animated;
		this.text = animatedText.iterator();
		this.iteratorReset = animatedText;
		for(Player player : players)
			bar.addPlayer(player);
	}
	
	@Override
	public void runTimer() 
	{
		//Remove the bossbar if over the displaytime
		if(removeTimeStamp < System.currentTimeMillis())
		{
			removeBar();
			return;
		}
		
		//Update bar level if animated
		if(this.animated)
		{
			bar.setProgress(1f - ((float)(System.currentTimeMillis()-startTimeStamp) / (removeTimeStamp-startTimeStamp)) );
		}
		
		if(this.text != null)
		{
			if(this.text.hasNext())
			{
				bar.setTitle(this.text.next());
			}
			else
			{
				this.text = this.iteratorReset.iterator();
				bar.setTitle(this.text.next());
			}
		}
	}

	/**
	 * Remove bossbar when the timer stops
	 */
	@Override
	public void timerStopped() {
		bar.removeAll();
	}

	@Override
	public void timerWait() 
	{
		//Remove bossbar if over displaytime
		if(removeTimeStamp < System.currentTimeMillis())
		{
			removeBar();
			return;
		}
		
		//update bar level if animated
		if(this.animated)
		{
			bar.setProgress(1f - ((float)(System.currentTimeMillis()-startTimeStamp) / (removeTimeStamp-startTimeStamp)) );
		}
	}
	
	/**
	 * Removes all instances of this bossbar
	 */
	public void removeBar()
	{
		//Remove all bars
		bar.removeAll();
		
		//Remove this timer because it is no longer needed
		KaranteeniCore.getTimerHandler().unregisterTimer(this);
	}
	
	/**
	 * Removes this bossbar from player
	 * @param player player to be removed from
	 */
	public void removePlayer(final Player player)
	{
		bar.removePlayer(player);
		this.player.remove(player);
	}
}
