package net.karanteeni.restarter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.timers.KaranteeniTimer;

public class Restarter extends KaranteeniPlugin implements KaranteeniTimer, TranslationContainer {
	private boolean restarting = false;
	private int delay = 15;
	private long restartTime = Long.MAX_VALUE;
	
	public Restarter() {
		super(true);
	}

	
	@Override
	public void onEnable() {
		// check the restart time every quarter of a minute
		registerTranslations();
		RestartCMD restart = new RestartCMD();
		restart.setPermission("restarter.restart");
		restart.register();
		Restarter.getTimerHandler().registerTimer(this, 300);
	}
	
	
	@Override
	public void onDisable() {
		
	}


	@Override
	public void runTimer() {
		// restart the server if the restart time is over the restart time
		if(System.currentTimeMillis() >= restartTime)
			restart();
	}


	@Override
	public void timerStopped() {
		
	}


	@Override
	public void timerWait() {
		
	}
	
	
	/**
	 * Restarts the server
	 */
	public boolean restart() {
		if(restarting) return false;
		restarting = true;
		// unregister timer
		Restarter.getTimerHandler().unregisterTimer(this);
		
		// display the restart message
		for(Player player : Bukkit.getOnlinePlayers()) {
			BossBar bar = Bukkit.createBossBar(
					Restarter.getTranslator().getTranslation(this, player, "bossbar-restarting"), BarColor.YELLOW, BarStyle.SOLID);
			Restarter.getMessager().sendBossbar(player, new SoundType(Sound.MUSIC_CREDITS, 1000000f, 1.2f), delay, 5, true, bar);
		}
		
		// wait until restart
		BukkitRunnable displayer = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					// get the title texts
					String titleText = Restarter.getTranslator().getTranslation(
							Restarter.getPlugin(Restarter.class), 
							player, 
							"title-before-kick");
					String subtitleText = Restarter.getTranslator().getTranslation(
							Restarter.getPlugin(Restarter.class), 
							player, 
							"subtitle-before-kick");
					
					// send title
					Restarter.getMessager().sendTitle(0.5f, 0.5f, 20f, player, titleText, subtitleText, Sounds.NOTIFICATION.get());
				}
			}
		};
		
		// display the title and subtitle 8 seconds before kick
		displayer.runTaskLater(this, (delay - 8) * 20);
		
		BukkitRunnable restarter = new BukkitRunnable() {
			@Override
			public void run() {
				// loop each player and kick with server shutdown message
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.kickPlayer(Restarter.getTranslator().getTranslation(
							Restarter.getPlugin(Restarter.class), player, "kick.server-is-restarting"));
				}
				
				// stop the server
				Bukkit.getServer().shutdown();
			}
		};
		
		// run the restart timer
		restarter.runTaskLater(this, delay * 20);
		
		// do the restart
		return true;
	}


	@Override
	public void registerTranslations() {
		if(!this.getConfig().isSet("restart-delay")) {
			this.getConfig().set("restart-delay", 15);
			this.saveConfig();
		}
		this.delay = this.getConfig().getInt("restart-delay");
		
		if(!this.getConfig().isSet("restart-times")) {
			this.getConfig().set("restart-times", Arrays.asList("00-00", "02-00", "06-00", "10-00", "14-00", "20-00"));
			this.saveConfig();
		}
		List<String> times = this.getConfig().getStringList("restart-times");
		
		// get the next restart time in milliseconds
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		
		// loop each restart time and add them to the times
		for(String time : times) {
			// convert the string to hours and minutes
			String[] parts = time.split("-");
			if(parts.length != 2) {
				Bukkit.getLogger().log(Level.CONFIG, "Invalid time format in Restarter config: " + time);
				continue;
			}

			// hours and minutes of the given time
			int hours;
			int minutes;
			
			try {
				hours = Integer.parseInt(parts[0]);
				minutes = Integer.parseInt(parts[1]);
			} catch(NumberFormatException e) {
				Bukkit.getLogger().log(Level.CONFIG, "Invalid time format in Restarter config: " + time);
				continue;
			}
			
			// get current time
			Calendar restartTime = Calendar.getInstance();
			restartTime.setTimeInMillis(System.currentTimeMillis());
			
			// set the hours and minutes
			restartTime.set(Calendar.HOUR_OF_DAY, hours);
			restartTime.set(Calendar.MINUTE, minutes);
			restartTime.set(Calendar.SECOND, 0);
			restartTime.set(Calendar.MILLISECOND, 0);
						
			// check if the given time is in the past and if so, add 24 hours to move it to the next day
			if(cal.compareTo(restartTime) == 1) {
				restartTime.add(Calendar.DATE, 1);
			}

			// check if this is closer to the next restart time than the existing set time
			if(restartTime.getTimeInMillis() < this.restartTime) {
				this.restartTime = restartTime.getTimeInMillis();
			}
		}
		
		
		Restarter.getTranslator().registerTranslation(this, "bossbar-restarting", "The server is restarting");
		Restarter.getTranslator().registerTranslation(this, "title-before-kick", "§0> §6Karanteeni §0<");
		Restarter.getTranslator().registerTranslation(this, "subtitle-before-kick", "§ePlease come back soon!");
		Restarter.getTranslator().registerTranslation(this, "kick.server-is-restarting", "The server is restarting");
	}
}
