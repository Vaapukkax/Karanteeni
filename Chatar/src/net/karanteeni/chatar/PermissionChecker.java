package net.karanteeni.chatar;

import java.util.UUID;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;

public class PermissionChecker {
	private KaranteeniPerms plugin;
	
	public PermissionChecker() {
		plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
	}
	
	
	/**
	 * Returns the permissionplugin
	 * @return
	 */
	public KaranteeniPerms getPlugin() {
		return this.plugin;
	}
	
	
	/**
	 * Checks if the given player has the given permission
	 * @param uuid
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(UUID uuid, String permission) {
		PermissionPlayer player = plugin.getPermissionPlayer(uuid);
		return player.hasPermission(permission);
	}
}
