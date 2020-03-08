package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PermissionCheckEventListener implements Listener {
	private HashMap<UUID, HashSet<ExtendedPermission>> permissions = new HashMap<UUID, HashSet<ExtendedPermission>>();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void permissionCheck(PermissionCheckEvent event) {
		// if not player don't interfere
		if(!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer)event.getSender();
		String permission = event.getPermission();
		//permissions.get(player.getUniqueId()).
		
	}
}
