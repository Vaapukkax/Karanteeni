package net.karanteeni.core.players;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.KaranteeniPlugin;

public class KPlayer {
	//Player held by this KPlayer class
	private final Player player;
	
	/* List of all players */
	private static final Map<UUID, KPlayer> players = new HashMap<UUID, KPlayer>();
	
	//List which contains any kind of data of players. Boolean tells if this data is to be removed with delay
	private HashMap<NamespacedKey, Entry<Object,Boolean>> playerData = new HashMap<NamespacedKey, Entry<Object,Boolean>>();
	
	//Cache of playerdata to hold playerdata when player leaves but may come back
	private static HashMap<UUID, HashMap<NamespacedKey, Entry<Object,Boolean>>> cache = 
			new HashMap<UUID, HashMap<NamespacedKey, Entry<Object,Boolean>>>();
	
	/**
	 * Inserts a new KPlayer to list and loads possible cache data which
	 * may still remain if player is rejoining
	 * @param player Player of this KPlayer
	 */
	protected KPlayer(Player player)
	{
		this.player = player;		
		players.put(player.getUniqueId(), this); //Put player to list
		//Load player cache
		if(cache.containsKey(player.getUniqueId()))
		{
			//Load and remove cache from memory
			HashMap<NamespacedKey, Entry<Object, Boolean>> data = clearCache(player.getUniqueId());
			if(data != null) //Set data only if it's not null
				playerData = data;
		}
	}
	
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
	 * Checks if KPlayer has still cached data in memory
	 * @param uuid uuid of cached data
	 * @return true if there is data in cache, false otherwise
	 */
	protected static boolean hasCachedData(UUID uuid)
	{ return cache.containsKey(uuid); }
	
	/**
	 * Destroys this instance of KPlayer and
	 * saves the cache of this player
	 */
	public void destroy()
	{
		HashMap<NamespacedKey, Entry<Object, Boolean>> data = players.remove(player).playerData;
		HashMap<NamespacedKey, Entry<Object, Boolean>> storable = new HashMap<NamespacedKey, Entry<Object, Boolean>>();
		
		//Store only cacheable data to cache
		if(data == null) return;
		
		for(Entry<NamespacedKey, Entry<Object,Boolean>> entry : data.entrySet())
			if(entry.getValue().getValue()) //True if cacheable
				storable.put(entry.getKey(), entry.getValue());
		
		if(!storable.isEmpty())
			cache.put(player.getUniqueId(), storable);
	}
	
	/**
	 * Clears the cache held by KPlayer but may not clear all cache data in
	 * other places
	 * @param uuid UUID of player to remove the cache
	 * @return cached data
	 */
	public static HashMap<NamespacedKey, Entry<Object,Boolean>> clearCache(UUID uuid)
	{
		return cache.remove(uuid);
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
		return playerData.put(key, new SimpleEntry<Object,Boolean>(data,false));
	}
	
	/**
	 * Sets a specific data to player
	 * @param plugin which plugin does this data belong to
	 * @param key the key of the data
	 * @param data the data to be stores
	 * @return previous data if exists
	 */
	public Object setData(final KaranteeniPlugin plugin, final String key, final Object data)
	{
		return playerData.put(new NamespacedKey(plugin, key), new SimpleEntry<Object,Boolean>(data,false));
	}
	
	/**
	 * Sets a specific data for player
	 * @param key data identifier
	 * @param data data to store
	 * @return previous data if exists
	 */
	public Object setCacheData(final NamespacedKey key, final Object data)
	{
		return playerData.put(key, new SimpleEntry<Object,Boolean>(data,true));
	}
	
	/**
	 * Sets a specific data to player
	 * @param plugin which plugin does this data belong to
	 * @param key the key of the data
	 * @param data the data to be stores
	 * @return previous data if exists
	 */
	public Object setCacheData(final KaranteeniPlugin plugin, final String key, final Object data)
	{
		return playerData.put(new NamespacedKey(plugin, key), new SimpleEntry<Object,Boolean>(data,true));
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
		Entry<Object,Boolean> entry = playerData.remove(new NamespacedKey(plugin, key));
		if(entry != null)
			return entry.getKey();
		return null;
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
		Entry<Object,Boolean> entry = playerData.get(key);
		
		if(entry == null)
			return null;
		return entry.getKey();
	}
	
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	public List<?> getList(final NamespacedKey key)
	{
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		if(entry.getKey() instanceof List<?>)
			return (List<?>)entry.getKey();
		return null;
	}
	
	/**
	 * Is the boolean data stored to player true or false
	 * @param key data to find
	 * @return boolean data value or false if not found
	 */
	public boolean getBoolean(final NamespacedKey key)
	{
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return false;
		
		Object data = entry.getKey();
		
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
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(key);
		if(entry == null)
			return null;
		
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		
		return entry.getKey();
	}
	
	/**
	 * Is the boolean data stored to player true or false
	 * @param key data to find
	 * @return boolean data value or false if not found
	 */
	public boolean getBoolean(final Plugin plugin, final String key)
	{
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return false;
		Object data = entry.getKey();
		
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
		
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
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
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
		if(data instanceof Location)
			return (Location)data;
		return null;
	}
}
