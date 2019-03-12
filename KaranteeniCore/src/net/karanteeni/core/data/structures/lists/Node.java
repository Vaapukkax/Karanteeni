package net.karanteeni.core.data.structures.lists;

/**
 * A node for double linked list data structure
 * 
 * @author Matti Turpeinen
 *
 * @param <T> Value this and following nodes will be holding
 */
public class Node<T>
{
  private T       value;
  private Node<T> next;
  private Node<T> previous;

  /**
   * Creates a new node with value
   * @param value Value of the node
   */
  public Node(T value)
  { this.value = value; }

  /**
   * Creates a new node with a value and gives it the next node
   * @param value Value of the node
   * @param next  Next node
   */
  public Node(T value, Node<T> next)
  {
    this.value = value;
    this.next = next;
  }

  /**
   * Creates a new node with adjecent nodes
   * @param value    Value of the node
   * @param next     Next node
   * @param previous Previous node
   */
  public Node(T value, Node<T> next, Node<T> previous)
  {
    this.value = value;
    this.next = next;
    this.previous = previous;
  }

  /**
   * Returns the value of this node
   * @return The value of this node
   */
  public T getValue()
  { return this.value; }

  /**
   * Sets the value of thise node
   * @param value new value for node
   */
  public void setValue(T value)
  { this.value = value; }

  /**
   * Returns the next node
   * @return next node
   */
  public Node<T> getNext()
  { return this.next; }

  /**
   * Returns the previous node
   * @return previous node
   */
  public Node<T> getPrev()
  { return this.previous; }

  /**
   * Sets the next node
   * @param next new next node
   */
  public void setNext(Node<T> next)
  { this.next = next; }

  /**
   * Sets the previous node
   * @param prev new previous node
   */
  public void setPrev(Node<T> prev)
  { this.previous = prev; }
}
