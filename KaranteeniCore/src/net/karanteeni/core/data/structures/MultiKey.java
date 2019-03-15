package net.karanteeni.core.data.structures;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

/**
 * A multikey which has multiple values which all of can be accessed.
 * May be useful in Maps or Routes
 * @author Nuubles
 *
 * @param <K>
 */
public class MultiKey<K extends Comparable<K>> implements Comparable<MultiKey<K>>{
	
	private Set<K> keys;
	
	/**
	 * Initializes the multikey with given keys
	 * @param keys Keys the multikey has
	 */
	public MultiKey(Set<K> keys)
	{ 
		this.keys = keys; 
	}

	/**
	 * Initializes the multikey with given keys
	 * @param keys Keys the multikey has
	 */
	public MultiKey(Collection<K> keys)
	{ 
		this.keys = new HashSet<K>(keys); 
	}
	
	/**
	 * Initializes the multikey with given keys
	 * @param keys Keys the multikey has
	 */
	@SafeVarargs
	public MultiKey(K...keys)
	{ 
		this.keys = new HashSet<K>(Arrays.asList(keys)); 
	}
	
	/**
	 * Add a new key to the keys
	 * @param key
	 */
	public void addKey(K key)
	{ keys.add(key); }
	
	/**
	 * Removes a key from the multikey
	 * @param key
	 * @return
	 */
	public boolean removeKey(K key)
	{
		return this.keys.remove(key);
	}
	
	/**
	 * Check if multikey contains a given key.
	 * If given key is multikey, each value is checked and returns
	 * true if one key matches another
	 */
	@Override
	public boolean equals(Object key)
	{
		if(key == null)
		{
			Bukkit.broadcastMessage("false");
			return false;
		}
		
		if(!(key instanceof MultiKey<?>))
		{
			Bukkit.broadcastMessage(keys.contains(key)+"");
			return keys.contains(key);
		}
		else
		{
			MultiKey<?> key_ = (MultiKey<?>) key;
			for(K k : this.keys)
				if(key_.keys.contains(k))
				{
					Bukkit.broadcastMessage("true");
					return true;
				}
			Bukkit.broadcastMessage("false");
			return false;
		}
	}

	/**
	 * Returns 1 if any key of multikey is larger than arg,
	 * -1 if smaller, 0 otherwise
	 * @param arg
	 * @return
	 */
	/*@Override
	public int compareTo(K arg) {
		boolean larger = true;
		boolean smaller = true;
		
		for(K key : keys)
		{
			if(key.compareTo(arg) != 1)
				larger = false;
			
			if(key.compareTo(arg) != -1)
				smaller = false;
		}
		
		if((smaller && larger) || (!smaller && !larger))
			return 0;
		
		if(larger)
			return 1;
		return -1;
	}*/
	
	@Override
	public int compareTo(MultiKey<K> key)
	{
		if(this.keys.size() < key.keys.size())
			return -1;
		if(this.keys.size() > key.keys.size())
			return 1;
		return 0;
	}
	
}
