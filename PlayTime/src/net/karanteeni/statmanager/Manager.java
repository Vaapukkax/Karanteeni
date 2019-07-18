package net.karanteeni.statmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.karanteeni.statmanager.level.TimeLevel;

/**
 * Manager is responsible for checking all of the levels for players and updating them.
 * Manager also knows all of the levels registered and the total playing times of players
 * @author Nuubles
 *
 */
public class Manager {
	private HashMap<UUID, Time> times = new HashMap<UUID, Time>();
	private List<TimeLevel> timeLevels = new ArrayList<TimeLevel>();
	
	
	/**
	 * Adds a new time level to the list. The added time level will be checked
	 * when any of the functions 'performLevelCheck' is called 
	 * @param level level to be checked on performLevelCheck call
	 */
	public void addTimeLevel(TimeLevel level) {
		this.timeLevels.add(level);
	}
	
	
	/**
	 * Perform level check for all of the online players on this server
	 */
	public void performLevelCheck() {
		for(Player player : Bukkit.getOnlinePlayers())
			performLevelCheck(player);
	}
	
	
	/**
	 * Perform a level check for a given player
	 * @param player player to whom a level check will be performed
	 */
	public void performLevelCheck(Player player) {
		for(TimeLevel level : timeLevels)
		if(level.tryLevelUp(player))
			Bukkit.getLogger().log(Level.INFO, 
				String.format("Player %s (%s) successfully leveled to level '%s'",
				player.getUniqueId(), player.getName(), level.getName()));
	}
	
	
	/**
	 * Loads the time of a given player to memory
	 * @param uuid uuid whose time should be loaded to memory
	 * @return the loaded time or null if could not load
	 */
	public Time loadToMemory(UUID uuid) {
		if(hasUUID(uuid)) return null;
		Time time = Time.loadTime(uuid);
		if(time == null) return null;
		
		times.put(uuid, time);
		return time;
	}
	
	
	/**
	 * Returns the time currently in memory
	 * @param uuid uuid the time is associated with
	 * @return the time in memory or null
	 */
	public Time getTime(UUID uuid) {
		return this.times.get(uuid);
	}
	
	
	/**
	 * Returns the time currently in memory and if it does not exist, load the time
	 * from database and return that instead
	 * @param uuid uuid whose time will be returned
	 * @return the time loaded
	 */
	public Time forceLoadTime(UUID uuid) {
		Time time = this.times.get(uuid);
		if(time != null) return time;
		return Time.loadTime(uuid);
	}
	
	
	/**
	 * Unloads a given uuid from memory and saves it
	 * @param uuid uuid to unload
	 * @return time which was associated to the uuid
	 */
	public Time unloadUUID(UUID uuid) {
		if(!times.containsKey(uuid)) return null;
		Time time = times.remove(uuid);
		time.save();
		return time;
	}
	
	
	/**
	 * Saves all times in the memory
	 */
	public void save() {
		for(Time time : times.values())
			time.save();
	}
	
	
	/**
	 * Checks if the manager currently has the time for the given uuid
	 * @param uuid uuid to check
	 * @return true if uuid is in memory, false otherwise
	 */
	public boolean hasUUID(UUID uuid) {
		return this.times.containsKey(uuid);
	}
}
