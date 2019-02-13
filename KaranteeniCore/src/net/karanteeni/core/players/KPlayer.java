package net.karanteeni.core.players;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.KaranteeniPlugin;

public class KPlayer {
	
	private final Player player;
	
	public KPlayer(Player player)
	{
		this.player = player;
		players.put(player.getUniqueId(), this);
	}
	
	/* List of all players */
	private static final Map<UUID, KPlayer> players = new HashMap<UUID, KPlayer>();
	
	//List which contains any kind of data of players
	private Map<NamespacedKey, Object> playerData = new HashMap<NamespacedKey, Object>();
	
	/**
	 * Returns the KPlayer entity
	 * @param player
	 * @returnQ
	 */
	public static KPlayer getKPlayer(Player player)
	{
		return players.get(player.getUniqueId());
	}
	
	/**
	 * Destroys this instance of KPlayer
	 */
	public void destroy()
	{
		players.remove(player);
	}
	
	/**
	 * Fades in a red screen effect to player
	 * @param percentage
	 */
	public void fadeInScreenTint(int percentage, long time)
	{
		(new RedScreenTint()).fadeInBorder(player, percentage, time);
	}
	
	/**
	 * Fades out a red screen effect to player
	 * @param percentage
	 */
	public void fadeOutScreenTint(int percentage, long time)
	{
		(new RedScreenTint()).fadeOutBorder(player, percentage, time);
	}
	
	/**
	 * Sets a red screen effect to player
	 * @param percentage
	 */
	public void setScreenTint(int percentage)
	{
		(new RedScreenTint()).setBorder(player, percentage);
	}
	
	/**
	 * Sets a specific data for player
	 * @param key data identifier
	 * @param data data to store
	 * @return previous data if exists
	 */
	public Object setData(final NamespacedKey key, final Object data)
	{
		return playerData.put(key, data);
	}
	
	/**
	 * Sets a specific data to player
	 * @param plugin which plugin does this data belong to
	 * @param key the key of the data
	 * @param data the data to be stores
	 * @return
	 */
	public Object setData(final KaranteeniPlugin plugin, final String key, final Object data)
	{
		return playerData.put(new NamespacedKey(plugin, key), data);
	}
	
	/**
	 * Tests if player has data assosiated for this key
	 * @param key
	 * @return
	 */
	public boolean dataExists(final NamespacedKey key)
	{
		return playerData.containsKey(key);
	}
	
	/**
	 * Tests if player has data assosiated for this key
	 * @param key
	 * @return
	 */
	public boolean dataExists(final Plugin plugin, final String key)
	{
		return playerData.containsKey(new NamespacedKey(plugin, key));
	}
	
	/**
	 * Deletes a data associated to key and returns it
	 * @param key
	 * @return data deleted
	 */
	public Object removeData(final Plugin plugin, final String key)
	{
		return playerData.remove(new NamespacedKey(plugin, key));
	}
	
	/**
	 * Deletes a data associated to key and returns it
	 * @param key
	 * @return data deleted
	 */
	public Object removeData(final NamespacedKey key)
	{
		return playerData.remove(key);
	}
	
	/**
	 * Returns the player associated with this class
	 * @return
	 */
	public Player getPlayer()
	{
		return player;
	}
		
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	public Object getData(final NamespacedKey key)
	{
		return playerData.get(key);
	}
	
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	public List<?> getList(final NamespacedKey key)
	{
		Object data = playerData.get(key);
			if(data instanceof List<?>)
				return (List<?>)data;
			return null;
	}
	
	/**
	 * Is the boolean data stored to player true or false
	 * @param key data to find
	 * @return boolean data value or false if not found
	 */
	public boolean getBoolean(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		
		if(data instanceof Boolean)
			return (Boolean)data;
		return false;
	}
	
	/**
	 * Drops the given items at the location of the player
	 * @param items
	 */
	public void dropItemsAtPlayer(List<ItemStack> items)
	{
		Location l = player.getLocation();
		for(ItemStack item : items)
			l.getWorld().dropItemNaturally(l, item);
	}
	
	/**
	 * Returns a string data from player
	 * @param key
	 * @return
	 */
	public String getString(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		if(data != null)
			return data.toString();
		return null;
	}
	
	/**
	 * Returns a float data from player
	 * @param key
	 * @return
	 */
	public Float getFloat(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		if(data instanceof Float)
			return (Float)data;
		return null;
	}
	
	/**
	 * Returns a double data from player
	 * @param key
	 * @return
	 */
	public Double getDouble(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		if(data instanceof Double)
			return (Double)data;
		return null;
	}
	
	/**
	 * Returns an integer data from player
	 * @param key
	 * @return
	 */
	public Integer getInt(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		if(data instanceof Integer)
			return (Integer)data;
		return null;
	}
	
	/**
	 * Returns a character data from player
	 * @param key
	 * @return
	 */
	public Character getChar(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		if(data instanceof Character)
			return (Character)data;
		return null;
	}
	
	/**
	 * Returns a location data from player
	 * @param key
	 * @return
	 */
	public Location getLocationData(final NamespacedKey key)
	{
		Object data = playerData.get(key);
		if(data instanceof Location)
			return (Location)data;
		return null;
	}
	
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	public Object getData(final Plugin plugin, final String key)
	{
		return playerData.get(new NamespacedKey(plugin, key));
	}
	
	/**
	 * Is the boolean data stored to player true or false
	 * @param key data to find
	 * @return boolean data value or false if not found
	 */
	public boolean getBoolean(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		
		if(data instanceof Boolean)
			return (Boolean)data;
		return false;
	}
	
	/**
	 * Returns a string data from player
	 * @param key
	 * @return
	 */
	public String getString(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		if(data != null)
			return data.toString();
		return null;
	}
	
	/**
	 * Returns a float data from player
	 * @param key
	 * @return
	 */
	public Float getFloat(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		if(data instanceof Float)
			return (Float)data;
		return null;
	}
	
	/**
	 * Returns a double data from player
	 * @param key
	 * @return
	 */
	public Double getDouble(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		if(data instanceof Double)
			return (Double)data;
		return null;
	}
	
	/**
	 * Returns an integer data from player
	 * @param key
	 * @return
	 */
	public Integer getInt(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		if(data instanceof Integer)
			return (Integer)data;
		return null;
	}
	
	/**
	 * Returns a character data from player
	 * @param key
	 * @return
	 */
	public Character getChar(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		if(data instanceof Character)
			return (Character)data;
		return null;
	}
	
	/**
	 * Returns a location data from player
	 * @param key
	 * @return
	 */
	public Location getLocationData(final Plugin plugin, final String key)
	{
		Object data = playerData.get(new NamespacedKey(plugin, key));
		if(data instanceof Location)
			return (Location)data;
		return null;
	}
}
