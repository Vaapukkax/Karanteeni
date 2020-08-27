package net.karanteeni.halloween;

import java.util.Arrays;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.halloween.worldguard.WorldGuardManager;

public class Halloween extends KaranteeniPlugin {
	WorldGuardManager wgm = null;
	
	public Halloween() {
		super(true);
	}

	
	@Override
	public void onLoad() {
		wgm = new WorldGuardManager();
		wgm.registerFlags();
	}
	
	
	@Override
	public void onEnable() {
		if(wgm != null && this.getServer().getPluginManager().getPlugin("WorldGuard") != null && 
				this.getServer().getPluginManager().getPlugin("WorldGuard").isEnabled()) {
			wgm.register();	
		} else {
			wgm = null;
		}
		
		// register events
		getServer().getPluginManager().registerEvents(new LanterBreakEvent(this), this);
		
		// register commands
		CountLanternCMD lcmd = new CountLanternCMD(this, "countlanterns", "a", "a", "a", Arrays.asList());
		lcmd.setPermission("karanteenials.player.gamemode.spectator.self");
		lcmd.register();
		
		// register blindness timer for regions
		KaranteeniCore.getTimerHandler().registerTimer(new BlindnessTick(this), 30);
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	/**
	 * Returns the worldguard manager
	 * @return
	 */
	public WorldGuardManager getWorldGuard() {
		return wgm;
	}
}
