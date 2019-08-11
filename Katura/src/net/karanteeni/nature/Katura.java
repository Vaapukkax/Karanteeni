package net.karanteeni.nature;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.TimeComponent;
import net.karanteeni.nature.block.commands.SpawnCommand;
import net.karanteeni.nature.block.events.AutoGrass;
import net.karanteeni.nature.block.events.BlowDandelion;
import net.karanteeni.nature.block.events.ChopTree;
import net.karanteeni.nature.block.events.SpawnerSpawn;
import net.karanteeni.nature.block.events.TreeGrow;
import net.karanteeni.nature.commands.WorldCommand;
import net.karanteeni.nature.commands.time.TickParser;
import net.karanteeni.nature.commands.time.Time;
import net.karanteeni.nature.commands.time.TimeAddComponent;
import net.karanteeni.nature.commands.time.TimeRemoveComponent;
import net.karanteeni.nature.commands.time.TimeSetComponent;
import net.karanteeni.nature.commands.weather.Weather;
import net.karanteeni.nature.entity.events.EntityGrief;
import net.karanteeni.nature.entity.events.EntitySpawn;
import net.karanteeni.nature.entity.events.ExplosionEffects;
import net.karanteeni.nature.entity.events.FeedSheep;
import net.karanteeni.nature.entity.events.HungerPreventer;
import net.karanteeni.nature.entity.events.OnLightning;
import net.karanteeni.nature.entity.events.SpreadCreeperExplosion;
import net.karanteeni.nature.worldguard.WorldGuardManager;

public class Katura extends KaranteeniPlugin {
	private static String KEY_PREFIX = "Plugin-functionality.";
	private WorldGuardManager wgm;
	
	public Katura() {
		super(true);
	}
	
	
	@Override
	public void onLoad() {
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
		registerConfig();
		registerEvents();
		registerCommands();
		
		if(wgm != null && this.getServer().getPluginManager().getPlugin("WorldGuard") != null && 
				this.getServer().getPluginManager().getPlugin("WorldGuard").isEnabled()) {
			wgm.register();	
		} else {
			wgm = null;
		}
	}
	
	
	/**
	 * Returns the world guard manager used by the plugin
	 * @return world guard manager of this plugin
	 */
	public WorldGuardManager getWorldGuardManager() {
		return wgm;
	}
	
	
	/**
	 * Registers the nonexistent config keys
	 */
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
	
	
	/**
	 * Registers events to be used in this plugin
	 */
	private void registerEvents() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.SHEEP_FEED.toString()))
			getServer().getPluginManager().registerEvents(new FeedSheep(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.DANDELION_BLOW.toString()))
			getServer().getPluginManager().registerEvents(new BlowDandelion(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.AUTO_GRASS.toString()))
			getServer().getPluginManager().registerEvents(new AutoGrass(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.TREE_FELLER.toString()))
			getServer().getPluginManager().registerEvents(new ChopTree(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.TREE_GROWTH.toString()))
			getServer().getPluginManager().registerEvents(new TreeGrow(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.LIGHTNING_TRANSFORM.toString()))
			getServer().getPluginManager().registerEvents(new OnLightning(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.CREEPER_CHAINING.toString()))
			getServer().getPluginManager().registerEvents(new SpreadCreeperExplosion(), this);
		
		//Long config lists, create last
		getServer().getPluginManager().registerEvents(new EntityGrief(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.EXPLOSION_EFFECTS.toString()))
			getServer().getPluginManager().registerEvents(new ExplosionEffects(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.ENTITY_SPAWN.toString()))
			getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.MONSTER_SPAWN.toString()))
			getServer().getPluginManager().registerEvents(new SpawnerSpawn(), this);
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.HUNGER_MODIFICATION.toString()))
			getServer().getPluginManager().registerEvents(new HungerPreventer(), this);
	}
	
	
	/**
	 * Registers commands to be used in this plugin
	 */
	private void registerCommands() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.WORLD.toString())) {
			(new WorldCommand()).register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.TIME.toString())) {
			// create time chainer
			Time time = new Time();
			// create tick parser loader, load before attached component
			TickParser tp = new TickParser(true);
			// create components
			TimeSetComponent tsc = new TimeSetComponent(tp);
			tsc.setPermission("katura.time.set");
			TimeAddComponent tac = new TimeAddComponent(tp);
			tac.setPermission("katura.time.add");
			TimeRemoveComponent trc = new TimeRemoveComponent(tp);
			trc.setPermission("katura.time.remove");
			// add components to command
			time.addComponent("set", tsc);
			time.addComponent("add", tac);
			time.addComponent("remove", trc);
			time.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.WEATHER.toString())) {
			Weather weather = new Weather();
			weather.setLoader(new TimeComponent(true, false));
			weather.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.SET_SPAWNER.toString())) {
			SpawnCommand ss = new SpawnCommand();
			ss.register();
		}
	}

	@Override
	public void onDisable() {

	}
	
	/**
	 * Keys to access data in config. Which features of the plugin are enabled
	 * @author Nuubles
	 */
	private static enum KEYS {
		WORLD,
		TIME,
		WEATHER,
		HUNGER,
		SHEEP_FEED,
		DANDELION_BLOW,
		AUTO_GRASS,
		TREE_FELLER,
		TREE_GROWTH,
		LIGHTNING_TRANSFORM,
		CREEPER_CHAINING,
		EXPLOSION_EFFECTS,
		ENTITY_SPAWN,
		MONSTER_SPAWN,
		HUNGER_MODIFICATION,
		SET_SPAWNER
	}
}

