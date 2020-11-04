package net.karanteeni.missionnpcs.mission;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import net.karanteeni.missionnpcs.MissionNPCs;
import net.karanteeni.missionnpcs.Saveable;

public class Mission implements Comparable<Mission> {
	@Getter @Setter private NPC missionEntity;
	@Getter @Setter private NPC targetEntity;
	@Getter @Setter private Mission nextMission;
	@Getter @Setter private Mission previousMission;
	@Getter private List<MissionRequirement> startRequirements = null;
	@Getter private List<MissionRequirement> continueRequirements = null;
	@Getter private List<MissionRequirement> endRequirements = null;
	@Getter private List<MissionReward> rewards = null;
	@Getter @Setter private boolean celebration;
	@Getter private Speech beginSpeech;
	@Getter private Speech continueSpeech;
	@Getter private Speech endSpeech;
	@Getter private final String missionId;
	
	
	public Mission(@NonNull final String missionId) {
		this.missionId = missionId;
	}
	
	
	public void addRequirement(@NonNull final MissionRequirement requirement) {
		switch(requirement.getType()) {
			case START:
				this.startRequirements.add(requirement);
				break;
			case CONTINUE:
				this.continueRequirements.add(requirement);
				break;
			case END:
				this.endRequirements.add(requirement);
				break;
		}
	}
	
	
	public void addReward(@NonNull final MissionReward reward) {
		this.rewards.add(reward);
	}
	
	
	public void completeRequirement(@NonNull final MissionRequirement requirement, @NonNull final Player player) {
		if(isReadyToComplete(player)) {
			if(this.endSpeech != null)
				this.endSpeech.speak(this.missionEntity, player, this::completeSpeechFinished);
			else
				completeSpeechFinished(player);
		}
	}
	
	
	/**
	 * Called when a requirement fails permanently, such as player eats a cookie that he had to deliver to another NPC
	 * @param requirement
	 * @param player
	 */
	public void requirementFailed(@NonNull final MissionRequirement requirement, @NonNull final Player player) {
		throw new Error("unimplemented");
	}
	
	
	/**
	 * Called when the mission has been completed and the finishing end speech
	 * has also completed
	 * @param player
	 */
	private void completeSpeechFinished(@NonNull Player player) {
		giveRewards(player);
		if(this.celebration)
			celebrate(player);
		
		// continue to next mission
		if(this.nextMission != null)
			this.nextMission.activate(player);
		this.deactivate(player);
	}
	
	
	/**
	 * Activates the mission for the given player
	 * @param player
	 */
	public void activate(@NonNull final Player player) {
		this.beginSpeech.speak(missionEntity, player, this::startSpeechFinished);
	}
	
	
	/**
	 * Checks if the player can start the given mission
	 * @param player
	 * @return
	 */
	public boolean canActivate(@NonNull final Player player) {
		for(final MissionRequirement req : this.startRequirements) {
			if(!req.isFulfilled(player))
				return false;
		}
		return true;
	}
	
	
	/**
	 * Starts the actual mission itself
	 * @param player
	 */
	private void startSpeechFinished(@NonNull Player player) {
		Bukkit.broadcastMessage(ChatColor.RED + "Activate mission " + this.missionId);
	}
	
	
	/**
	 * Deactivates this mission for the given player
	 * @param player
	 */
	public void deactivate(@NonNull final Player player) {
		Bukkit.broadcastMessage(ChatColor.RED + "Deactivate mission " + this.missionId);
	}
	
	
	private void celebrate(@NonNull final Player player) {
		if(player.isOnline()) {
			player.playSound(
					player.getLocation().add(0, 100000, 0),
					Sound.UI_TOAST_CHALLENGE_COMPLETE,
					SoundCategory.PLAYERS,
					1000000000,
					1.6f);
		}
	}
	
	
	private void giveRewards(@NonNull final Player player) {
		if(rewards != null)
			for(MissionReward reward : rewards)
				reward.giveReward(player);
	}
	
	
	private boolean isReadyToComplete(@NonNull final Player player) {
		boolean complete = true;
		for(final MissionRequirement endRequirement : endRequirements) {
			complete = endRequirement.isFulfilled(player);
			if(!complete)
				break;
		}
		return complete;
	}
	
	
	/**
	 * Saves this mission to the config
	 * @param plugin
	 */
	public void save(@NonNull MissionNPCs plugin) {
		for(MissionRequirement req : this.startRequirements)
			this.saveRequirement(plugin, plugin.getSettings().getConfigurationSection(this.missionId+".start-requirements"), req);
		
		for(MissionRequirement req : this.continueRequirements)
			this.saveRequirement(plugin, plugin.getSettings().getConfigurationSection(this.missionId+".continue-requirements"), req);

		for(MissionRequirement req : this.endRequirements)
			this.saveRequirement(plugin, plugin.getSettings().getConfigurationSection(this.missionId+".end-requirements"), req);
	}
	
	
	/**
	 * Deletes this mission from the config files
	 * @param plugin
	 */
	public void delete(@NonNull MissionNPCs plugin) {
		plugin.getNpcManager().removeMission(this);
		plugin.getSettings().set(this.missionId, null);
		if(!this.isFirst())
			this.previousMission.nextMission = this.nextMission;
	}
	
	
	/**
	 * Saves the requirements of this mission to the given requirement section
	 * @param plugin
	 * @param requirement
	 */
	public void saveRequirement(@NonNull MissionNPCs plugin, @NonNull ConfigurationSection section, @NonNull MissionRequirement requirement) {
		for(Field field : requirement.getClass().getDeclaredFields()) {
			if(field.isAnnotationPresent(Saveable.class)) {
				try {
					section.set(plugin.getRequirements().getRequirementTag(requirement.getClass()) + "." + field.getName(), field.get(requirement));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Is this mission currently active for the given player
	 * @param uuid uuid to whom the mission is active
	 * @return true if the mission is active
	 */
	public boolean isActive(@NonNull final UUID uuid) {
		return false;
	}
	
	
	/**
	 * Is this mission completed for the given player
	 * @param uuid uuid to whom the mission is 
	 * @return true if the mission is complete for the player
	 */
	public boolean isCompleted(@NonNull final UUID uuid) {
		return false;
	}
	
	
	/**
	 * Is this mission the first mission in a possible sequence
	 * @return true if this is the first mission in a sequence
	 */
	public boolean isFirst() {
		return this.previousMission == null;
	}
	
	
	/**
	 * Is this mission the last mission in a possible sequence
	 * @return true if this is the last mission in a sequence
	 */
	public boolean isLast() {
		return this.nextMission == null;
	}


	@Override
	public int compareTo(Mission mission) {
		if(mission == null)
			return -1;
		return this.missionId.compareTo(mission.missionId);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		else if(o instanceof String)
			return this.missionId.equals(o);
		else if(o instanceof Mission)
			return this.missionId.equals(((Mission)o).missionId);
		return false;
	}
}
