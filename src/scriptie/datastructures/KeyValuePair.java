package scriptie.datastructures;

public class KeyValuePair<K,V> {
    public K key;
	public V value;
	
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}
}