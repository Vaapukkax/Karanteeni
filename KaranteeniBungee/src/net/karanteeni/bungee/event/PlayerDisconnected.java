package net.karanteeni.bungee.event;

import net.karanteeni.bungee.BungeeMain;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnected implements Listener {
	
	@EventHandler
	public void playerDisconnect(PlayerDisconnectEvent event)
	{
		BungeeMain.getInstance().getLogger().info(event.getPlayer().getDisplayName() + " has disconnected ;c");
	}
}
