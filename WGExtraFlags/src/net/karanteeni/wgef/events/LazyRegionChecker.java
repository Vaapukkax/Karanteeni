package net.karanteeni.wgef.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.karanteeni.core.timers.KaranteeniTimer;
import net.karanteeni.wgef.worldguard.WorldGuardManager;

public class LazyRegionChecker implements KaranteeniTimer {
	private WorldGuardManager wgm;
	private HashMap<UUID, Set<ProtectedRegion>> regions = new HashMap<UUID, Set<ProtectedRegion>>();
	
	public LazyRegionChecker(WorldGuardManager wgm) {
		this.wgm = wgm;
	}
	
	
	@Override
	public void runTimer() {
		// loop all players
		for(Player player : Bukkit.getOnlinePlayers()) {
			// if player is not yet registered, register the player
			if(!regions.containsKey(player.getUniqueId()))
				regions.put(player.getUniqueId(), new HashSet<ProtectedRegion>());
			
			ApplicableRegionSet set = wgm.getRegions(player.getLocation());
			Set<ProtectedRegion> rgs = set.getRegions();
			
			Set<ProtectedRegion> regionList = regions.get(player.getUniqueId());
			
			// regions player has left
			Set<ProtectedRegion> leftRegions = Sets.difference(regionList, rgs);
			
			// regions player has joined
			Set<ProtectedRegion> joinedRegions = Sets.difference(rgs, regionList);

			if(leftRegions != null && !leftRegions.isEmpty()) {
				try {					
					Bukkit.getPluginManager().callEvent(new RegionExitEvent(player, leftRegions));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(joinedRegions != null && !joinedRegions.isEmpty())
				try {
					Bukkit.getPluginManager().callEvent(new RegionEnterEvent(player, joinedRegions));
				} catch(Exception e) {
					e.printStackTrace();
				}
			
			if(leftRegions != null || joinedRegions != null)
				regions.put(player.getUniqueId(), rgs);
		}
	}

	
	@Override
	public void timerStopped() {
		regions.clear();
	}

	
	@Override
	public void timerWait() {
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * Removes the player from region checks
	 * @param uuid uuid to remove
	 */
	public boolean removePlayer(UUID uuid) {
		return regions.remove(uuid) != null;
	}
}
