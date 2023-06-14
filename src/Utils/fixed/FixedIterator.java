package Utils.fixed;

import java.util.Iterator;

public class FixedIterator<E> implements Iterator<E> {
    private final E[] a;
    private int i = 0;

    public FixedIterator(E[] array) { a = array; }

    @Override public boolean hasNext() { return a.length > i; }
    @Override public E next() { return a[i++]; }
}
