package net.karanteeni.core.data.structures;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class UndirectedAdjacencyListGraph<A>
{
	private LinkedHashMap< Vertex<A> , A> vertexList = new LinkedHashMap< Vertex<A> , A>();
	private Deque< Edge<A> > edgeList = new Deque< Edge<A> >();
	
	/**
	 * Constructor for graph
	 */
	public UndirectedAdjacencyListGraph()
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
	 * Inserts a vertex to graph
	 * @param key vertexes key
	 * @param data vertexes data
	 */
	public void insertVertex(A data)
	{ vertexList.put(new Vertex<A>(data), data); }
  
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
  public Edge<A> 
    insertUndirectedEdge(Vertex<A> from, Vertex<A> to)
  { return edgeList.insertLast(new Edge<A>(from, to)).getValue(); }
  
  /**
   * Inserts an edge based on values
   * @param first
   * @param second
   * @return null if values not found
   */
  public Edge<A> insertUndirectedEdge(A first, A second)
  {
	  Vertex<A> a1 = null;
	  Vertex<A> a2 = null;
	  
	  for(Vertex<A> vertex : vertexList.keySet())
	  {
		  if(a1 == null && vertex.getValue() == first)
			  a1 = vertex;
		  
		  if(a2 == null && vertex.getValue() == second)
			  a2 = vertex;
		  
		  if(a1 == null && a2 == null)
		  {
			  Edge<A> edge = new Edge<A>(a1,a2); 
			  edgeList.insertLast(edge);
			  return edge;
		  }
	  }
	  return null;
  }
  
  /**
   * Inserts an edge based on values
   * @param first
   * @param second
   * @return null if values not found
   */
  public Edge<A> insertEqualsUndirectedEdge(A first, A second)
  {
	  Vertex<A> a1 = null;
	  Vertex<A> a2 = null;
	  
	  for(Vertex<A> vertex : vertexList.keySet())
	  {
		  if(a1 == null && vertex.getValue().equals(first))
			  a1 = vertex;
		  
		  if(a2 == null && vertex.getValue().equals(second))
			  a2 = vertex;
		  
		  if(a1 == null && a2 == null)
		  {
			  Edge<A> edge = new Edge<A>(a1,a2); 
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
  public void removeEdge(final Edge<A> edge)
  { 
    edge.getFrom().getEdges().removeElem(edge);
    edge.getTo().getEdges().removeElem(edge);
    
    edgeList.removeElem(edge);
  }
  
  /**
   * Returns the values put to this graph
   * @return
   */
  public Collection<A> getValues()
  {
	  return vertexList.values();
  }
  
  /**
   * Removes the given vertex from graph
   * @param vertex vertex to be removed
   * @return the value the vertex held.
   */
  @SuppressWarnings("unused")
public A removeVertex(Vertex<A> vertex)
  { 
    //Remove edges to this node
    for(Edge<A> edge : vertex.getEdges())
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
  public Set<Vertex<A>> getVertices()
  { return this.vertexList.keySet(); }
  
  /**
   * Returns the edges in this graph.
   * Editing thse may break them!
   * @return the edges of this graph
   */
  public Deque<Edge<A>> getEdges()
  { return this.edgeList; }
  
  /**
   * Returns the degrees of a vertex
   * @param vertex
   * @return
   */
  public int degrees(Vertex<A> vertex)
  { return vertex.getEdges().size(); }
  
  /**
   * Checks whether two vertices are connected
   * @param a
   * @param b
   * @return
   */
  public boolean areAdjacent(Vertex<A> a, Vertex<A> b)
  {
	for(Edge<A> e : this.edgeList)
		if(e.getFrom() == e.getTo())
			return true;
    return false;
  }
}
