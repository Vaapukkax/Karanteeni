package net.karanteeni.utilika;

import java.util.Arrays;
import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.utilika.block.setsign.LineLoader;
import net.karanteeni.utilika.block.setsign.SetSignCommand;
import net.karanteeni.utilika.block.setsign.SetSignComponent;
import net.karanteeni.utilika.calculator.CalculatorCommand;
import net.karanteeni.utilika.events.EasyBridge;
import net.karanteeni.utilika.external.CoreProtectAccessor;
import net.karanteeni.utilika.inventory.InventoryTweaks;
import net.karanteeni.utilika.inventory.InventoryUtilities;
import net.karanteeni.utilika.items.RepairCommand;
import net.karanteeni.utilika.items.setname.SetNameCommand;
import net.karanteeni.utilika.items.setname.SetNameComponent;
import net.karanteeni.utilika.structure.PuddleSplash;
import net.karanteeni.utilika.structure.builder.BuildersWand;
import net.karanteeni.utilika.structure.builder.WandBuilder;
import net.karanteeni.utilika.structure.elevator.Elevator;
import net.karanteeni.utilika.worldguard.WorldGuardManager;

public class Utilika extends KaranteeniPlugin {
	private WorldGuardManager wgm;
	private InventoryUtilities invUtil;
	private CoreProtectAccessor coreProtectAccessor;
	private static String KEY_PREFIX = "Plugin-functionality.";
	
	public Utilika() {
		super(true);
	}
	
	
	@Override
	public void onLoad() {
		invUtil = new InventoryUtilities();
		try {
			wgm = new WorldGuardManager();
		} catch (NoClassDefFoundError e) {
			// no worldguard on server
			wgm = null;
		}
	}
	
	
	@Override
	public void onEnable() {
		enableDependencies();
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
	
	
	private void registerRecipies() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.BUILDERS_WAND.toString()))
			Bukkit.addRecipe(BuildersWand.getRecipe());
	}
	
	
	private void enableDependencies() {
		if(this.getServer().getPluginManager().getPlugin("CoreProtect") != null) {
			this.coreProtectAccessor = new CoreProtectAccessor(this);
		}
	}
	
	
	public CoreProtectAccessor getCoreProtectAccessor() {
		return this.coreProtectAccessor;
	}
	
	
	public InventoryUtilities getInventoryUtilities() {
		return invUtil;
	}
	
	
	public WorldGuardManager getWorldGuardManager() {
		return this.wgm;
	}
	
	
	private void registerEvents() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.ELEVATOR.toString())) {
			getServer().getPluginManager().registerEvents(new Elevator(), this);
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.INVENTORY_TWEAKS.toString())) {
			getServer().getPluginManager().registerEvents(new InventoryTweaks(this), this);
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.EASYBRIDGE.toString())) {
			getServer().getPluginManager().registerEvents(new EasyBridge(), this);
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.BUILDERS_WAND.toString())) {
			getServer().getPluginManager().registerEvents(new WandBuilder(this), this);
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.THROWABLE_PUDDLE.toString())) {
			getServer().getPluginManager().registerEvents(new PuddleSplash(this), this);
		}
	}
	
	
	private void registerCommands() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.REPAIR.toString())) {
			RepairCommand repair = new RepairCommand();
			repair.setPermission("utilika.repair");
			repair.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.SETNAME.toString())) {
			SetNameCommand snc = new SetNameCommand(this, 
					"setname", 
					"/setname <name>", 
					"sets the item name", 
					KaranteeniPlugin.defaultMessages.defaultNoPermission(), Arrays.asList());
			snc.setPermission("utilika.name.use");
			SetNameComponent sco = new SetNameComponent(true);
			snc.setLoader(sco);
			snc.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.SETSIGN.toString())) {
			SetSignCommand snc = new SetSignCommand(this, 
					"setsign", 
					"/setsign <line> <name>", 
					"sets the text in the described row of a line", 
					KaranteeniPlugin.defaultMessages.defaultNoPermission(), Arrays.asList());
			snc.setPermission("utilika.setsign.use");
			SetSignComponent sco = new SetSignComponent(false);
			LineLoader linel = new LineLoader(true);
			snc.setLoader(linel);
			linel.setLoader(sco);
			snc.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.CALCULATOR.toString())) {
			CalculatorCommand calc = new CalculatorCommand(this);
			calc.setPermission("utilika.calculate");
			calc.register();
		}
	}
	
	
	/**
	 * Keys to access data in config. Which features of the plugin are enabled
	 * @author Nuubles
	 */
	private static enum KEYS {
		REPAIR,
		ELEVATOR,
		INVENTORY_TWEAKS,
		SETNAME,
		SETSIGN,
		EASYBRIDGE,
		BUILDERS_WAND,
		THROWABLE_PUDDLE,
		CALCULATOR
	}
}