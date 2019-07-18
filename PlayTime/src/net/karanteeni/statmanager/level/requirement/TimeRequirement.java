package net.karanteeni.statmanager.level.requirement;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import net.karanteeni.statmanager.StatManager;
import net.karanteeni.statmanager.Time;


public class TimeRequirement extends Requirement<String> {
	// permissions required for this rank
	private long requiredTime;
	private StatManager plugin;
	
	public TimeRequirement(ConfigurationSection config, String path, String key) {
		super(config, path, key);
		
		// load the permissions
		this.requiredTime = this.getLong();
		this.plugin = StatManager.getPlugin(StatManager.class);
	}

	
	@Override
	public boolean isRequirementMet(Player player) {
		Time time = plugin.getManager().forceLoadTime(player.getUniqueId());
		return time.getTime() >= requiredTime;
	}
	
	
	
	public static void register(String key) {
		TimeRequirement.register(key, TimeRequirement.class);
	}
}
