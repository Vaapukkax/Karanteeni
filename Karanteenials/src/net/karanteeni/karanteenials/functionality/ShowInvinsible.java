package net.karanteeni.karanteenials.functionality;

import java.util.UUID;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class ShowInvinsible {
	private final KaranteeniPlugin plugin;
	private static final String SHOW_NEAR = "show-invisible";
	
	public ShowInvinsible(KaranteeniPlugin plugin) {
		this.plugin = plugin;
		
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		//Power level for groups
		for(Group group : perms.getGroupList().getGroups())
			if(!group.isCustomDataSet(plugin, SHOW_NEAR)) //Add the power level to each group if not set
				group.setCustomData(plugin, SHOW_NEAR, true);
	}
	
	/**
	 * Returns if the player with this uuid should be shown to lower level players in the command /near
	 * or other occurrenses
	 * @param uuid uuid of whose visibility is being checked
	 * @return should this player be shown to lower level players
	 */
	public boolean showInNear(UUID uuid) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		Group group = perms.getPermissionPlayer(uuid).getGroup();
		if(group == null)
			return true;
		return group.getCustomBoolean(plugin, SHOW_NEAR);
	}
	
	/**
	 * Returns if the player with this uuid should be shown to lower level players in the command /near
	 * or other occurrenses
	 * @param player player whose visibility is being checked
	 * @return should this player be shown to lower level players
	 */
	public boolean showInNear(Player player) {
		return showInNear(player.getUniqueId());
	}
}
