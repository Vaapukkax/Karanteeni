package net.karanteeni.teleportal.spawn;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.teleportal.Teleportal;

public class SpawnManager {
	private final Teleportal plugin;
	private static final String SPAWN = "spawn";
	private static final String NOOBSPAWN = "noobspawn.location";
	private static final String RESPAWN = "respawn.location";
	
	public SpawnManager(Teleportal teleportal) {
		plugin = teleportal;
	}
	
	
	/**
	 * Teleports a player to spawn
	 * @param player player to teleport
	 * @return location if spawn is set and teleport successful
	 */
	public Location teleportToSpawn(Player player) {
		if(!isSpawnSet()) return null;
				
		Teleporter tp = new Teleporter(getSpawnLocation());
		return tp.teleport(player, true);
		
	}
	
	
	/**
	 * Teleports a player to spawn
	 * @param player player to teleport
	 * @return location if spawn is set and teleport successful
	 */
	public boolean teleportToSpawn(Collection<Player> players) {
		if(!isSpawnSet()) return false;
				
		Teleporter tp = new Teleporter(getSpawnLocation());
		
		for(Player player : players)
			tp.teleport(player, true);
		return true;
	}
	
	
	/**
	 * Returns the current spawn location
	 * @return the spawn location or null if spawn is not set
	 */
	public Location getSpawnLocation() {
		if(!plugin.getConfig().isSet(SPAWN)) return null;
		
		return (Location)plugin.getConfig().get(SPAWN);
	}
	
	
	/**
	 * Check if the spawn location has been set to the config
	 * @return true if spawn has been set, false if not
	 */
	public boolean isSpawnSet() {
		return plugin.getConfig().isSet(SPAWN);
	}
	
	
	/**
	 * Deletes spawn if spawn has been set
	 * @return true if spawn has been set, false otherwise
	 */
	public boolean deleteSpawn() {
		if(!isSpawnSet()) return false;
		
		plugin.getConfig().set(SPAWN, null);
		plugin.saveConfig();
		return true;
	}
	
	
	/**
	 * Set the spawn location
	 * @param location location for spawn
	 */
	public void setSpawn(Location location) {
		plugin.getConfig().set(SPAWN, location);
		plugin.saveConfig();
	}
	
	
	/**
	 * Teleports a player to spawn
	 * @param player player to teleport
	 * @param failsafe if true then this will attempt to teleport to normal spawn location
	 * if noobspawn is not set
	 * @return location if spawn is set and teleport successful
	 */
	public Location teleportToNoobSpawn(Player player) {
		boolean failsafe = useNoobSpawnFailSafe();
		
		if(!isNoobSpawnSet())
			if(failsafe)
				return teleportToSpawn(player);
			else
				return null;
				
		Teleporter tp = new Teleporter(getNoobSpawnLocation());
		Location loc = tp.teleport(player, true);
		
		if(loc != null) return loc;
		
		if(failsafe)
			return teleportToSpawn(player);
		return null;
	}
	
	
	public Location teleportToRespawn(Player player) {
		boolean failsafe = useRespawnFailSafe();
		
		if(!isRespawnSet())
			if(failsafe)
				return teleportToSpawn(player);
			else
				return null;
			
		Teleporter tp = new Teleporter(getRespawnLocation());
		Location loc = tp.teleport(player, true);
		
		if(loc != null) return loc;
		if(failsafe)
			return teleportToSpawn(player);
		return null;
	}
	
	
	/**
	 * Teleports a player to spawn
	 * @param player player to teleport
	 * @param failsafe if true then this will attempt to teleport to normal spawn location
	 * if noobspawn is not set
	 * @return location if spawn is set and teleport successful
	 */
	public boolean teleportToNoobSpawn(Collection<Player> players) {
		boolean failsafe = useNoobSpawnFailSafe();
		
		if(!isNoobSpawnSet())
			if(failsafe)
				return teleportToSpawn(players);
			else
				return false;
				
		Teleporter tp = new Teleporter(getNoobSpawnLocation());
		for(Player player : players) {
			// teleport failed, use failsafe
			if(tp.teleport(player, true) == null && failsafe)
				teleportToSpawn(player);
		}
		
		return true;
	}
	
	
	/**
	 * Does the plugin use failsafe when teleporting to noobspawn. That means
	 * if noobspawn is not or tp fails then use normal spawn
	 * @return true if failsafe active, false otherwise
	 */
	public boolean useNoobSpawnFailSafe() {
		if(!plugin.getConfig().isSet("noobspawn.failsafe")) {
			plugin.getConfig().set("noobspawn.failsafe", true);
			plugin.saveConfig();
		}
		
		return plugin.getConfig().getBoolean("noobspawn.failsafe");
	}
	
	
	/**
	 * Does the plugin use failsafe when teleporting to respawn. That means
	 * if noobspawn is not or tp fails then use normal spawn
	 * @return true if failsafe active, false otherwise
	 */
	public boolean useRespawnFailSafe() {
		if(!plugin.getConfig().isSet("respawn.failsafe")) {
			plugin.getConfig().set("respawn.failsafe", true);
			plugin.saveConfig();
		}
		
		return plugin.getConfig().getBoolean("respawn.failsafe");
	}
	
	
	/**
	 * Returns the current spawn location
	 * @return the spawn location or null if spawn is not set
	 */
	public Location getNoobSpawnLocation() {
		if(!plugin.getConfig().isSet(NOOBSPAWN)) return null;
		
		return (Location)plugin.getConfig().get(NOOBSPAWN);
	}
	
	
	/**
	 * Returns the current respawn location
	 * @return the respawn location or null if spawn is not set
	 */
	public Location getRespawnLocation() {
		if(!plugin.getConfig().isSet(RESPAWN)) return null;
		
		return (Location)plugin.getConfig().get(RESPAWN);
	}
	
	
	/**
	 * Check if the spawn location has been set to the config
	 * @return true if spawn has been set, false if not
	 */
	public boolean isNoobSpawnSet() {
		return plugin.getConfig().isSet(NOOBSPAWN);
	}
	
	
	/**
	 * Check if the spawn location has been set to the config
	 * @return true if spawn has been set, false if not
	 */
	public boolean isRespawnSet() {
		return plugin.getConfig().isSet(RESPAWN);
	}
	
	
	/**
	 * Deletes spawn if spawn has been set
	 * @return true if spawn has been set, false otherwise
	 */
	public boolean deleteNoobSpawn() {
		if(!isNoobSpawnSet()) return false;
		
		plugin.getConfig().set(NOOBSPAWN, null);
		plugin.saveConfig();
		return true;
	}
	
	
	/**
	 * Set the spawn location
	 * @param location location for spawn
	 */
	public void setNoobSpawn(Location location) {
		plugin.getConfig().set(NOOBSPAWN, location);
		plugin.saveConfig();
	}
	
	
	/**
	 * Deletes spawn if spawn has been set
	 * @return true if spawn has been set, false otherwise
	 */
	public boolean deleteRespawn() {
		if(!isRespawnSet()) return false;
		
		plugin.getConfig().set(RESPAWN, null);
		plugin.saveConfig();
		return true;
	}
	
	
	/**
	 * Set the spawn location
	 * @param location location for spawn
	 */
	public void setRespawn(Location location) {
		plugin.getConfig().set(RESPAWN, location);
		plugin.saveConfig();
	}
}
