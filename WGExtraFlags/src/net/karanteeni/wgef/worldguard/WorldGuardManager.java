package net.karanteeni.wgef.worldguard;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.World;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardManager {
	private RegionContainer rg = null;
	private final HashMap<String, Flag<?>> flags = new HashMap<String, Flag<?>>();
	
	public void addFlagToRegister(String name, boolean defaultState) {
		flags.put(name, new StateFlag(name, defaultState));
	}
	
	
	public void addBooleanFlag(String name, String defaultState) {
		flags.put(name, new StringFlag(name, defaultState));
	}
	
	
	public void addStringFlag(String name) {
		flags.put(name, new StringFlag(name));
	}
	
	
	public void addDoubleFlag(String name) {
		flags.put(name, new DoubleFlag(name));
	}
	
	
	public void addLocationFlag(String name) {
		flags.put(name, new LocationFlag(name));
	}
	
	
	public void addIntegerFlagToRegister(String name) {
		flags.put(name, new IntegerFlag(name));
	}
	
	
	public Flag<?> getFlag(String name) {
		return flags.get(name);
	}
	
	
	/**
	 * Registers the carpet flag to worldguard
	 */
	public void registerFlags() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		for(Flag<?> flag : flags.values())
			registry.register(flag);
	}
	
	
	/**
	 * Called after the plugin has been enabled
	 */
	public void register() {
		rg = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}
	
	
	/**
	 * Returns the regionmanager for given world
	 * @param world world where the regionmanager is taken from
	 * @return
	 */
	public RegionManager getRegionManager(World world) {
		return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
	}
	
	
	public ApplicableRegionSet getRegions(Location location) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.getApplicableRegions(BukkitAdapter.adapt(location));
	}
	
	
	/**
	 * Check if the carpet is allowed at the given location
	 * @param location location to check if the carpets are allowed
	 * @return true if carpets are allowed, false otherwise
	 */
	/*public boolean isInHalloweenGame(Location location) {
		ApplicableRegionSet regions = rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
		return regions.testState(null, HALLOWEEN_GAME);
	}*/
}
