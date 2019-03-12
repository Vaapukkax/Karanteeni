package net.karanteeni.core.data.structures.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.karanteeni.core.data.structures.lists.Deque;

public class Digraph<A, B>
{
	private HashMap< A , Vertex<B>> vertexList = new HashMap< A , Vertex<B>>();
	private Deque< Edge<B> > edgeList = new Deque< Edge<B> >();
	
	/**
	 * Constructor for graph
	 */
	public Digraph()
	{
		
	}

	/**
	 * Returns the amount of vertices in the graph O(1)
	 * @return Amount of vertices
	 */
	public int numVertices()
	{ return vertexList.size();	}

	/**
	 * Returns the amount of edges in the graph O(1)
	 * @return Amount of edges
	 */
	public int numEdges()
	{ return edgeList.size();	}

	/**
   * Returns the indegree of the vertex
   * @param v Vertex which indegree amount will be returned
   * @return Edges coming in
   */
	public int degree(Vertex<A> v)
	{ return v.getEdges().size(); }

	/**
	 * Returns the keys used by this graph
	 * @return Collection of keys used by this graph vertices
	 */
	public Collection<A> getKeys()
	{ return this.vertexList.keySet(); }
	
	/**
	 * Returns the map of keys and their vertices 
	 * @return
	 */
	public Map<A, Vertex<B>> getKeyValuePairs()
	{ return this.vertexList; }
	
	/**
	 * Inserts a vertex to graph
	 * @param key vertexes key
	 * @param data vertexes data
	 */
	public void insertVertex(A key, B data)
	{ vertexList.put(key, new Vertex<B>(data)); }
  
  /**
   * Checks whether this graph contains any vertices
   * @return is this graph empty from vertices
   */
  public boolean isEmpty()
  { return vertexList.size() == 0; }
  
  /**
   * Inserts a new directed edge to graph
   * @param from
   * @param to
   * @param key
   * @param data
   * @return 
   */
  public Edge<B> 
    insertDirectedEdge(Vertex<B> from, Vertex<B> to)
  { return edgeList.insertLast(new Edge<B>(from, to)).getValue(); }
  
  /**
   * Inserts an edge based on values
   * @param first First key
   * @param second Second key
   * @return null if values not found
   */
  public Edge<B> insertDirectedEdge(B first, B second)
  {
	  Vertex<B> a1 = null;
	  Vertex<B> a2 = null;
	  
	  for(Entry<A,Vertex<B>> ent : vertexList.entrySet())
	  {
		  if(a1 == null && ent.getKey() == first)
			  a1 = ent.getValue();
		  
		  if(a2 == null && ent.getKey() == second)
			  a2 = ent.getValue();
		  
		  if(a1 != null && a2 != null)
		  {
			  Edge<B> edge = new Edge<B>(a1,a2); 
			  edgeList.insertLast(edge);
			  return edge;
		  }
	  }
	  return null;
  }
  
  /**
   * Inserts an edge based on values
   * @param first Key to look for
   * @param second Key to look for
   * @return null if values not found
   */
  public Edge<B> insertEqualsUndirectedEdge(A first, A second)
  {
	  Vertex<B> a1 = null;
	  Vertex<B> a2 = null;
	  
	  for(Entry<A,Vertex<B>> ent : vertexList.entrySet())
	  {
		  if(a1 == null && ent.getKey().equals(first))
			  a1 = ent.getValue();
		  
		  if(a2 == null && ent.getKey().equals(second))
			  a2 = ent.getValue();
		  
		  if(a1 != null && a2 != null)
		  {
			  Edge<B> edge = new Edge<B>(a1,a2); 
			  edgeList.insertLast(edge);
			  return edge;
		  }
	  }
	  return null;
  }
  
  /**
   * Returns the size of the graph O(1)
   * @return
   */
  public int size()
  { return vertexList.size() + edgeList.size(); }
  
  /**
   * Removes an edge from the graph
   * @param edge edge to be removed
   * @return The value the edge held
   */
  public void removeEdge(final Edge<B> edge)
  { 
    edge.getFrom().getEdges().removeElem(edge);
    edge.getTo().getEdges().removeElem(edge);
    
    edgeList.removeElem(edge);
  }
  
  /**
   * Returns the values put to this graph
   * @return
   */
  public Collection<B> getValues()
  {
	  List<B> vals = new ArrayList<B>();
	  
	  vertexList.values().forEach((Vertex<B> val) -> {vals.add(val.getValue());});
	  return vals;
  }
  
  /**
   * Removes the given vertex from graph
   * @param vertex vertex to be removed
   * @return the value the vertex held.
   */
  @SuppressWarnings("unused")
public B removeVertex(Vertex<B> vertex)
  { 
    //Remove edges to this node
    for(Edge<B> edge : vertex.getEdges())
      removeEdge(edge);
    
    vertexList.remove(vertex);
    
    if(vertex == null) return null;
    
    //Remove vertex from main list
    return vertex.getValue();
  }
  
  /**
   * Returns the vertices in this graph.
   * Editing these may break them!
   * @return the vertices of this graph.
   */
  public Collection<Vertex<B>> getVertices()
  { return this.vertexList.values(); }
  
  /**
   * Returns the edges in this graph.
   * Editing thse may break them!
   * @return the edges of this graph
   */
  public Deque<Edge<B>> getEdges()
  { return this.edgeList; }
  
  /**
   * Returns the degrees of a vertex
   * @param vertex
   * @return
   */
  public int degrees(Vertex<B> vertex)
  { return vertex.getEdges().size(); }
  
  /**
   * Checks whether two vertices are connected
   * @param a
   * @param b
   * @return
   */
  public boolean areAdjacent(Vertex<B> a, Vertex<B> b)
  {
	for(Edge<B> e : this.edgeList)
		if(e.getFrom() == e.getTo())
			return true;
    return false;
  }
}
