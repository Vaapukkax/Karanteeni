package net.karanteeni.missionnpcs.mission;

import java.util.UUID;
import org.bukkit.entity.Player;
import lombok.Getter;
import lombok.NonNull;

/**
 * A generic requirement for a mission
 * @author Nuubles
 *
 */
public abstract class MissionRequirement {
	@Getter private final RequirementType type;
	@Getter private final String requirementId;
	@Getter private final Mission mission;
	
	public MissionRequirement(
			@NonNull final String requirementId,
			@NonNull final RequirementType type,
			@NonNull Mission mission) {
		this.requirementId = requirementId;
		this.type = type;
		this.mission = mission;
	}
	
	
	/**
	 * Check whether or not the player has fulfilled this requirement
	 * @param player
	 */
	public abstract boolean isFulfilled(@NonNull final UUID uuid);
	
	
	/**
	 * Check whether or not the player has fulfilled this requirement
	 * @param player
	 */
	public abstract boolean isFulfilled(@NonNull final Player player);
	
	
	/**
	 * Marks this mission an fulfilled
	 * @param player
	 */
	public void fulfill(@NonNull final Player player) {
		this.mission.completeRequirement(this, player);
	}
	
	
	/**
	 * Marks this requirement as failed, will cancel the mission
	 * @param player
	 */
	public void fail(@NonNull final Player player) {
		this.mission.requirementFailed(this, player);
	}
	
	
	/**
	 * Get the description of this requirement for the player
	 * The NPC of this mission will speak this text, prefer translations
	 * @param player
	 */
	public abstract String getDescription(final Player player);
	
	
	public enum RequirementType {
		START,
		CONTINUE,
		END
	}
}