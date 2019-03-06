package net.karanteeni.foxet;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.foxet.event.AfkStateChangeEvent;

public class Foxet extends KaranteeniPlugin implements Listener{
	
	public Foxet()
	{
		super(true);
	}
	
	@Override
	public void onEnable()
	{
		//Register the config variables
		registerConfig();
		//Register the events
		registerEvents();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	/**
	 * Registers the events of this plugin
	 */
	private void registerEvents()
	{
		if(getConfig().getBoolean("afk.enabled")) //Are the afk checks enabled
		{
			AfkStateChangeEvent.register(this);
			getServer().getPluginManager().registerEvents(this, this);
		}
	}
	
	/**
	 * Register all config values
	 */
	private void registerConfig()
	{
		//Register AFK config
		if(!getConfig().isSet("afk.enabled"))
		{
			getConfig().set("afk.enabled", true);
			saveConfig();
		}
	}
	
	@EventHandler
	public void stateChangeEvent(AfkStateChangeEvent event)
	{
		Bukkit.broadcastMessage(event.getNewAfkState().toString());
	}
}
