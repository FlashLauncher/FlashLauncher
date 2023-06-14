package Utils.fixed;

import java.util.Map;

public class FixedEntry<K, V> implements Map.Entry<K, V> {
    private final K k;
    private final V v;

    public FixedEntry(final K key, final V value) { k = key; v = value; }
    @Override public K getKey() { return k; }
    @Override public V getValue() { return v; }
    @Override public V setValue(V value) { return null; }
}
