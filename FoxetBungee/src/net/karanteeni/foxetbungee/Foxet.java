package net.karanteeni.foxetbungee;

import net.karanteeni.bungee.KaranteeniBungee;
import net.karanteeni.foxetbungee.ban.BanEvent;
import net.karanteeni.foxetbungee.command.Ping;
import net.md_5.bungee.api.plugin.Listener;

public class Foxet extends KaranteeniBungee implements Listener {
	private DataManager dataManager = null;
	private static Foxet instance;
	
	public Foxet() {
		super("FoxetBungee");
		instance = this;
	}
	
	
	@Override
	public void onEnable() {
		this.dataManager = new DataManager(this);
		enableCommands();
		enableEvents();
		getLogger().info("§2Foxet anticheat has loaded. Stay safe");
	}
	
	
	public DataManager getDataManager() {
		return dataManager;
	}
	
	
	/**
	 * Returns an instance of the currently used foxet plugin
	 * @return instance of the plugin
	 */
	public static Foxet getInstance() {
		return instance;
	}
	
	
	/**
	 * Register plugin commands
	 */
	private void enableCommands() {
		getProxy().getPluginManager().registerCommand(this, new Ping());
	}
	
	
	/**
	 * Register plugin events
	 */
	private void enableEvents() {
		this.getProxy().registerChannel("foxet:ban");
		this.getProxy().getPluginManager().registerListener(this, new BanEvent(this));
	}
}
