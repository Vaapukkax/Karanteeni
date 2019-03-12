package net.karanteeni.core.data.structures;

/**
 * Edge for graphs
 * @author Matti Turpeinen
 *
 * @param <K> Edge key class
 * @param <V> Edge value class
 * @param <VK> Vertex key class
 * @param <VV> Vertex value class
 */
public class Edge<VV>
{
  private Vertex<VV>  from;
  private Vertex<VV>  to;

  /**
   * Creates a new edge with given key and value
   * @param key   Key for this edge
   * @param value Value(data) for this edge
   */
  public Edge()
  {
  }

  /**
   * Creates a new edge with given key and value. Connects the
   * given two vertices and adds new incoming and outgoing edge
   * to both.
   * @param from Start vertex where the edge starts
   * @param to  End vertex where the edge ends
   * @param key Key for this edge
   * @param data Value(data) for this edge
   */
  public Edge(Vertex<VV> from, Vertex<VV> to)
  {
    this.from = from;
    this.to = to;
    from.addEdge(this);
    to.addEdge(this);
  }

  /**
   * Gets the from vertex of this edge
   * @return The from vertex of this edge
   */
  public Vertex<VV> getFrom()
  { return from; }

  /**
   * Sets the from vertex of this edge
   * @param vertex The new from vertex of this edge
   */
  public void setFrom(Vertex<VV> vertex)
  { this.from = vertex; }

  /**
   * Gets the to vertex from this edge
   * @return The to vertex of this edge
   */
  public Vertex<VV> getTo()
  { return to; }

  /**
   * Sets the to vertex of this edge
   * @param vertex The new to vertex of this edge
   */
  public void setTo(Vertex<VV> vertex)
  { this.to = vertex; }
}
