package net.karanteeni.bungee;

public class KaranteeniBungeeCore extends KaranteeniBungee {
	private static KaranteeniBungeeCore instance;
	
	public KaranteeniBungeeCore() {
		super("KaranteeniBungee");
		instance = this;
	}


	@Override
	public void onEnable() {
		super.enable();
		enableCommands();
		enableEvents();
		getLogger().info("§2KaranteeniBungee has loaded!");
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	/**
	 * Returns an instance of this plugin
	 * @return instance of this plugin
	 */
	public static KaranteeniBungeeCore getInstance() {
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
		/*getProxy().getPluginManager().registerListener(this, new PostLogin());
		this.getProxy().getPluginManager().registerListener(this, this);
		this.getProxy().registerChannel("Return");*/
	}
}
