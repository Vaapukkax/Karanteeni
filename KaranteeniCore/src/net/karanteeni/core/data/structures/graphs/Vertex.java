package net.karanteeni.core.data.structures.graphs;

import net.karanteeni.core.data.structures.lists.Deque;

public class Vertex<V> {	
	private V value;
	private Deque<Edge<V>> edges 		= new Deque<Edge<V>>();

	/**
	 * Creates a new vertex with given variables
	 * @param key   The key of this vertex
	 * @param value The value of this vertex
	 */
	public Vertex(final V value)
	{
		this.value = value;
	}
	
	/**
	 * Returns the incoming edges for this vertex
	 * @return The incoming edges
	 */
	public Deque<Edge<V>> getEdges()
	{ return this.edges; }

	/**
	 * Sets the incoming edges by Node<E>
	 * @param inEdges The new incoming edges
	 */
	public void setEdges(Deque<Edge<V>> inEdges)
	{ this.edges = inEdges; }

	
	/**
	 * Adds a new incoming edge to the vertex
	 * @param edge the incoming edge
	 */
	public void addEdge(Edge<V> edge)
	{ this.edges.insertLast(edge); }

	/**
	 * Returns the value this Vertex holds
	 * @return Vertexes value
	 */
	public V getValue()
	{ return value; }


	/**
	 * Sets the value of this vertex
	 * @param value the new value
	 */
	public void setValue(final V value)
	{ this.value = value; }
	
	/**
	 * Returns this variable as a string for debugging purposes
	 * @return {Key: key, Value: value}
	 */
	@Override
	public String toString()
	{ return String.format("Value: %s", value); }
}
