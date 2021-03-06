package net.karanteeni.christmas2019.worldguard;

import org.bukkit.Location;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardManager {
	private RegionContainer rg = null;
	private final StateFlag HALLOWEEN_GAME = new StateFlag("pkgkerays", false);
	
	
	/**
	 * Registers the carpet flag to worldguard
	 */
	public void registerFlags() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		registry.register(HALLOWEEN_GAME);
	}
	
	
	/**
	 * Called after the plugin has been enabled
	 */
	public void register() {
		rg = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}
	
	
	/**
	 * Check if the carpet is allowed at the given location
	 * @param location location to check if the carpets are allowed
	 * @return true if carpets are allowed, false otherwise
	 */
	public boolean isInHalloweenGame(Location location) {
		ApplicableRegionSet regions = rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
		return regions.testState(null, HALLOWEEN_GAME);
	}
}
