package net.karanteeni.nature.worldguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardManager {
	private RegionContainer rg = null;
	private final StateFlag LIGHTNING_TRANSFORM_BLOCKS = new StateFlag("lightning-transform-blocks", true);
	
	/**
	 * Registers the carpet flag to worldguard
	 */
	public void registerFlags() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		registry.register(LIGHTNING_TRANSFORM_BLOCKS);
	}
	
	
	/**
	 * Called after the plugin has been enabled
	 */
	public void register() {
		rg = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}
	
	
	/**
	 * Get the regions at the location of the player
	 * @param location location to get the regions from
	 * @return regions at location
	 */
	public ApplicableRegionSet getRegions(Location location) {
		return rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
	}
	
	
	/**
	 * Test if the player can build
	 * @param player
	 * @return
	 */
	public boolean canBuild(Player player, Location loc) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BUILD);
	}
	
	
	/**
	 * Check if the carpet is allowed at the given location
	 * @param location location to check if the carpets are allowed
	 * @return true if carpets are allowed, false otherwise
	 */
	public boolean canLightningTransform(Location location) {
		//ApplicableRegionSet regions = rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
		return getRegions(location).testState(null, LIGHTNING_TRANSFORM_BLOCKS);
	}
}
