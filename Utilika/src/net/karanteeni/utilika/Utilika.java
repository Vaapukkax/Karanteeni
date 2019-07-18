package net.karanteeni.utilika;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.utilika.items.RepairCommand;
import net.karanteeni.utilika.structure.elevator.Elevator;

public class Utilika extends KaranteeniPlugin {
	private static String KEY_PREFIX = "Plugin-functionality.";
	
	public Utilika() {
		super(true);
	}
	
	
	@Override
	public void onEnable() {
		registerEvents();
		registerCommands();
		boolean save = false;
		//Check that all possible values are set in the config
		for(KEYS key : KEYS.values()) {
			if(!getSettings().isSet(KEY_PREFIX+key.toString())) {
				getSettings().set(KEY_PREFIX+key.toString(), true);
				save = true;
			}
		}
		
		if(save)
			saveSettings();
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void registerEvents() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.ELEVATOR.toString())) {
			getServer().getPluginManager().registerEvents(new Elevator(), this);
		}
	}
	
	
	private void registerCommands() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.REPAIR.toString())) {
			RepairCommand repair = new RepairCommand();
			repair.setPermission("utilika.repair");
			repair.register();
		}
	}
	
	
	/**
	 * Keys to access data in config. Which features of the plugin are enabled
	 * @author Nuubles
	 */
	private static enum KEYS {
		REPAIR,
		ELEVATOR
	}
}