package net.karanteeni.acidaddons;

import org.bukkit.plugin.java.JavaPlugin;
import net.karanteeni.acidaddons.events.CobbleStoneForm;

public class AcidAddons extends JavaPlugin {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new CobbleStoneForm(), this);
	}
	
	
	@Override
	public void onDisable() {
		
	}
}
