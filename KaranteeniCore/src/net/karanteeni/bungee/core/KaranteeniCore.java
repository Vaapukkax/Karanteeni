package net.karanteeni.bungee.core;

public class KaranteeniCore extends KaranteeniPlugin {
	private static KaranteeniCore instance;
	
	public KaranteeniCore() {
		super("KaranteeniCore");
		instance = this;
	}


	@Override
	public void onEnable() {
		super.enable();
		enableCommands();
		enableEvents();
		getLogger().info("§2KaranteeniCore has loaded!");
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	/**
	 * Returns an instance of this plugin
	 * @return instance of this plugin
	 */
	public static KaranteeniCore getInstance() {
		return instance;
	}
	
	
	/**
	 * Register plugin commands
	 */
	private void enableCommands() {
		
	}
	
	
	/**
	 * Register plugin events
	 */
	private void enableEvents() {

	}
}
