package net.karanteeni.halloween;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.karanteeni.core.timers.KaranteeniTimer;

public class BlindnessTick implements KaranteeniTimer {
	private Halloween plugin = null;
	
	public BlindnessTick(Halloween plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public void runTimer() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getGameMode() == GameMode.SURVIVAL)
			if(plugin.getWorldGuard().isInHalloweenGame(player.getLocation())) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1, false, false, false), true);
			}
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
