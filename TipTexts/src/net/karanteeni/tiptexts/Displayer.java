package net.karanteeni.tiptexts;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Displayer {
	private int secondLimit;
	private TipTexts plugin;
	private BukkitRunnable runnable;
	
	public Displayer(int limit, TipTexts plugin) {
		this.secondLimit = limit;
		this.plugin = plugin;
	}
	
	
	/**
	 * Starts the display of tips for players
	 */
	public void run() {
		// stop the possible previous timer
		if(runnable != null) 
			runnable.cancel();
		
		// create a new runnable
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				// display the random message to each player
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.sendMessage(
						TipTexts.getTranslator().getTranslation(plugin, player, TipTexts.FORMAT)
						.replace(TipTexts.MESSAGE, TipTexts.getTranslator().getRandomTranslation(plugin, player, TipTexts.TIPS)));
				}
			}
		};
		
		// run the timer
		runnable.runTaskTimerAsynchronously(plugin, secondLimit * 20, secondLimit * 20);
	}
	
	
	/**
	 * Stop the running timer
	 */
	public void stop() {
		if(runnable != null) 
			runnable.cancel();
	}
}
