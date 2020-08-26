package net.karanteeni.wgef;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.wgef.events.LazyRegionChecker;
import net.karanteeni.wgef.events.callable.RegionMusic;
import net.karanteeni.wgef.events.callable.SurvivalFly;
import net.karanteeni.wgef.worldguard.WorldGuardManager;

public class WGEF extends KaranteeniPlugin {
	WorldGuardManager wgm = null;
	
	public WGEF() {
		super(true);
	}

	
	@Override
	public void onLoad() {
		wgm = new WorldGuardManager();
		wgm.addStringFlag(RegionMusic.ENTER_PLAY_DIR_SONGS);
		wgm.addFlagToRegister(SurvivalFly.ALLOW_SURVIVAL_FLY, true);
		wgm.registerFlags();
	}
	
	
	@Override
	public void onEnable() {
		if(wgm != null && this.getServer().getPluginManager().getPlugin("WorldGuard") != null && 
				this.getServer().getPluginManager().getPlugin("WorldGuard").isEnabled()) {
			wgm.register();	
		} else {
			Bukkit.getLogger().log(Level.WARNING, "WorldGuard not found. Disabling plugin");
			wgm = null;
		}
		
		// initialize the timer
		WGEF.getTimerHandler().registerTimer((new LazyRegionChecker(wgm)), 20);
		registerEvents();
	}
	
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void registerEvents() {
		// music player
		this.getServer().getPluginManager().registerEvents(new RegionMusic(this), this);
		this.getServer().getPluginManager().registerEvents(new SurvivalFly(this), this);
	}
	
	
	/**
	 * Returns the worldguard manager
	 * @return
	 */
	public WorldGuardManager getWorldGuard() {
		return wgm;
	}
}
