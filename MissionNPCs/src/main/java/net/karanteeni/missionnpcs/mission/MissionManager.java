package net.karanteeni.missionnpcs.mission;

import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import lombok.NonNull;
import net.karanteeni.missionnpcs.MissionNPCs;

public class MissionManager {
	private HashMap<String, Mission> missions = new HashMap<String, Mission>();
	private MissionNPCs plugin;
	
	public MissionManager(@NonNull MissionNPCs plugin) {
		this.plugin = plugin;
		ConfigurationSection missionSection = plugin.getSettings().getConfigurationSection("mission");
		
		for(String key : missionSection.getKeys(false)) {
			Mission mission = new Mission(key);
			if(missions.containsKey(mission.getMissionId())) {
				Bukkit.getLogger().log(Level.WARNING, "Duplicate mission id");
			} else {
				// store the mission to the set and load requirements for this mission
				missions.put(mission.getMissionId(), mission);
				plugin.getRequirements().loadAndAttachRequirements(mission, missionSection.getConfigurationSection(key));
			}
		}
	}
	
	
	/**
	 * Retrieves a mission with the given ID
	 * @param id id of the mission to retrieve
	 * @return mission retrieved
	 */
	public Mission getMission(@NonNull final String id) {
		return this.missions.get(id);
	}
	
	
	/**
	 * Adds a new mission to the mission set
	 * @param mission mission to add
	 * @return true if mission added, false if mission already exists
	 */
	public boolean addMission(@NonNull Mission mission) {
		if(this.missions.containsKey(mission.getMissionId()))
			return false;
		this.missions.put(mission.getMissionId(), mission);
		mission.save(plugin);
		return true;
	}
	
	
	/**
	 * Checks if a mission with the given ID exists
	 * @param id id of the mission
	 * @return true if mission exists, false otherwise
	 */
	public boolean doesMissionExist(@NonNull final String id) {
		return this.missions.containsKey(id);
	}
	
	
	/**
	 * Removes the given mission from the missions
	 * @param id
	 * @return
	 */
	public Mission removeMission(@NonNull final String id) {
		if(this.missions.containsKey(id))
			return null;
		
		Mission mission = this.missions.remove(id);
		mission.delete(plugin);
		return mission;
	}
	
	
	/**
	 * Activates the given mission for the given player
	 * @param player
	 * @param mission
	 */
	public void activateMission(@NonNull final Player player, @NonNull final Mission mission) {
		
	}
	
	
	/**
	 * Deactivates the given mission from the player
	 * @param player
	 * @param id
	 */
	public void deactivateMission(@NonNull final Player player, @NonNull final String id) {
		
	}
}
