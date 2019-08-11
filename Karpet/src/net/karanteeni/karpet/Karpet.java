package net.karanteeni.karpet;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.karpet.worldguard.WorldGuardManager;

public class Karpet extends KaranteeniPlugin {
	private CarpetHandler handler;
	private WorldGuardManager wgm;
	
	public Karpet() {
		super(true);
	}
	
	
	@Override
	public void onLoad() {
		// register worldguard flags
		try {
			wgm = new WorldGuardManager();
			wgm.registerFlags();
		} catch (NoClassDefFoundError e) {
			// no worldguard on server
			wgm = null;
		}
	}
	
	
	@Override
	public void onEnable() {
		register();
		
		if(wgm != null && this.getServer().getPluginManager().getPlugin("WorldGuard") != null && 
				this.getServer().getPluginManager().getPlugin("WorldGuard").isEnabled()) {
			wgm.register();	
		} else {
			wgm = null;
		}
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void register() {
		// register handler
		handler = new CarpetHandler(wgm);
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