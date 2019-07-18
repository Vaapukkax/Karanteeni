package net.karanteeni.statmanager.level.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.config.YamlConfig;

public abstract class Executor<T> {
	private String path;
	private ConfigurationSection config;
	protected static final String PLAYER_NAME = "%name%";
	protected static final String PLAYER_DISPLAY_NAME = "%displayname%";
	protected static final String PLAYER_UUID = "%uuid%";
	
	@SuppressWarnings("rawtypes")
	private final static HashMap<String, Class<? extends Executor>> executors = 
			new HashMap<String, Class<? extends Executor>>();
	
	
	/**
	 * 
	 * @param path Path to the key
	 * @param key requirements key
	 */
	public Executor(ConfigurationSection config, String path, String key) {
		this.path = path + "." + key;
		this.config = config;
	}
	
	
	/**
	 * Called when the player reaches a given level
	 * @param uuid uuid of the executor
	 */
	public abstract void execute(Player player);
	
	
	protected static final void register(String key, Class<? extends Executor<?>> clazz) {
		executors.put(key, clazz);
	}
	
	
	/**
	 * Returns all of the requirements ever created
	 * @return requirements created
	 */
	@SuppressWarnings("rawtypes")
	public static final Class<? extends Executor> getExecutor(String key) {
		return executors.get(key);
	}
	
	
	/**
	 * Check if there is a requirement class created
	 * @param key key to the requirement class
	 * @return true if the requirement class exists, false otherwise
	 */
	public static final boolean hasExecutor(String key) {
		return executors.containsKey(key);
	}
	
	
	/**
	 * Returns a string from config
	 * @return string from config
	 */
	protected String getString() {
		return config.getString(path);
	}
	
	
	/**
	 * Returns an int from config
	 * @return int from config
	 */
	protected int getInteger() {
		return config.getInt(path);
	}
	
	
	protected long getLong() {
		return config.getLong(path);
	}
	
	
	protected Object get() {
		return config.get(path);
	}
	
	
	@SuppressWarnings("unchecked")
	protected T getObject() throws ClassCastException {
		Object o = config.get(path);
		if(o == null) return null;
		return (T)o;
	}
	
	
	protected List<String> getStringList() {
		return config.getStringList(path);
	}
	
	
	protected List<Integer> getIntegerList() {
		return config.getIntegerList(path);
	}
	
	
	protected Map<String, Object> getValues() {
		return this.config.getValues(false);
	}
	
	
	protected List<Short> getShortList() {
		return this.config.getShortList(path);
	}
	
	
	protected List<Map<?, ?>> getMapList() {
		return config.getMapList(path);
	}
	
	
	protected boolean getBoolean() {
		return this.config.getBoolean(path);
	}
	
	
	protected List<Boolean> getBooleanList() {
		return config.getBooleanList(path);
	}
	
	
	protected List<Byte> getByteList() {
		return config.getByteList(path);
	}
	
	
	protected List<Character> getCharacterList() {
		return config.getCharacterList(path);
	}
	
	
	protected Double getDouble() {
		return this.config.getDouble(path);
	}
	
	
	protected List<Double> getDoubleList() {
		return this.config.getDoubleList(path);
	}
	
	
	protected List<Float> getFloatList() {
		return this.config.getFloatList(path);
	}
	
	
	protected ItemStack getItemStack() {
		return this.config.getItemStack(path);
	}
	
	
	protected List<Long> getLongList() {
		return this.config.getLongList(path);
	}
	
	
	protected List<?> getList() {
		return this.config.getList(path);
	}
	
	/**
	 * Checks if this executor has been set to the configuration file
	 * @return true if this executor has been set to the config and false if not 
	 */
	public static final boolean isConfigurationSet(YamlConfig config, String path, String key) {
		return config.getConfig().isSet(path + "." + key);
	}
}
