package Utils.fixed;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class FixedSet<E> implements Set<E> {
    private final E[] a;

    public FixedSet(final E[] array) { a = array; }

    @Override public int size() { return a.length; }
    @Override public boolean isEmpty() { return a.length == 0; }

    @Override
    public boolean contains(final Object o) {
        for (final E e : a)
            if (e.equals(o))
                return true;
        return false;
    }

    @Override public Iterator<E> iterator() { return new FixedIterator<>(a); }
    @Override public Object[] toArray() { return a; }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] arr) throws ArrayStoreException, NullPointerException {
        if (arr.length < a.length)
            return (T[]) Arrays.copyOf(a, a.length, arr.getClass());
        System.arraycopy(a, 0, arr, 0, a.length);
        return arr;
    }

    @Override public boolean add(E e) { return false; }
    @Override public boolean remove(Object o) { return false; }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object i : c)
            if (!contains(i))
                return false;
        return true;
    }

    @Override public boolean addAll(Collection<? extends E> c) { return false; }
    @Override public boolean retainAll(Collection<?> c) { return false; }
    @Override public boolean removeAll(Collection<?> c) { return false; }
    @Override public void clear() {}
}
