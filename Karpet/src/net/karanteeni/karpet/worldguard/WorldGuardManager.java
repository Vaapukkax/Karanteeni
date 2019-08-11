package net.karanteeni.karpet.worldguard;

import org.bukkit.Location;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardManager {
	private RegionContainer rg = null;
	private final StateFlag ALLOW_CARPETS = new StateFlag("allow-carpets", true);
	
	
	/**
	 * Registers the carpet flag to worldguard
	 */
	public void registerFlags() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		registry.register(ALLOW_CARPETS);
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
	public boolean isCarpetAllowed(Location location) {
		ApplicableRegionSet regions = rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
		return regions.testState(null, ALLOW_CARPETS);
	}
}
