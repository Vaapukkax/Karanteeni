package net.karanteeni.core.data.structures.graphs;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Route<K extends Comparable<K>,V> {
	//Path we've made
	private Deque<LinkedNode<K,V>> path = new ArrayDeque<LinkedNode<K,V>>();
	//Path we've made but stepped back from
	private Deque<LinkedNode<K,V>> futurePath = new ArrayDeque<LinkedNode<K,V>>();
	//Nodes in this web
	private Map<K,LinkedNode<K,V>> nodes = new HashMap<K,LinkedNode<K,V>>();

	/**
	 * Initializes the route to start with given path startpoints
	 * @param startValues Points which from the path can be started
	 */
	public Route(Collection<Entry<K,V>> startValues)
	{
		for(Entry<K,V> entry : startValues)
			nodes.put(entry.getKey(), new LinkedNode<K,V>(entry.getKey(),entry.getValue()));
	}
	
	/**
	 * Initializes an empty route with no startpoints
	 * to start traversal
	 */
	public Route()
	{}
	
	/**
	 * Initializes a route with one startpoint to start
	 * traversal from
	 * @param key Key of the startpoint
	 * @param value Value of the startpoint
	 */
	public Route(final K key, final V value)
	{ nodes.put(key, new LinkedNode<K,V>(key,value)); }
	
	/**
	 * Traverses a given path starting from current point.
	 * Stops traversal if unsuccessful, but stays at endpoint.
	 * Cannot perform restep after this action, traversal is stored in unstep.
	 * @param points Keys to travel along
	 * @return True if traversal was successful to point, False if unsuccessful and
	 * stayed at endpoint 
	 */
	public boolean traversePoints(List<K> points)
	{
		//Traverse the points
		for(K point : points)
		{
			if(!this.hasNextNode(point))
				return false;
			this.step(point);
		}
		
		return true;
	}
	
	/**
	 * Connects current node to another node following the
	 * given path. Will not replace existing node if there is one
	 * with same key as the node being added.
	 * Basically creates a bridge from current node to the node
	 * in the given path
	 * @param points Path to follow to next node FROM START
	 * @return Was the route created to the path
	 */
	public boolean addCurrentToPath(List<K> points)
	{
		//If empty list is given, return false
		if(points == null || points.size() == 0)
			return false;
		
		if(this.path.peek().hasNextNode(points.get(points.size()-1)))
			return false;
		
		return this.putCurrentToPath(points);
	}
	
	/**
	 * Connects current node to another node following the
	 * given path and replaces old node with same key if there is one.
	 * Basically creates a bridge from current node to the node
	 * in the given path
	 * @param points Path to follow to next node FROM START
	 * @return Was the route created to the path
	 */
	public boolean putCurrentToPath(List<K> points)
	{
		//If empty list is given, return false
		if(points == null || points.size() == 0)
			return false;
		
		if(this.nodes.isEmpty())
			return false;
		
		boolean firstStep = true;
		LinkedNode<K,V> currentNode = null;
		
		//Traverse path along points
		for(K point : points)
		{
			if(firstStep)
			{
				//Node to start the traversal from
				currentNode = this.nodes.get(points);
				if(currentNode == null)
					return false;
				continue;
			}
			
			currentNode = currentNode.getNextNode(point);
			
			if(currentNode == null)
				return false;
		}
		
		//currentNode will be the node to which current node needs to connect
		this.path.peek().putNextNode(currentNode);
		return true;
	}
	
	/**
	 * Traverses a given path starting from current point.
	 * Stops traversal if unsuccessful, but stays at endpoint.
	 * Cannot perform restep after this action, traversal is stored in unstep.
	 * @param points Keys to travel along
	 * @return True if traversal was successful to point, False if unsuccessful and
	 * stayed at endpoint 
	 */
	public boolean traversePoints(@SuppressWarnings("unchecked") K... points)
	{
		if(points == null)
			return false;
		
		//Traverse the points
		for(K point : points)
		{
			if(!this.hasNextNode(point))
				return false;
			this.step(point);
		}
		
		return true;
	}
	
	/**
	 * Returns the possible path choices for next key
	 * @return
	 */
	public Collection<K> getNextKeys()
	{ 
		if(path.isEmpty()) //Begin a new path
			return nodes.keySet();
		else //Continue old path
			return path.peek().getNextKeys();
	}
	
	
	/**
	 * Traverses forward in path to the next object.
	 * Cannot restep after this is ran unless unsteps are made
	 * @param key Key to which object should we traverse to
	 * @return List of keys possible to continue from the next node
	 */
	public Collection<K> step(final K key)
	{
		if(path.isEmpty())
		{
			LinkedNode<K,V> node = nodes.get(key);
			if(node == null) //There is no next node after this, return empty list
				return new ArrayDeque<K>();
			path.push(node); //Add new node to the route traversed
			futurePath.clear();
			return node.getNextKeys();
		}
		
		//We've already traversed some path, continue on
		LinkedNode<K,V> node = path.peek().getNextNode(key);
		if(node == null) //There is no next node after this, return empty list
			return new ArrayDeque<K>();
		
		path.push(node);
		futurePath.clear();
		return node.getNextKeys();
	}
	
	/**
	 * Removes the next node if it exists
	 * @param key Key of the next node to remove
	 * @return Value of the node removed
	 */
	public V removeNextNode(final K key)
	{
		//We haven't started traversal yet, remove from start nodes
		if(path.isEmpty())
		{
			LinkedNode<K,V> node = nodes.remove(key);
			if(node == null)
				return null;
			return node.getValue();
		}
		
		LinkedNode<K,V> node = path.peek().removeNextNode(key);
		if(node == null)
			return null;
		return node.getValue();
	}
	
	/**
	 * Removes all the next nodes after this node, or if
	 * we've not traversed at all, clear the start nodes.
	 */
	public void clearNextNodes()
	{
		if(path.isEmpty())
		{
			this.nodes.clear();
			return;
		}
		
		path.peek().clearNextNodes();
	}
	
	/**
	 * Steps back one step in the route.
	 * Works kinda like UNDO
	 * @return Can we step back more
	 */
	public boolean unstep()
	{
		if(path.isEmpty())
			return false;
			
		futurePath.push(path.pop());
		return !path.isEmpty();
	}
	
	/**
	 * Steps forward in path we've already traversed
	 * but backstepped.
	 * Works kinda like REDO
	 * @return Can we restep further
	 */
	public boolean restep()
	{
		if(futurePath.isEmpty())
			return false;
		
		path.push(futurePath.pop());
		return !futurePath.isEmpty();
	}
	
	/**
	 * Checks if this route has no routes
	 * @return Is this route empty
	 */
	public boolean isEmpty()
	{ return this.nodes.isEmpty(); }
	
	/**
	 * Returns the traversed path from start to current node.
	 * MAY LOOP LIKE A RING!
	 * @return List of key-value pairs in the traversed order
	 */
	public ArrayDeque<Entry<K,V>> getCurrentPath()
	{
		ArrayDeque<Entry<K,V>> traversal = new ArrayDeque<Entry<K,V>>();
		
		for(LinkedNode<K,V> node : this.path)
			traversal.add(new SimpleEntry<K,V>(node.getKey(),node.getValue()));
		return traversal;
	}
	
	/**
	 * Clears the traversed path, returning to start where
	 * no moves were made.
	 * Restep and unstep cannot be operated after this unless
	 * steps are made.
	 */
	public void clearPath()
	{
		this.path.clear();
		this.futurePath.clear();
	}
	
	/**
	 * Are there any nodes further where
	 * it is possible to traverse to.
	 * @return If there are nodes after this node to traverse to
	 */
	public boolean hasNextNodes()
	{ 
		if(this.path.isEmpty())
			return this.nodes.isEmpty();
		return path.peek().isEmpty(); 
	}
	
	/**
	 * Checks if it is possible to traverse from current position to a node with
	 * given key
	 * @param key Key to look for
	 * @return Is it possible to traverse with given key
	 */
	public boolean hasNextNode(final K key)
	{
		if(this.path.isEmpty())
			return this.nodes.containsKey(key);
		if(this.path.peek().isEmpty())
			return false;
		return this.path.peek().hasNextNode(key);
	}
	
	/**
	 * Checks if it is possible to restep (REDO)
	 * @return Is it possible to restep unstepped steps
	 */
	public boolean canRestep()
	{ return !this.futurePath.isEmpty(); }
	
	/**
	 * Checks if it is possible to unstep (UNDO)
	 * @return Is it possible to unstep stepped steps
	 */
	public boolean canUnstep()
	{ return !this.path.isEmpty(); }
	
	/**
	 * Adds a new node after current node or at the start nodes
	 * @param key Key for the new node
	 * @param value Value for the new node
	 * @return True if node was added, False if node already existed
	 * and was not added
	 */
	public boolean addNextNode(final K key, final V value)
	{
		if(this.hasNextNode(key))
			return false;
		
		if(this.path.isEmpty())
			this.nodes.put(key, new LinkedNode<K,V>(key, value));
		else
			this.path.peek().addNextNode(key, value);
		return true;
	}
	
	/**
	 * Puts a new node after current node or start nodes
	 * @param key Key for the new node
	 * @param value Value for the new node
	 */
	public void putNextNode(final K key, final V value)
	{
		if(this.path.isEmpty())
			this.nodes.put(key, new LinkedNode<K,V>(key, value));
		else
			this.path.peek().addNextNode(key, value);
	}
}
