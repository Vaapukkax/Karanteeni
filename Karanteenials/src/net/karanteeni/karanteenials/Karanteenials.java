package net.karanteeni.karanteenials;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.BinaryComponent;
import net.karanteeni.core.command.defaultcomponent.MaterialLoader;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.karanteenials.functionality.PlayerFunctionality;
import net.karanteeni.karanteenials.player.BurnComponent;
import net.karanteeni.karanteenials.player.ExtinguishComponent;
import net.karanteeni.karanteenials.player.FeedComponent;
import net.karanteeni.karanteenials.player.FlyCommand;
import net.karanteeni.karanteenials.player.HealComponent;
import net.karanteeni.karanteenials.player.InvincibleComponent;
import net.karanteeni.karanteenials.player.KillComponent;
import net.karanteeni.karanteenials.player.LightningComponent;
import net.karanteeni.karanteenials.player.LightningEffectComponent;
import net.karanteeni.karanteenials.player.PlayerCommand;
import net.karanteeni.karanteenials.player.SpeedCommand;
import net.karanteeni.karanteenials.player.StarveComponent;
import net.karanteeni.karanteenials.player.gamemode.GameModeCommand;
import net.karanteeni.karanteenials.player.gamemode.GameModeLoader;
import net.karanteeni.karanteenials.player.home.DelHomeCommand;
import net.karanteeni.karanteenials.player.home.HomeCommand;
import net.karanteeni.karanteenials.player.home.ListHomesCommand;
import net.karanteeni.karanteenials.player.home.SetHomeCommand;
import net.karanteeni.karanteenials.player.misc.ClearInventoryCommand;
import net.karanteeni.karanteenials.player.misc.EnderChestCommand;
import net.karanteeni.karanteenials.player.misc.HatCommand;
import net.karanteeni.karanteenials.player.misc.NickCommand;
import net.karanteeni.karanteenials.player.misc.NickSetEvent;
import net.karanteeni.karanteenials.player.misc.WorkbenchCommand;
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
	public void onEnable() {
		data = new PlayerFunctionality(this);
		
		registerConfig();
		registerEvents();
		registerCommands();
	}
	
	
	@Override
	public void onDisable() { }
	
	
	private void registerConfig() {
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
	
	
	private void registerEvents() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.NICK.toString())) {
			NickSetEvent.register(this);
		}
	}
	
	
	private void registerCommands() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.HOME.toString())) {
			(new HomeCommand(this)).register();
			(new SetHomeCommand(this)).register();
			(new DelHomeCommand(this)).register();
			(new ListHomesCommand(this)).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.BACK.toString())) {
			(new Back(this)).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.TPASK.toString())) {
			(new TeleportToggle(this)).register();
			(new TpAsk(this)).register();
			(new TpAskHere(this)).register();
			(new TeleportAccept(this)).register();
			(new TeleportDeny(this)).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.TELEPORT.toString())) {
			(new Teleport(this)).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.SPEED_SETTING.toString())) {
			(new SpeedCommand(this)).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.FLY.toString())) {
			(new FlyCommand(this)).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.GAMEMODE.toString())) {
			GameModeCommand gmc = new GameModeCommand(this);
			PlayerLoader loader = new PlayerLoader(true, false, false, false);
			GameModeLoader gml = new GameModeLoader(true);
			gmc.setLoader(gml);
			gml.setLoader(loader);
			gmc.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.PLAYER_CMD.toString())) {
			// create command and player loader
			PlayerCommand pc = new PlayerCommand();
			PlayerLoader playerLoader = new PlayerLoader(true, false, false, false);
			
			// create components
			FeedComponent fc = new FeedComponent();
			fc.setLoader(playerLoader);
			StarveComponent sc = new StarveComponent();
			sc.setLoader(playerLoader);
			HealComponent hc = new HealComponent();
			hc.setLoader(playerLoader);
			KillComponent kc = new KillComponent();
			kc.setLoader(playerLoader);
			BurnComponent bc = new BurnComponent();
			bc.setLoader(playerLoader);
			ExtinguishComponent ec = new ExtinguishComponent();
			ec.setLoader(playerLoader);
			LightningComponent lc = new LightningComponent();
			lc.setLoader(playerLoader);
			LightningEffectComponent lec = new LightningEffectComponent();
			lec.setLoader(playerLoader);
			
			PlayerLoader gcLoader = new PlayerLoader(false, false, false, false);
			BinaryComponent ooc = new BinaryComponent(true, BinaryComponent.BINARY.ENABLE_DISABLE);
			ooc.setLoader(gcLoader);
			
			InvincibleComponent gc = new InvincibleComponent();
			gc.setLoader(ooc);
			
			// add components
			pc.addComponent("feed", fc);
			pc.addComponent("starve", sc);
			pc.addComponent("heal", hc);
			pc.addComponent("invincible", gc);
			pc.addComponent("kill", kc);
			pc.addComponent("burn", bc);
			pc.addComponent("extinguish", ec);
			pc.addComponent("lightning", lc);
			pc.addComponent("lightningeffect", lec);
			
			// register command
			pc.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.HAT.toString())) {
			// /hat command
			(new HatCommand()).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.WORKBENCH_ENDERCHEST.toString())) {
			// /enderchest and /workbench commands
			WorkbenchCommand wb = new WorkbenchCommand();
			wb.setPermission("karanteenials.inventory.workbench");
			wb.register();
			
			EnderChestCommand ec = new EnderChestCommand();
			ec.setPermission("karanteenials.inventory.enderchest");
			ec.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.CLEAR_INVENTORY.toString())) {
			ClearInventoryCommand cic = new ClearInventoryCommand();
			PlayerLoader pl = new PlayerLoader(true, false, false, false);
			MaterialLoader ml = new MaterialLoader(true, false, false);
			pl.setLoader(ml);
			cic.setLoader(pl);
			pl.setPermission("karanteenials.inventory.clear-multiple");
			cic.setPermission("karanteenials.inventory.clear");
			cic.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.NICK.toString())) {
			NickCommand nc = new NickCommand();
			nc.register();
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
		FLY,
		GAMEMODE,
		HAT,
		PLAYER_CMD,
		WORKBENCH_ENDERCHEST,
		CLEAR_INVENTORY,
		NICK
	}
}