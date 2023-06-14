package Utils.fixed;

import java.lang.reflect.*;
import java.util.*;

public class FixedMap<K, V> implements Map<K, V> {
    private final K[] kl;
    private final V[] vl;
    private final FixedEntry<K, V>[] entries;

    @SuppressWarnings("unchecked")
    public FixedMap(final K[] keys, final V[] values) throws RuntimeException {
        if (keys.length != values.length) throw new RuntimeException("Keys Length != Values Length");
        kl = (K[]) Array.newInstance(keys.getClass().getComponentType(), keys.length);
        vl = (V[]) Array.newInstance(values.getClass().getComponentType(), values.length);
        entries = (FixedEntry<K, V>[]) Array.newInstance(FixedEntry.class, kl.length);
        for (int i = 0; i < kl.length; i++) {
            kl[i] = keys[i];
            vl[i] = values[i];
            entries[i] = new FixedEntry<>(kl[i], vl[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public FixedMap(final List<K> keys, final List<V> values) throws RuntimeException {
        if (keys.size() != values.size()) throw new RuntimeException("Keys Length != Values Length");
        kl = (K[]) new Object[keys.size()];
        vl = (V[]) new Object[kl.length];
        entries = (FixedEntry<K, V>[]) Array.newInstance(FixedEntry.class, kl.length);
        for (int i = 0; i < kl.length; i++) {
            kl[i] = keys.get(i);
            vl[i] = values.get(i);
            entries[i] = new FixedEntry<>(kl[i], vl[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public FixedMap(final Map<K, V> map) {
        final K[] lkl = (K[]) map.keySet().toArray();
        kl = (K[]) Array.newInstance(lkl.getClass().getComponentType(), lkl.length);
        final V[] lvl = (V[]) map.values().toArray();
        vl = (V[]) Array.newInstance(lvl.getClass().getComponentType(), lvl.length);
        entries = (FixedEntry<K, V>[]) Array.newInstance(FixedEntry.class, kl.length);
        for (int i = 0; i < kl.length; i++) {
            kl[i] = lkl[i];
            vl[i] = lvl[i];
            entries[i] = new FixedEntry<>(kl[i], vl[i]);
        }
    }

    @Override public int size() { return kl.length; }
    @Override public boolean isEmpty() { return kl.length == 0; }

    @Override
    public boolean containsKey(final Object key) {
        for (final K k : kl)
            if (k == key)
                return true;
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        for (final V v : vl)
            if (v == value)
                return true;
        return false;
    }

    @Override
    public V get(final Object key) {
        for (int i = 0; i < kl.length; i++)
            if (kl[i] == key)
                return vl[i];
        return null;
    }

    @Override public V put(K key, V value) { return null; }
    @Override public V remove(Object key) { return null; }
    @Override public void putAll(Map<? extends K, ? extends V> m) {}
    @Override public void clear() {}
    @Override public Set<K> keySet() { return new FixedSet<>(kl); }
    @Override public Collection<V> values() { return new FixedSet<>(vl); }
    @Override public Set<Entry<K, V>> entrySet() { return new FixedSet<>(entries); }
}
