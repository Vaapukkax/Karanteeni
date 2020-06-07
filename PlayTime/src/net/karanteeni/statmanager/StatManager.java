package net.karanteeni.statmanager;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.command.defaultcomponent.TimeLoader;
import net.karanteeni.core.config.YamlConfig;
import net.karanteeni.statmanager.commands.AddComponent;
import net.karanteeni.statmanager.commands.PlayTimeCommand;
import net.karanteeni.statmanager.commands.RemoveComponent;
import net.karanteeni.statmanager.commands.SetComponent;
import net.karanteeni.statmanager.event.PlayerJoin;
import net.karanteeni.statmanager.event.PlayerQuit;
import net.karanteeni.statmanager.event.WorldSave;
import net.karanteeni.statmanager.level.TimeLevel;
import net.karanteeni.statmanager.level.execute.BroadcastExecutor;
import net.karanteeni.statmanager.level.execute.LocalGroupSetExecutor;
import net.karanteeni.statmanager.level.requirement.LocalGroupRequirement;
import net.karanteeni.statmanager.level.requirement.PermissionRequirement;
import net.karanteeni.statmanager.level.requirement.TimeRequirement;

public class StatManager extends KaranteeniPlugin {
	private Manager manager;
	private YamlConfig levels;
	
	public StatManager() {
		super(true);
	}
	
	
	@Override
	public void onLoad() {
		// create a new level manager
		this.manager = new Manager();
		
		// initialize database
		if(!Time.initialize()) {
			Bukkit.getLogger().log(Level.SEVERE, StatManager.getDefaultMsgs().defaultDatabaseError());
		}
		
		// register requirements when the plugin loads before using them
		registerRequirements();
		registerExecutors();
	}
	
	
	@Override
	public void onEnable() {
		registerConfig();
		registerEvents();
		registerCommands();
		loadTimeLevels();
	}
	
	
	@Override
	public void onDisable() {
		// save everyones play time when the plugin shuts down
		manager.save();
	}
	
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
		getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
		getServer().getPluginManager().registerEvents(new WorldSave(this), this);
	}
	
	
	/**
	 * Registers requirements to the requirements
	 */
	private void registerRequirements() {
		PermissionRequirement.register("permission");
		TimeRequirement.register("global-time");
		
		if(getServer().getPluginManager().getPlugin("KaranteeniPerms") != null) {
			// register permission plugin requirements if the permission plugin is enabled
			LocalGroupRequirement.register("local-group");
			//GlobalGroupRequirement.register("global-group");
		}
	}
	
	
	/**
	 * Registers executors to the Executor
	 */
	private void registerExecutors() {
		BroadcastExecutor.register("broadcast-key");
		if(getServer().getPluginManager().getPlugin("KaranteeniPerms") != null) {
			// register permission plugin executors if the permission plugin is enabled
			LocalGroupSetExecutor.register("local-group");
		} else {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
		}
	}
	
	
	/**
	 * Registers the config the levels are in
	 */
	private void registerConfig() {
		this.levels = new YamlConfig(this, "levels.yml");
	}
	
	
	/**
	 * Loads the time levels to memory
	 */
	private void loadTimeLevels() {
		// formattable path to be used as a path to the requirements section
		String warningMessage = "Below the 'levels' section you may add any names for the levels you want to use.\n"
				+ "The resulting output may look something like the following:\n"
				+ "levels:\n"
				+ "  firstLevel:\n"
				+ "    requirements:\n"
				+ "      rank: defaultRank\n"
				+ "      time: 3600000\n"
				+ "    execute:\n"
				+ "      commands:\n"
				+ "        - broadcast Congratz for the new level %name% %uuid% %displayname%\n"
				+ "        - broadcast Remember to vote today\n"
				+ "      broadcast: Broadcasts can also be in their own tags %name%!\n"
				+ "      new-rank: defaultRank2\n"
				+ "      sound: ENTITY_PLAYER_DEATH";
		
		// check if any levels have been created
		if (!levels.getConfig().isConfigurationSection(TimeLevel.LEVELS)){
			Bukkit.getLogger().log(Level.WARNING, "No limits have been set to the config; please add configuration section 'limits'");
			Bukkit.getLogger().log(Level.WARNING, warningMessage);
			return; 
		} else if(levels.getConfig().getConfigurationSection(TimeLevel.LEVELS).getKeys(false).isEmpty()) {
			Bukkit.getLogger().log(Level.WARNING, "No limits have been set to the config; please add configuration section 'limits'");
			Bukkit.getLogger().log(Level.WARNING, warningMessage);
			return;
		}
		
		ConfigurationSection levelSection = levels.getConfig().getConfigurationSection(TimeLevel.LEVELS);
		
		// loop all of the random keys in the level section
		for(String levelKey : levelSection.getKeys(false)) {
			// load this level from the config
			TimeLevel level = 
					TimeLevel.loadTimeLevel(levels.getConfig().getConfigurationSection(TimeLevel.LEVELS + "." + levelKey), levelKey);
			
			// add the level to the manager
			manager.addTimeLevel(level);
		}
	}
	
	
	private void registerCommands() {
		// register the /playtime command
		PlayTimeCommand ptc = new PlayTimeCommand();
		PlayerLoader pl = new PlayerLoader(true, false, true, true, true);
		ptc.setLoader(pl);
		
		TimeLoader timeComponent = new TimeLoader(true, true);
		
		SetComponent setComponent = new SetComponent();
		setComponent.setPermission("statmanager.set");
		pl.addComponent("set", setComponent);
		setComponent.setLoader(timeComponent);
		
		RemoveComponent subtractComponent = new RemoveComponent();
		subtractComponent.setPermission("statmanager.remove");
		pl.addComponent("remove", subtractComponent);
		subtractComponent.setLoader(timeComponent);
		
		AddComponent addComponent = new AddComponent();
		addComponent.setPermission("statmanager.add");
		pl.addComponent("add", addComponent);
		addComponent.setLoader(timeComponent);
		
		ptc.register();
		
	}
	
	
	/**
	 * Returns the time manager
	 * @return manager which is responsible for managing players play times
	 */
	public Manager getManager() {
		return this.manager;
	}
}