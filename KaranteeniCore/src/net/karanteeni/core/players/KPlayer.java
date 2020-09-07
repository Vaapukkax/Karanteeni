package net.karanteeni.core.players;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;

public class KPlayer implements Listener {
	//Player held by this KPlayer class
	private Player player;
	private final UUID uuid;
	private boolean invincible = false;
	
	/* List of all players */
	private static final Map<UUID, KPlayer> players = new HashMap<UUID, KPlayer>();
	
	//List which contains any kind of data of players. Boolean tells if this data is to be removed with delay
	private HashMap<NamespacedKey, Entry<Object,Boolean>> playerData = new HashMap<NamespacedKey, Entry<Object,Boolean>>();
	
	//Cache of playerdata to hold playerdata when player leaves but may come back
	private static HashMap<UUID, HashMap<NamespacedKey, Entry<Object,Boolean>>> cache = 
			new HashMap<UUID, HashMap<NamespacedKey, Entry<Object,Boolean>>>();
	
	// run these one the player has joined and late joined
	private PriorityQueue<net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>>> runOncePlayerJoined = 
			new PriorityQueue<net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>>>();
	private PriorityQueue<net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>>> runOncePlayerLateJoined = 
			new PriorityQueue<net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>>>();
	
	/**
	 * Inserts a new KPlayer to list and loads possible cache data which
	 * may still remain if player is rejoining
	 * @param player Player of this KPlayer
	 */
	protected KPlayer(UUID uuid) {
		//this.player = player;
		this.uuid = uuid;
		players.put(uuid, this); //Put player to list
		//Load player cache
		if(cache.containsKey(uuid)) {
			//Load and remove cache from memory
			HashMap<NamespacedKey, Entry<Object, Boolean>> data = clearCache(uuid);
			if(data != null) //Set data only if it's not null
				playerData = data;
		}
	}
	
	
	/**
	 * Add a new consumer to run once player has logged onto the server
	 * @param priority priority of consumer
	 * @param consumer consumer to run once player joins
	 * @throws Exception modifier added after player has already joined
	 */
	public void addPlayerJoinModifier(EventPriority priority, Consumer<KPlayer> consumer) throws IllegalArgumentException {
		if(isValid()) throw new IllegalArgumentException("Player is already online");
		runOncePlayerJoined.add(new net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>>(priority,consumer));
	}
	
	
	/**
	 * Add a new consumer to run once player has logged onto the server
	 * @param priority priority of consumer
	 * @param consumer consumer to run once player joins
	 * @throws Exception modifier added after player has already joined
	 */
	public void addPlayerLateJoinModifier(EventPriority priority, Consumer<KPlayer> consumer) throws IllegalArgumentException {
		if(isValid()) throw new IllegalArgumentException("Player is already online");
		runOncePlayerJoined.add(new net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>>(priority,consumer));
	}
	
	
	/**
	 * Runs the player join modifiers
	 */
	protected void runPlayerJoinModifiers() {
		for(net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>> entry = runOncePlayerJoined.poll(); 
				entry != null; 
				entry = runOncePlayerJoined.poll()) {
			entry.getValue().accept(this);
		}
		
		runOncePlayerJoined.clear();
	}
	
	
	/**
	 * Runs the player join modifiers with a small delay
	 */
	protected void runPlayerLateJoinModifiers() {
		KPlayer self = this;
		// run the late join modifiers 3 ticks after join
		Bukkit.getScheduler().runTaskLater(KaranteeniCore.getPlugin(KaranteeniCore.class), new Runnable() {			
				@Override
				public void run() {
					for(net.karanteeni.core.data.Entry<EventPriority, Consumer<KPlayer>> entry = runOncePlayerLateJoined.poll(); 
							entry != null; 
							entry = runOncePlayerJoined.poll()) {
						entry.getValue().accept(self);
					}
					
					runOncePlayerLateJoined.clear();
				}			
		}, 3);
	}
	
	
	/**
	 * Returns the KPlayer entity
	 * @param player
	 * @returnQ
	 */
	public static KPlayer getKPlayer(Player player) {
		return players.get(player.getUniqueId());
	}
	
	
	/**
	 * Returns the KPlayer entity
	 * @param player
	 * @returnQ
	 */
	public static KPlayer getKPlayer(UUID uuid) {
		return players.get(uuid);
	}
	
	
	/**
	 * Sets the player to this KPlayer class
	 * @param player player to set
	 */
	protected void setPlayer(Player player) throws IllegalArgumentException {
		if(!player.getUniqueId().equals(uuid))
			throw new IllegalArgumentException("UUID doesn't match with given players UUID");
		this.player = player;
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
	public void destroy() {
		HashMap<NamespacedKey, Entry<Object, Boolean>> data = players.remove(uuid).playerData;
		HashMap<NamespacedKey, Entry<Object, Boolean>> storable = new HashMap<NamespacedKey, Entry<Object, Boolean>>();
		
		//Store only cacheable data to cache
		if(data == null) return;
		
		for(Entry<NamespacedKey, Entry<Object,Boolean>> entry : data.entrySet())
			if(entry.getValue().getValue()) //True if cacheable
				storable.put(entry.getKey(), entry.getValue());
		
		if(!storable.isEmpty())
			cache.put(uuid, storable);
	}
	
	
	/**
	 * Clears the cache held by KPlayer but may not clear all cache data in
	 * other places
	 * @param uuid UUID of player to remove the cache
	 * @return cached data
	 */
	public static HashMap<NamespacedKey, Entry<Object,Boolean>> clearCache(UUID uuid) {
		return cache.remove(uuid);
	}
	
	
	/**
	 * Clears the cache held by KPlayer but may not clear all cache data in
	 * other places
	 * @param uuid UUID of player to remove the cache
	 * @return cached data
	 */
	public HashMap<NamespacedKey, Entry<Object,Boolean>> clearCache() {
		return cache.remove(uuid);
	}
	
	
	/**
	 * Fades in a red screen effect to player
	 * @param percentage
	 */
	public void fadeInScreenTint(int percentage, long time) {
		(new RedScreenTint()).fadeInBorder(player, percentage, time);
	}
	
	
	/**
	 * Fades out a red screen effect to player
	 * @param percentage
	 */
	public void fadeOutScreenTint(int percentage, long time) {
		(new RedScreenTint()).fadeOutBorder(player, percentage, time);
	}
	
	
	/**
	 * Sets a red screen effect to player
	 * @param percentage
	 */
	public void setScreenTint(int percentage) {
		(new RedScreenTint()).setBorder(player, percentage);
	}

	
	/**
    * Removes the items of type from an inventory.
    * @param inventory Inventory to modify
    * @param type The type of Material to remove
    * @param amount The amount to remove, or {@link Integer.MAX_VALUE} to remove all
    * @return The amount of items that could not be removed, 0 for success, or -1 for failures
    */
    public int removeItems(Material type, int amount) {
        if(type == null)
            return -1;       
        if (amount <= 0)
            return -1;
        Inventory inventory = this.player.getInventory();
        if (amount == Integer.MAX_VALUE) {
            inventory.remove(type);
            return 0;
        }
 
        HashMap<Integer,ItemStack> retVal = inventory.removeItem(new ItemStack(type,amount));
 
        int notRemoved = 0;
        for(ItemStack item : retVal.values()) {
            notRemoved += item.getAmount();
        }
        return notRemoved;
    }
    
    
    /**
     * Retrieves the amount of specified items in the players inventory
     * @param type
     * @return
     */
    public int getAmountOfType(Material type) {
    	Inventory inventory = player.getInventory();
    	int count = 0;
    	for(ItemStack item : inventory.all(type).values()) {
    		count += item.getAmount();
    	}
    	return count;
    }
	
	
	/**
	 * Check if this player is invincible
	 * @return true if player is invincible, false if not
	 */
	public boolean isInvincible() {
		return this.invincible;
	}
	
	
	/**
	 * Set the invincibility of player
	 */
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	
	/**
	 * Sets a specific data for player
	 * @param key data identifier
	 * @param data data to store
	 * @return previous data if exists
	 */
	public Object setData(final NamespacedKey key, final Object data) {
		return playerData.put(key, new SimpleEntry<Object,Boolean>(data,false));
	}
	
	
	/**
	 * Sets a specific data to player
	 * @param plugin which plugin does this data belong to
	 * @param key the key of the data
	 * @param data the data to be stores
	 * @return previous data if exists
	 */
	public Object setData(final KaranteeniPlugin plugin, final String key, final Object data) {
		return playerData.put(new NamespacedKey(plugin, key), new SimpleEntry<Object,Boolean>(data,false));
	}
	
	
	/**
	 * Sets a specific data for player
	 * @param key data identifier
	 * @param data data to store
	 * @return previous data if exists
	 */
	public Object setCacheData(final NamespacedKey key, final Object data) {
		return playerData.put(key, new SimpleEntry<Object,Boolean>(data,true));
	}

	public Object removeCacheData(final NamespacedKey key) {
		return playerData.remove(key);
	}
	
	
	/**
	 * Sets a specific data to player
	 * @param plugin which plugin does this data belong to
	 * @param key the key of the data
	 * @param data the data to be stores
	 * @return previous data if exists
	 */
	public Object setCacheData(final KaranteeniPlugin plugin, final String key, final Object data) {
		return playerData.put(new NamespacedKey(plugin, key), new SimpleEntry<Object,Boolean>(data,true));
	}

	public Object removeCacheData(final KaranteeniPlugin plugin, final String key) {
		return removeCacheData(new NamespacedKey(plugin, key));
	}
	
	
	/**
	 * Tests if player has data assosiated for this key
	 * @param key
	 * @return
	 */
	public boolean dataExists(final NamespacedKey key) {
		return playerData.containsKey(key);
	}
	
	
	/**
	 * Tests if player has data assosiated for this key
	 * @param key
	 * @return
	 */
	public boolean dataExists(final Plugin plugin, final String key) {
		return playerData.containsKey(new NamespacedKey(plugin, key));
	}
	
	
	/**
	 * Deletes a data associated to key and returns it
	 * @param key
	 * @return data deleted
	 */
	public Object removeData(final Plugin plugin, final String key) {
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
	public Object removeData(final NamespacedKey key) {
		return playerData.remove(key);
	}
	
	
	/**
	 * Returns the player associated with this class
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	
	
	/**
	 * Check if the player variable has been set
	 * @return true if is set, false otherwise
	 */
	public boolean isValid() {
		return player != null;
	}
	
	
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	public Object getData(final NamespacedKey key) {
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
	@SuppressWarnings("unchecked")
	public <T> T getObject(final NamespacedKey key) throws ClassCastException {
		Entry<Object,Boolean> entry = playerData.get(key);
		
		if(entry == null)
			return null;
		return (T)entry.getKey();
	}
	
	
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	public List<?> getList(final NamespacedKey key) {
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
	public boolean getBoolean(final NamespacedKey key) {
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
	public boolean dropItemsAtPlayer(List<ItemStack> items) {
		if(!isValid()) return false;
		
		Location l = player.getLocation();
		for(ItemStack item : items)
			l.getWorld().dropItemNaturally(l, item);
		return true;
	}
	
	
	/**
	 * Returns a string data from player
	 * @param key
	 * @return
	 */
	public String getString(final NamespacedKey key) {
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
	public Float getFloat(final NamespacedKey key) {
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
	public Double getDouble(final NamespacedKey key) {
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
	public Integer getInt(final NamespacedKey key) {
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
	public Character getChar(final NamespacedKey key) {
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
	public Location getLocationData(final NamespacedKey key) {
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
	public Object getData(final Plugin plugin, final String key) {
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		
		return entry.getKey();
	}
	
	
	/**
	 * Gets nonspecific datavalue from players data
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(final Plugin plugin, final String key) throws ClassCastException {
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		
		return (T)entry.getKey();
	}
	
	
	/**
	 * Is the boolean data stored to player true or false
	 * @param key data to find
	 * @return boolean data value or false if not found
	 */
	public boolean getBoolean(final Plugin plugin, final String key) {
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
	public String getString(final Plugin plugin, final String key) {
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
	public Float getFloat(final Plugin plugin, final String key) {
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
	public Double getDouble(final Plugin plugin, final String key) {
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
	public Integer getInt(final Plugin plugin, final String key) {
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
	public Character getChar(final Plugin plugin, final String key) {
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
	public Location getLocationData(final Plugin plugin, final String key) {
		Entry<Object,Boolean> entry = playerData.get(new NamespacedKey(plugin, key));
		if(entry == null)
			return null;
		Object data = entry.getKey();
		if(data instanceof Location)
			return (Location)data;
		return null;
	}
}
