package net.karanteeni.foxet;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.foxet.punishment.BanCommand;

public class Foxet extends KaranteeniPlugin {
	
	public Foxet() {
		super(true);
	}
	
	
	@Override
	public void onEnable() {
		//Register the config variables
		registerConfig();
		//Register the events
		registerEvents();
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	/**
	 * Registers the events of this plugin
	 */
	private void registerEvents() {
		
	}
	
	
	/**
	 * Register all config values
	 */
	private void registerConfig() {
		BanCommand ban = new BanCommand();
		PlayerLoader loader = new PlayerLoader(true, true, false, true, true);
		ban.setPermission("foxet.punishment.ban");
		ban.setLoader(loader);
		ban.register();
		getServer().getMessenger().registerOutgoingPluginChannel(this, "foxet:ban");
		getServer().getMessenger().registerIncomingPluginChannel(this, "foxet:ban", ban);
	}
}
