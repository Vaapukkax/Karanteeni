package net.karanteeni.karanteenials.functionality;

import java.util.UUID;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class PowerLevel {
	private final KaranteeniPlugin plugin;
	private static final String POWER_LEVEL = "power_level";
	
	public PowerLevel(KaranteeniPlugin plugin) {
		this.plugin = plugin;
		
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		//Power level for groups
		for(Group group : perms.getGroupList().getGroups())
			if(!group.isCustomDataSet(plugin, POWER_LEVEL)) //Add the power level to each group if not set
				group.setCustomData(plugin, POWER_LEVEL, 0);
	}
	
	/**
	 * Returns the Karanteenials power level of given uuids group
	 * @param uuid uuid of whose power level is being checked
	 * @return power level of uuid or -1 if not found
	 */
	public int getPowerLevel(UUID uuid) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		Group group = perms.getPermissionPlayer(uuid).getGroup();
		if(group == null)
			return -1;
		return group.getCustomInt(plugin, POWER_LEVEL);
	}
	
	/**
	 * Returns the Karanteenials power level of given players group
	 * @param player player whose power level is being checked
	 * @return power level of player or -1 if players group is not found
	 */
	public int getPowerLevel(Player player) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		Group group = perms.getPermissionPlayer(player.getUniqueId()).getGroup();
		if(group == null)
			return -1;
		return group.getCustomInt(plugin, POWER_LEVEL);
	}
}
