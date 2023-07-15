package Utils;

import java.util.*;

public class ListSet<T> implements Set<T> {
    private final ArrayList<T> l;

    public ListSet() { l = new ArrayList<>(); }
    public ListSet(T[] array) { l = new ArrayList<>(Arrays.asList(array)); }
    public ListSet(List<T> list) { l = new ArrayList<>(list); }

    @Override public int size() { return l.size(); }
    @Override public boolean isEmpty() { return l.isEmpty(); }
    @Override public boolean contains(Object o) { return l.contains(o); }
    @Override public Iterator<T> iterator() { return l.iterator(); }
    @Override public Object[] toArray() { return l.toArray(); }
    @Override public <T1> T1[] toArray(T1[] a) { return l.toArray(a); }
    @Override public boolean add(T t) { return l.add(t); }
    @Override public boolean remove(Object o) { return l.remove(o); }
    @Override public boolean containsAll(Collection<?> c) { return l.containsAll(c); }
    @Override public boolean addAll(Collection<? extends T> c) { return l.addAll(c); }
    @Override public boolean retainAll(Collection<?> c) { return l.retainAll(c); }
    @Override public boolean removeAll(Collection<?> c) { return l.removeAll(c); }
    @Override public void clear() { l.clear(); }
    @Override public String toString() { return l.toString(); }
}
