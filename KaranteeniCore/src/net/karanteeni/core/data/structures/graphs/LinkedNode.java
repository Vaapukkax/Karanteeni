package net.karanteeni.core.data.structures.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A node that is linked forward to next nodes. Used for example by
 * Web data structure
 * @author Nuubles
 *
 * @param <K> Key
 * @param <V> Value
 */
public class LinkedNode <K extends Comparable<K>,V> implements Comparable<LinkedNode<K,V>>, Iterable<LinkedNode<K,V>>{
	private K key;
	private V value;
	private Map<K,LinkedNode<K,V>> nextNodes = new HashMap<K,LinkedNode<K,V>>();
	
	/**
	 * Initializes a new linkedNode with a key and a value
	 * @param key Key of this node
	 * @param value Value of this node
	 */
	public LinkedNode(final K key, final V value) throws IllegalArgumentException
	{
		if(key == null)
			throw new IllegalArgumentException("Given key was null!");
		
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Returns the next node using a key 
	 * @param key Key which the next node needs to have
	 * @return Next node after this or null if there's no next node
	 */
	public LinkedNode<K,V> getNextNode(final K key)
	{ return nextNodes.get(key); }
	
	/**
	 * Returns the all the next nodes after this node
	 * @return Collection of next nodes after this
	 */
	public Collection<LinkedNode<K,V>> getNextNodes()
	{ return nextNodes.values(); }
	
	/**
	 * Returns the next possible routes after this node
	 * @return Routes after this node
	 */
	public Collection<K> getNextKeys()
	{ return nextNodes.keySet(); }
	
	/**
	 * Return the next node which was removed or null
	 * if no node was found
	 * @param key Key of the node to be removed
	 * @return The removed node
	 */
	public LinkedNode<K,V> removeNextNode(final K key)
	{ return this.nextNodes.remove(key); }
	
	/**
	 * Adds a next node to this node and overrides old if there
	 * is some node with same key
	 * @param key Key of the next node to add
	 * @param value Value of the next node to add
	 */
	public void putNextNode(final K key, final V value)
	{
		LinkedNode<K,V> node = new LinkedNode<K,V>(key,value);
		this.nextNodes.put(key, node);
	}
	
	/**
	 * Adds a next node to this node and overrides old if there
	 * is some node with same key
	 * @param node Node to add
	 */
	public void putNextNode(LinkedNode<K,V> node)
	{ this.nextNodes.put(node.key, node); }
	
	/**
	 * Like putNextNode, but if key already exists,
	 * does not add.
	 * @param key Key of the node
	 * @param value Value of the node
	 * @return Was the addition successful
	 */
	public boolean addNextNode(final K key, final V value)
	{
		if(this.nextNodes.containsKey(key))
			return false;
		LinkedNode<K,V> node = new LinkedNode<K,V>(key,value);
		this.nextNodes.put(key, node);
		return true;
	}
	
	/**
	 * Like putNextNode, but if key already exists,
	 * does not add.
	 * @param node Node to add
	 * @return Was the addition successful
	 */
	public boolean addNextNode(final LinkedNode<K,V> node)
	{
		if(this.nextNodes.containsKey(node.key))
			return false;
		this.nextNodes.put(key, node);
		return true;
	}
	
	/**
	 * Removes all the nodes after this node
	 */
	public void clearNextNodes()
	{ this.nextNodes.clear(); }
	
	/**
	 * Compares the values of the nodes, but not the
	 * next nodes. If nodes contain null then return false
	 */
	@Override
	public boolean equals(Object node)
	{
		if(!(node instanceof LinkedNode<?,?>))
			return false;
		
		LinkedNode<?,?> converted = (LinkedNode<?,?>)node;
		
		//Verify there are no null values
		if(this.key == null || this.value == null || converted.key == null || converted.value == null)
			return false;
		
		return (this.key.equals(converted.key) && this.value.equals(converted.value));
	}
	
	/**
	 * Does this node have next nodes to traverse to
	 * @return True if can travel further, false otherwise
	 */
	public boolean isEmpty()
	{ return this.nextNodes.isEmpty(); }
	
	/**
	 * Compare the keys of this and a given node
	 */
	@Override
	public int compareTo(LinkedNode<K,V> node)
	{ return this.key.compareTo(node.key); }
	
	/**
	 * Checks if there is a next node that has given key
	 * @param key Key to look for
	 * @return Was there a next node with given key
	 */
	public boolean hasNextNode(final K key)
	{ return this.nextNodes.containsKey(key); }
	
	/*==========[ ACCESSORS ]=========*/
	
	/**
	 * Get this nodes key
	 * @return The key of this node
	 */
	public K getKey()
	{ return this.key; }
	
	/**
	 * Set the key of this node
	 * @param key the new node for this key
	 * @return Old key of this node
	 */
	public K setKey(final K key)
	{
		K k = this.key;
		this.key = key;
		return k;
	}
	
	/**
	 * Get the value of this node
	 * @return The value of this node
	 */
	public V getValue()
	{ return this.value; }
	
	/**
	 * Set the value of this node
	 * @param value The value of this node
	 * @return The old value of this value
	 */
	public V setValue(final V value)
	{ 
		V v = this.value;
		this.value = value;
		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<LinkedNode<K, V>> iterator() {
		return this.nextNodes.values().iterator();
	}
}
