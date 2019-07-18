package net.karanteeni.regionmanager;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.regionmanager.structure.elevator.Elevator;

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
		getServer().getPluginManager().registerEvents(new Elevator(), this);
	}
	
	
	private void registerCommands() {

	}
}