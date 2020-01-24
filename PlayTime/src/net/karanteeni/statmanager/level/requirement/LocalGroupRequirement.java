package net.karanteeni.statmanager.level.requirement;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;


public class LocalGroupRequirement extends Requirement<String> {
	// permissions required for this rank
	private Group group;
	private KaranteeniPerms plugin;
	
	public LocalGroupRequirement(ConfigurationSection config, String path, String key) throws IllegalArgumentException {
		super(config, path, key);
		
		// load the permissions
		plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		group = plugin.getGroupList().getGroup(this.getString());
		
		if(group == null) 
			throw new IllegalArgumentException("No group found with group ID '" + this.getString() + "'");
	}

	
	@Override
	public boolean isRequirementMet(Player player) {
		// check if the group is the same as required group
		return plugin.getPermissionPlayer(player.getUniqueId()).getGroup().equals(group);
	}
	
	
	public static void register(String key) {
		LocalGroupRequirement.register(key, LocalGroupRequirement.class);
	}
}
