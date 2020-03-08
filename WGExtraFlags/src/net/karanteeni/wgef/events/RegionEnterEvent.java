package net.karanteeni.wgef.events;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionEnterEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Set<ProtectedRegion> regions;
	private Player player;
	
	public RegionEnterEvent(Player player, Set<ProtectedRegion> regions) {
		this.regions = regions;
		this.player = player;
	}
	
	
	/**
	 * Get the player who left the region
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	
	/**
	 * Get all regions the player has left
	 */
	public Set<ProtectedRegion> getRegions() {
		return new HashSet<ProtectedRegion>(this.regions);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
}
