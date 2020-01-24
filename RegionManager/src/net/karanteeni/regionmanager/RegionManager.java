package net.karanteeni.regionmanager;

import net.karanteeni.core.KaranteeniPlugin;

public class RegionManager extends KaranteeniPlugin
{
	public RegionManager() {
		super(true);
	}
	
	
	@Override
	public void onEnable() {
		registerEvents();
		registerCommands();
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void registerEvents() {
		
	}
	
	
	private void registerCommands() {

	}
}