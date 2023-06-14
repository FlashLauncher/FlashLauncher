package Utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {
    private final K k;
    private V v;

    @SuppressWarnings("unused")
    public Pair(final K key) { k = key; }
    public Pair(final K key, final V value) { k = key; v = value; }

    @SuppressWarnings("unchecked")
    public static <K, V> Pair<K, V>[] make(K[] keys, V[] values) {
        final Pair<K, V>[] l = new Pair[keys.length];
        for (int i = 0; i < keys.length; i++)
            l[i] = new Pair<>(keys[i], values[i]);
        return l;
    }

    @Override public K getKey() { return k; }
    @Override public V getValue() { return v; }

    @Override
    public V setValue(final V value) {
        synchronized (this) {
            final V o = v;
            v = value;
            return o;
        }
    }
}
