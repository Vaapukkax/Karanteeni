package net.karanteeni.foxet;

import net.karanteeni.core.KaranteeniPlugin;

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
		TestCommand test = new TestCommand();
		test.register();
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "Return", test);
	}
}
