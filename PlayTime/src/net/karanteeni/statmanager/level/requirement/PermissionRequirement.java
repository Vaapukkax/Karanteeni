package net.karanteeni.statmanager.level.requirement;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import net.karanteeni.statmanager.StatManager;


public class PermissionRequirement extends Requirement<String> {
	// permissions required for this rank
	private List<String> permissions;
	
	public PermissionRequirement(ConfigurationSection config, String path, String key) {
		super(config, path, key);
		
		// load the permissions
		permissions = this.getStringList();
	}

	
	@Override
	public boolean isRequirementMet(Player player) {
		for(String permission : permissions)
			if(!player.hasPermission(permission)) return false;
		return true;
	}
	
	
	public static void register(String key) {
		PermissionRequirement.register(key, PermissionRequirement.class);
	}
}
