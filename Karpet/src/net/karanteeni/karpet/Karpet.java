package net.karanteeni.karpet;

import net.karanteeni.core.KaranteeniPlugin;

public class Karpet extends KaranteeniPlugin {
	private CarpetHandler handler;
	
	public Karpet() {
		super(true);
	}
	
	
	@Override
	public void onEnable() {
		register();
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void register() {
		// register handler
		handler = new CarpetHandler();
		Karpet.getTimerHandler().registerTimer(handler, 2);
		
		// register commands
		CarpetCommand cc = new CarpetCommand(handler);
		cc.register();
		
		// register events
		getServer().getPluginManager().registerEvents(new DamageEvent(this), this);
	}
	
	
	/**
	 * Returns the carpet handler
	 * @return handler to manage the magic carpets
	 */
	public CarpetHandler getCarpetHandler() {
		return this.handler;
	}
}