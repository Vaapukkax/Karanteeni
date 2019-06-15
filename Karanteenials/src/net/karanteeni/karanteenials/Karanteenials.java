package net.karanteeni.karanteenials;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.karanteenials.functionality.PlayerFunctionality;
import net.karanteeni.karanteenials.player.FlyCommand;
import net.karanteeni.karanteenials.player.SpeedCommand;
import net.karanteeni.karanteenials.player.home.DelHomeCommand;
import net.karanteeni.karanteenials.player.home.HomeCommand;
import net.karanteeni.karanteenials.player.home.ListHomesCommand;
import net.karanteeni.karanteenials.player.home.SetHomeCommand;
import net.karanteeni.karanteenials.teleport.Back;
import net.karanteeni.karanteenials.teleport.Teleport;
import net.karanteeni.karanteenials.teleport.TeleportAccept;
import net.karanteeni.karanteenials.teleport.TeleportDeny;
import net.karanteeni.karanteenials.teleport.TeleportToggle;
import net.karanteeni.karanteenials.teleport.TpAsk;
import net.karanteeni.karanteenials.teleport.TpAskHere;

public class Karanteenials extends KaranteeniPlugin
{
	PlayerFunctionality data;
	private static String KEY_PREFIX = "Plugin-functionality.";
	
	public Karanteenials() {
		super(true);
	}
	
	@Override
	public void onEnable()
	{
		data = new PlayerFunctionality(this);
		
		registerConfig();
		registerEvents();
		registerCommands();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	private void registerConfig()
	{
		boolean save = false;
		//Check that all possible values are set in the config
		for(KEYS key : KEYS.values()) {
			if(!getConfig().isSet(KEY_PREFIX+key.toString())) {
				getConfig().set(KEY_PREFIX+key.toString(), true);
				save = true;
			}
		}
		
		if(save)
			saveConfig();
	}
	
	private void registerEvents()
	{
		
	}
	
	private void registerCommands()
	{
		if(getConfig().getBoolean(KEY_PREFIX+KEYS.HOME.toString())) {
			(new HomeCommand(this)).register();
			(new SetHomeCommand(this)).register();
			(new DelHomeCommand(this)).register();
			(new ListHomesCommand(this)).register();
		}
		
		if(getConfig().getBoolean(KEY_PREFIX+KEYS.BACK.toString())) {
			(new Back(this)).register();
		}
		
		if(getConfig().getBoolean(KEY_PREFIX+KEYS.TPASK.toString())) {
			(new TeleportToggle(this)).register();
			(new TpAsk(this)).register();
			(new TpAskHere(this)).register();
			(new TeleportAccept(this)).register();
			(new TeleportDeny(this)).register();
		}
		
		if(getConfig().getBoolean(KEY_PREFIX+KEYS.TELEPORT.toString())) {
			(new Teleport(this)).register();
		}
		
		if(getConfig().getBoolean(KEY_PREFIX+KEYS.SPEED_SETTING.toString())) {
			(new SpeedCommand(this)).register();
		}
		
		if(getConfig().getBoolean(KEY_PREFIX+KEYS.FLY.toString())) {
			(new FlyCommand(this)).register();
		}
	}
	
	/**
	 * Returns the data manager of this plugin
	 * @return data manager of this plugin
	 */
	public PlayerFunctionality getPlayerData()
	{ return data; }
	
	/**
	 * Keys to access data in config. Which features of the plugin are enabled
	 * @author Nuubles
	 */
	private static enum KEYS {
		HOME, 
		BACK, 
		TPASK, 
		TELEPORT, 
		SPEED_SETTING,
		FLY
	}
}