package Utils;

import java.util.*;

public class ListMap<K extends Object,V extends Object> implements Map<K,V> {
    private final ArrayList<Pair<K, V>> entries = new ArrayList<>();

    @Override public int size() { return entries.size(); }
    @Override public boolean isEmpty() { return size() == 0; }

    @Override
    public boolean containsKey(Object key) {
        for (Pair<K, V> e : entries)
            if (e.getKey().equals(key))
                return true;
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        for (final Pair<K, V> e : entries)
            if (e.getValue().equals(value))
                return true;
        return false;
    }

    @Override
    public V get(final Object key) {
        for (final Pair<K, V> e : entries)
            if (e.getKey().equals(key))
                return e.getValue();
        return null;
    }

    @Override
    public V put(final K key, final V value) {
        for (final Pair<K, V> s : entries)
            if (s.getKey().equals(key))
                return s.setValue(value);
        entries.add(new Pair<>(key, value));
        return null;
    }

    @Override
    public V remove(Object key) {
        final int s = size();
        for (int i = 0; i < s; i++) {
            final Pair<K, V> e = entries.get(i);
            if (e.getKey().equals(key)) {
                entries.remove(e);
                return e.getValue();
            }
        }
        return null;
    }

    @Override
    public void putAll(final Map m) {
        if (m == null) return;
        Set<Entry<K, V>> s = m.entrySet();
        for (Entry<K, V> e : s)
            put(e.getKey(), e.getValue());
    }

    @Override public void clear() { entries.clear(); }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new ListSet<>();
        for (Pair<K, V> e : entries)
            keys.add(e.getKey());
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<V> values = new ListSet<>();
        for (final Pair<K, V> e : entries)
            values.add(e.getValue());
        return values;
    }

    @Override public Set<Entry<K, V>> entrySet() { return new ListSet<Entry<K, V>>(entries.toArray(new Entry[0])); }
}
