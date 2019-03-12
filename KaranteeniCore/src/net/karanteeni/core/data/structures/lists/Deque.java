package net.karanteeni.core.data.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A Doubly linked list
 * @author Matti Turpeinen
 *
 * @param <T> Which types are stored in the list
 */
public class Deque<T> implements Iterable<T>
{
  private Node<T> head;
  private Node<T> tail;
  private int length;
  
  /**
   * Creates a new double linked list with no values
   */
  public Deque() {}
  
  /**
   * Creates a new list with one value
   * @param val value for the first element
   */
  public Deque(T val)
  {
    head = new Node<T>(val);
    tail = head;
    length = 1;
  }
  
  /**
   * Iterator for iterating over list elements
   * @author Matti Turpeinen
   *
   */
  private class DequeIterator implements Iterator<T> 
  {
    private Node<T> nextNode = head;
    
    @Override
    public boolean hasNext()
    { return nextNode != null; }

    @Override
    public T next()
    {
      if(!hasNext()) throw new NoSuchElementException("Iteration exceeded!");
      
      T ret = nextNode.getValue();
      nextNode = nextNode.getNext();
      return ret;
    }
  }
  
  /**
   * Clears the deque
   */
  public void clear()
  {
    this.head = null;
    this.tail = null;
    this.length = 0;
  }
  
  /**
   * Returns whether this deque contains an object
   * @param val Object to be looked for
   * @return Contains object
   */
  public boolean contains(T val)
  {
    for(T t : this)
      if(t == val)
        return true;
    return false;
  }
  
  /**
   * Returns whether this deque contains an object
   * from another deque
   * @param vals Deque of objects to be looked for
   * @return Contains object
   */
  public boolean containsElem(Deque<T> vals)
  {
    for(T t : this)
      for(T t2 : vals)
        if(t == t2)
          return true;
    return false;
  }
  
  /**
   * Returns whether the list is empty
   * @return is list empty
   */
  public boolean isEmpty()
  { return length == 0; }
  
  /**
   * Returns the head node of the list
   * @return head node
   */
  public Node<T> getHead()
  { return head; }
  
  /**
   * Returns the tail node of the list
   * @return tail node
   */
  public Node<T> getTail()
  { return tail; }
  
  public int size()
  { return length; }
  
  /**
   * Gets the value of head from the list
   * @return value in the head
   * @throws IndexOutOfBoundsException empty list
   */
  public T first() throws IndexOutOfBoundsException
  {
    if(head == null)
      throw new IndexOutOfBoundsException("Size of list is 0!");
    return head.getValue();
  }
  
  /**
   * Gets the value of tail from the list
   * @return value in the tail
   * @throws IndexOutOfBoundsException empty list
   */
  public T last() throws IndexOutOfBoundsException
  {
    if(tail == null)
      throw new IndexOutOfBoundsException("Size of list is 0!");
    return tail.getValue();
  }

  /**
   * Adds a new value to the end of the list
   * @param value value stored
   * @return the node created and added
   */
  public Node<T> insertFirst(T value)
  {
    Node<T> newNode = new Node<T>(value);
    ++length;
    
    //If list is empty then create a new node
    if(head == null)
    {
      tail = newNode;
      head = newNode;
      return newNode;
    }
    else
    {
      head.setPrev(newNode);
      newNode.setNext(tail);
      head = newNode;
      return newNode;
    }
  }
  
  /**
   * Adds a new value to the end of the list
   * @param value value stored
   * @return the node created and added
   */
  public Node<T> insertLast(T value)
  {
    Node<T> newNode = new Node<T>(value);
    ++length;
    
    //If list is empty then create a new node
    if(tail == null)
    {
      tail = newNode;
      head = newNode;
      return newNode;
    }
    else
    {
      tail.setNext(newNode);
      newNode.setPrev(tail);
      tail = newNode;
      return newNode;
    }
  }
  
  /**
   * Removes the first element in the list
   * @return the value in the head or null if empty
   */
  public T removeFirst()
  {
    if(head == null) return null;
    else if(head.getNext() == null)
    {
      T val = head.getValue();
      head = null;
      tail = null;
      --length;
      return val;
    }
    else
    {
      T val = head.getValue();
      head = head.getNext();
      head.setPrev(null);
      --length;
      return val;
    }
  }
  
  /**
   * Removes the last element in the list
   * @return the value in the tail or null if empty
   */
  public T removeLast()
  {
    if(tail == null) return null;
    else if(tail.getPrev() == null)
    {
      T val = tail.getValue();
      head = null;
      tail = null;
      --length;
      return val;
    }
    else
    {
      T val = tail.getValue();
      tail = tail.getPrev();
      tail.setNext(null);
      --length;
      return val;
    }
  }
  
  /**
   * Returns a value from index
   * @param index index to get value from
   * @return Value found
   * @throws IndexOutOfBoundsException index < 0 or index >= length
   */
  public T get(int index) throws IndexOutOfBoundsException
  {
    if(index >= length || index < 0)
      throw new IndexOutOfBoundsException(
          String.format("Index out of range! List length is %s, value given %s", length, index));
    else
    {
      //Loop the nodes until at index
      Node<T> ret = head;
      while(index-- > 0)
        ret = ret.getNext();
      return ret.getValue();
    }
  }
  
  /**
   * Removes a value(node) from the list
   * @param index index from which the value will be removed
   * @return the value at the index
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public T removeElem(int index) throws IndexOutOfBoundsException
  {
    if(index >= length || index < 0)
      throw new IndexOutOfBoundsException(
          String.format("Index out of range! List length is %s, value given %s", length, index));
    else
    {
      //Loop the nodes until at index
      Node<T> ret = head;
      while(index-- > 0)
        ret = ret.getNext();
      
      Node<T> prev = ret.getPrev();
      Node<T> next = ret.getNext();
      
      //relink the nodes to new order
      if(prev == null && next == null)
      {
        head = null;
        tail = null;
      }
      else if(prev == null)
      {
        head = next;
        next.setPrev(null);
      }
      else if(next == null)
      {
        tail = prev;
        prev.setNext(null);
      }
      else
      {
        prev.setNext(next);
        next.setPrev(prev);
      }
      
      --length;
      return ret.getValue();
    }
  }
  
  /**
   * Removes a value(node) from the list
   * @param index index from which the value will be removed
   * @return the value at the index
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public T removeElem(T elem) throws IndexOutOfBoundsException
  {
    //Loop the nodes until at index
    Node<T> ret = head;
    while(ret != null && !(ret.getValue() == elem))
      ret = ret.getNext();
    
    if(ret == null)
      return null;
    
    Node<T> prev = ret.getPrev();
    Node<T> next = ret.getNext();
    
    //relink the nodes to new order
    if(prev == null && next == null)
    {
      head = null;
      tail = null;
    }
    else if(prev == null)
    {
      head = next;
      next.setPrev(null);
    }
    else if(next == null)
    {
      tail = prev;
      prev.setNext(null);
    }
    else
    {
      prev.setNext(next);
      next.setPrev(prev);
    }
    
    --length;
    return ret.getValue();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<T> iterator()
  { return new DequeIterator(); }
}
