package net.karanteeni.core.data;


public class Entry<U extends Comparable<U>, Y> implements Comparable<Entry<U, Y>> {
	private U key;
	private Y value;
	
	
	public Entry(U key, Y value) {
		this.key = key;
		this.value = value;
	}
	
	
	public U getKey() {
		return this.key;
	}
	
	
	public Y getValue() {
		return this.value;
	}
	
	
	public Y setValue(Y value) {
		Y ret = this.value;
		this.value = value;
		return ret;
	}
	
	
	@Override
	public int compareTo(Entry<U, Y> entry) {
		return key.compareTo(entry.key);
	}
}
