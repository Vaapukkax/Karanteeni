package net.karanteeni.statmanager.level;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import net.karanteeni.statmanager.level.execute.Executor;
import net.karanteeni.statmanager.level.requirement.Requirement;

public class TimeLevel {
	// keys to the config
	public final static String LEVELS = "levels";
	public final static String EXECUTE = "executors";
	public final static String REQUIREMENTS = "requirements";
	public final static String GROUP = "group";
	public final static String COMMANDS = "commands";
	public final static String NEW_RANK = "new-rank";
	public final static String BROADCAST = "broadcast";
	public final static String SOUND = "sound";
	//private static PlayTime plugin = PlayTime.getPlugin(PlayTime.class);
	
	private List<Requirement<?>> requirements = new ArrayList<Requirement<?>>();
	private List<Executor<?>> executors = new ArrayList<Executor<?>>();
	private String key;
	
	/*private long timeRequirement;
	private String key;
	private List<String> requiredPermissions;
	private Group requiredGroup;
	private List<String> commandsToRun;
	private Group newGroup;
	private String broadcastKey;
	private Sound sound;*/
	
	
	public TimeLevel(String key) {
		this.key = key;
	}
	
	
	/**
	 * Returns the name (or key) of this time level which is the highest level in configuration of this level
	 * @return the name (key) of this time level
	 */
	public String getName() {
		return this.key;
	}
	
	
	/**
	 * Adds a new requirement to this time level. The added requirement cannot be removed
	 * @param req requirement to add
	 */
	private void addRequirement(Requirement<?> req) {
		this.requirements.add(req);
	}
	
	
	/**
	 * Adds a new executor to this time level
	 * @param ex executor to add
	 */
	private void addExecutor(Executor<?> ex) {
		this.executors.add(ex);
	}
	
	
	/**
	 * Check if this level has any requirements loaded
	 * @return true if requirements exist, false otherwise
	 */
	private boolean hasRequirements() {
		return this.requirements.size() != 0;
	}
	
	
	/**
	 * Check if this level has any executors loaded
	 * @return true if executors exist, false otherwise
	 */
	private boolean hasExecutors() {
		return this.executors.size() != 0;
	}
	
	
	/**
	 * Loads and returns a time level from the given configuration file
	 * @param config config to load the time level from
	 * @param path path to the time level
	 * @param key key of the time level
	 * @return the loaded time level
	 */
	@SuppressWarnings("unchecked")
	public static TimeLevel loadTimeLevel(ConfigurationSection section, String key) {
		TimeLevel level = new TimeLevel(key);
		
		// check that requirements and executors exist
		if(section.getConfigurationSection(REQUIREMENTS).getKeys(false).isEmpty()) {
			Bukkit.getLogger().log(Level.WARNING, "No requirements set to level " + key + " and thus the level cannot be created!");
			return null;
		} else if(section.getConfigurationSection(EXECUTE).getKeys(false).isEmpty()) {
			Bukkit.getLogger().log(Level.WARNING, "No executors set to level " + key + " and thus the level cannot be created!");
			return null;
		}
		
		// load and set all requirements registered and written in the config
		for(String requirementKey : section.getConfigurationSection(REQUIREMENTS).getKeys(false)) {
			try {
				// check if the requirement we're trying to load event exists
				if(!Requirement.hasRequirement(requirementKey)) {
					Bukkit.getLogger().log(Level.WARNING, "Could not create the given requirement '" + requirementKey 
							+ "' as it does not exist or is not registered by plugins.");
					continue;
				}
				
				// get the class which will be used in creating a new object
				Class<? extends Requirement<?>> reqClass = (Class<? extends Requirement<?>>) Requirement.getRequirement(requirementKey);
				// get the constructor
				Constructor<? extends Requirement<?>> ctor = reqClass.getConstructor(ConfigurationSection.class, String.class, String.class);
				
				// create a new instance of the given class
				Requirement<?> req = ctor.newInstance(section, REQUIREMENTS, requirementKey);
				level.addRequirement(req);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // ---Requirement load end
		
		
		// load and set all executors registered and written in the config
		for(String executorKey : section.getConfigurationSection(EXECUTE).getKeys(false)) {
			try {
				// check if the requirement we're trying to load event exists
				if(!Executor.hasExecutor(executorKey)) {
					Bukkit.getLogger().log(Level.WARNING, "Could not create the given executor '" + executorKey 
							+ "' as it does not exist or is not registered by plugins.");
					continue;
				}
				
				// get the class which will be used in creating a new object
				Class<? extends Executor<?>> exClass = (Class<? extends Executor<?>>) Executor.getExecutor(executorKey);
				// get the constructor
				Constructor<? extends Executor<?>> ctor = exClass.getConstructor(ConfigurationSection.class, String.class, String.class);
				
				// create a new instance of the given class
				Executor<?> ex = ctor.newInstance(section, EXECUTE, executorKey);
				level.addExecutor(ex);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // ---Executor load end
		
		
		// return the loaded TimeLevel
		if(level.hasExecutors() && level.hasRequirements())
			return level;
		Bukkit.getLogger().log(Level.WARNING, "Could not load time level " + key + " as it had no valid requirements OR executors");
		return null;
	}
	
	
	/**
	 * Tries to level up the given player
	 * @param player player to attempt the level up
	 * @return true if level up was a success and all requirements met and
	 * all executors ran. False if level up was a failure as the requirements were not met
	 */
	public boolean tryLevelUp(Player player) {
		// check if player fills all of the requirements for this level
		for(Requirement<?> requirement : requirements)
		if(!requirement.isRequirementMet(player)) return false;
		
		// execute the level up executions
		for(Executor<?> executor : executors)
			executor.execute(player);
		return true;
	}
	
	/**
	 * Executes the level up process for the given player
	 * @param player player to try to level up
	 * @return was the player able to proceed to this level
	 */
	/*public boolean executeLevelUp(Player player) {
		// check if the levels requirements are met
		if(!this.areRequirementsMet(player.getUniqueId())) return false;
		
	}
	
	
	/**
	 * Sets the required permissions to this time level
	 * @param permissions permissions required
	 */
	/*public void setRequiredPermissions(List<String> permissions) {
		this.requiredPermissions = permissions;
	}*/
	
	
	/**
	 * Adds the given permission to the required permissions
	 * @param permission permission required
	 */
	/*public void addRequiredPermission(String permission) {
		if(this.requiredPermissions == null) this.requiredPermissions = new ArrayList<String>();
		this.requiredPermissions.add(permission);
	}*/
	
	
	/**
	 * Sets the group required for this level
	 * @param group group required for this level
	 */
	/*public void setRequiredGroup(Group group) {
		this.requiredGroup = group;
	}*/
	
	
	/**
	 * Sets the message broadcasted when this level is reached
	 * @param broadcast message broadcasted on level reach
	 */
	/*public void setBroadcastKey(String broadcastKey) {
		this.broadcastKey = broadcastKey;
	}*/
	
	
	/**
	 * Sets the sound played to the player who reaches this level
	 * @param sound sound played on level reached
	 */
	/*public void setSound(Sound sound) {
		this.sound = sound;
	}*/
	
	
	/**
	 * Converts the given strings to a complete configuration path
	 * @param strings strings to combine into a path
	 * @return the combined path
	 */
	/*public String getPath(String... strings) {
		String results = "";
		
		for(int i = 0; i < strings.length; ++i) {
			if(i == 0)
				results += strings[i];
			else
				results += strings[i] + ".";
		}
		
		return results;
	}*/
	
	
	/**
	 * Check if all of the given requirements are being met
	 * @param uuid uuid of the player whose state is being checked
	 * @return true if requirements are met, false otherwise
	 */
	/*public boolean areRequirementsMet(UUID uuid) {
		return (isTimeRequirementMet(uuid)
				&& isGroupMet(uuid)
				&& arePermissionsMet(uuid));
	}*/
	
	
	/**
	 * Check if the given players global play time meets the requirement for this time level
	 * @param uuid uuid of the player
	 * @return true if requirement is met, false otherwise
	 */
	/*private boolean isTimeRequirementMet(UUID uuid) {
		Time time = plugin.getManager().forceLoadTime(uuid);
		return time.getTime() >= this.timeRequirement;
	}*/
	
	
	/**
	 * Checks if the group requirement is met
	 * @param uuid uuid of the player
	 * @return true if the group is correct or the required group is null, false otherwise
	 */
	/*private boolean isGroupMet(UUID uuid) {
		if(requiredGroup == null) return true;
		return perms.getPlayerModel().getLocalGroup(uuid).equals(requiredGroup);
	}*/
	
	
	/**
	 * Checks if all of the required permissions are met for the time level
	 * @param uuid uuid of the player being checked
	 * @return true if permissions are empty or all of them are met. false if missing a required permission
	 */
	/*private boolean arePermissionsMet(UUID uuid) {
		if(this.requiredPermissions == null || this.requiredPermissions.isEmpty()) return true;
		PermissionPlayer pp = perms.getPlayerModel().getPermissionPlayer(uuid);
		for(String permission : this.requiredPermissions)
		if(!pp.hasPermission(permission, PermissionPlayer.DATA_TYPE.GROUP_AND_PLAYER))
			return false;
		return true;
	}*/
}



























