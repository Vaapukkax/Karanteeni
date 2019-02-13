package net.karanteeni.bungee;

import net.karanteeni.bungee.command.Ping;
import net.karanteeni.bungee.event.PlayerDisconnected;
import net.karanteeni.bungee.event.PostLogin;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {
	private static BungeeMain instance;
	
	public BungeeMain()
	{
		instance = this;
	}
	
	public static BungeeMain getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEnable()
	{
		enableCommands();
		enableEvents();
		getLogger().info("§2KaranteeniBungee has loaded!");
	}
	
	/**
	 * Rekisteröi pluginin komennot
	 */
	private void enableCommands()
	{
		getProxy().getPluginManager().registerCommand(this, new Ping());
	}
	
	/**
	 * Rekisteröi pluginin eventit
	 */
	private void enableEvents()
	{
		getProxy().getPluginManager().registerListener(this, new PlayerDisconnected());
		getProxy().getPluginManager().registerListener(this, new PostLogin());
	}
}
