package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.UUID;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class JoinBungeeEvent implements Listener {
	
	/**
	 * Downloads and initializes the player data such as permissions and group
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPreLogin(PreLoginEvent event) {
		if(event.isCancelled()) return;
		
		KaranteeniPermsBungee plugin = KaranteeniPermsBungee.getInstance();
		
		UUID uuid = event.getConnection().getUniqueId();
		
		plugin.getPlayerModel().loadPermissionPlayerData(uuid);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(LoginEvent event) {
		// remove data from memory if login gets cancelled
		if(event.isCancelled()) {
			KaranteeniPermsBungee.getInstance().getPlayerModel().clearPermissionPlayer(event.getConnection().getUniqueId());
		}
	}
	
	
	/**
	 * When the player disconnects from proxy remove all their data
	 * @param event
	 */
	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		KaranteeniPermsBungee.getInstance().getPlayerModel().clearPermissionPlayer(event.getPlayer().getUniqueId());
	}
}
