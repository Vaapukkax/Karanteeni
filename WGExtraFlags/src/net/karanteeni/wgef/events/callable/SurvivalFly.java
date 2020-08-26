package net.karanteeni.wgef.events.callable;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.karanteeni.wgef.WGEF;
import net.karanteeni.wgef.events.RegionEnterEvent;

public class SurvivalFly implements Listener {
	public static String ALLOW_SURVIVAL_FLY = "allow-survival-fly";
	private WGEF plugin;
	
	public SurvivalFly(WGEF plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void playerFly(PlayerToggleFlightEvent event) {
		if(!event.isFlying())
			return;
		
		if(event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		
		if(allowPlayerFlyInLocation(event.getPlayer(), plugin.getWorldGuard().getRegions(event.getPlayer().getLocation())))
			return;
		
		event.setCancelled(true);
		event.getPlayer().setAllowFlight(false);
	}
	
	
	@EventHandler
	public void regionEnter(RegionEnterEvent event) {
		if(event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		
		if(!event.getPlayer().isFlying() && !event.getPlayer().getAllowFlight())
			return;
		
		if(allowPlayerFlyInLocation(event.getPlayer(), plugin.getWorldGuard().getRegions(event.getPlayer().getLocation())))
			return;
		
		event.getPlayer().setAllowFlight(false);
		event.getPlayer().setFlying(false);
	}
	
	
	/**
	 * Check if player can fly in the given regions
	 * @param player
	 * @param regions
	 * @return
	 */
	private Boolean allowPlayerFlyInLocation(Player player, ApplicableRegionSet regions) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		StateFlag.State state = regions.queryState(localPlayer, (StateFlag)plugin.getWorldGuard().getFlag(ALLOW_SURVIVAL_FLY));
		if(state == StateFlag.State.ALLOW)
			return true;
		else if(state == StateFlag.State.DENY)
			return false;
		else
			return true;
	}
}
