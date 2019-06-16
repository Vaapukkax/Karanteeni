package net.karanteeni.core.data;

public class Pair<U,Y> {
	private U key;
	private Y value;
	
	
	/**
	 * Initialize a new pair with values
	 * @param u first (key) value of pair
	 * @param y second (value) value of pair
	 */
	public Pair(U key, Y value) {
		this.key = key;
		this.value = value;
	}
	
	
	/**
	 * Returns the key of this pair
	 * @return key
	 */
	public U getKey() {
		return this.key;
	}
	
	
	/**
	 * Returns the value of this pair
	 * @return value
	 */
	public Y getValue() {
		return this.value;
	}
	
	
	/**
	 * Sets the key in this pair to a different value
	 * @param key new key value
	 */
	public void setKey(U key) {
		this.key = key;
	}
	
	
	/**
	 * Sets the value in this pair to a different value
	 * @param value new value for the pair
	 */
	public void setValue(Y value) {
		this.value = value;
	}
	
	
	/**
	 * Returns a new pair in which the values are swapped.
	 * The reference to objects is the same
	 * @return a new pair in which the values are swapped
	 */
	public Pair<Y,U> swap() {
		return new Pair<Y,U>(value, key);
	}
	
	
	@Override
	public boolean equals(Object pair) {
		if(!(pair instanceof Pair<?,?>)) {
			return false;
		}
		
		return ((Pair<?,?>)pair).key.equals(this.key);
	}
	
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
