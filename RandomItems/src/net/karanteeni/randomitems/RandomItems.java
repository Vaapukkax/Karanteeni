package net.karanteeni.randomitems;

import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.randomitems.events.FortuneCookieCrack;
import net.karanteeni.randomitems.items.FortuneCookie;

public class RandomItems extends KaranteeniPlugin {
	private static String KEY_PREFIX = "Plugin-functionality.";
	
	public RandomItems() {
		super(true);
	}
	
	@Override
	public void onLoad() {

	}
	
	
	@Override
	public void onEnable() {
		registerEvents();
		registerCommands();
		registerRecipies();
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
	
	
	public void registerCommands() {
		
	}
	
	
	public void registerEvents() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.FORTUNE_COOKIES.toString()))
			getServer().getPluginManager().registerEvents(new FortuneCookieCrack(), this);
	}
	
	
	private void registerRecipies() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.FORTUNE_COOKIES.toString()))
			Bukkit.addRecipe(FortuneCookie.getRecipe());
	}
	
	
	/**
	 * Keys to access data in config. Which features of the plugin are enabled
	 * @author Nuubles
	 */
	private static enum KEYS {
		FORTUNE_COOKIES
	}
}
