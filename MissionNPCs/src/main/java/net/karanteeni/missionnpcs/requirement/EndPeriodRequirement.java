package net.karanteeni.missionnpcs.requirement;

import java.util.UUID;
import org.bukkit.entity.Player;
import lombok.NonNull;
import net.karanteeni.missionnpcs.mission.Mission;
import net.karanteeni.missionnpcs.mission.MissionRequirement;

public class EndPeriodRequirement extends MissionRequirement {

	public EndPeriodRequirement(@NonNull String requirementId, @NonNull RequirementType type,
			@NonNull Mission mission) {
		super(requirementId, type, mission);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isFulfilled(@NonNull UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFulfilled(@NonNull Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

}
