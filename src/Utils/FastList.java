package Utils;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @since FlashLauncher 0.2.6
 */
public class FastList<E> extends AbstractList<E> {
    public final Object writeLocker = new Object();
    private final SyncVar<State<E>> s;

    /**
     * @deprecated FlashLauncher 0.2.7
     * <p> Use {@link FastList(Class)} instead.</p>
     */
    @Deprecated
    public FastList() {
        throw new RuntimeException("Deprecated!");
    }

    /**
     * @since FlashLauncher 0.2.7
     */
    public FastList(final Class<E> type) { s = new SyncVar<>(new EmptyState<>(type)); }

    /**
     * @since FlashLauncher 0.2.6
     */
    public FastList(final E[] array) {
        s = new SyncVar<>(
                array.length > 0 ?
                    new ArrayState<>(Arrays.copyOf(array, array.length)) :
                        new EmptyState<>((Class<E>) array.getClass().getComponentType())
        );
    }

    public State<E> getState() { return s.get(); }
    public void setState(final State<E> newState) { synchronized (writeLocker) { s.set(newState == null ? new EmptyState<>(s.get().type) : newState); } }
    public void optimize() { synchronized (writeLocker) { s.set(s.get().optimize()); } }

    @Override public boolean isEmpty() { return s.get().isEmpty(); }
    @Override public int size() { return s.get().size(); }
    @Override public E get(final int index) { return s.get().get(index); }
    @Override public Iterator<E> iterator() { return s.get().iterator(); }

    @Override public void add(final int index, final E element) { synchronized (writeLocker) { s.set(new AddState<>(s.get(), index, element)); } }

    @Override public E[] toArray() { return s.get().toArray(); }

    public static class State<E> implements Iterable<E> {
        final Class<E> type;

        public State(final Class<E> type) { this.type = type; }

        public boolean isEmpty() { return true; }
        public int size() { return 0; }
        public E get(final int index) { throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0"); }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                @Override public boolean hasNext() { return false; }
                @Override public E next() { throw new NoSuchElementException(); }
            };
        }

        public State<E> optimize() {
            return isEmpty() ? new EmptyState<>(type) : new ArrayState<>(this);
        }

        public E[] toArray() {
            final E[] l = (E[]) Array.newInstance(type, size());
            for (int i = 0; i < l.length; i++)
                l[i] = get(i);
            return l;
        }
    }

    public static class EmptyState<E> extends State<E> {
        public EmptyState(final Class<E> type) { super(type); }
        @Override public State<E> optimize() { return this; }
    }

    public static class ArrayState<E> extends State<E> {
        public final E[] elements;

        public ArrayState(final E[] array) {
            super((Class<E>) array.getClass().getComponentType());
            elements = array;
        }

        public ArrayState(final State<E> state) { this(state.toArray()); }

        @Override public boolean isEmpty() { return elements.length == 0; }
        @Override public int size() { return elements.length; }

        @Override public E get(final int index) {
            if (index >= elements.length)
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.length);
            return elements[index];
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private int i = 0;

                @Override public boolean hasNext() { return i < elements.length; }

                @Override
                public E next() {
                    if (i >= elements.length)
                        throw new NoSuchElementException();
                    return elements[i++];
                }
            };
        }

        @Override public State<E> optimize() { return this; }
    }

    public static class AddState<E> extends State<E> {
        public final State<E> parent;
        public final int index, size;
        public final E element;

        public AddState(final State<E> parent, final int index, final E element) {
            super(parent.type);
            this.parent = parent;
            this.index = index;
            this.element = element;
            this.size = parent.size() + 1;
        }

        @Override public boolean isEmpty() { return false; }
        @Override public int size() { return size; }

        @Override public E get(final int index) {
            if (this.index > index)
                return parent.get(index);
            if (index >= size)
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            if (this.index < index)
                return parent.get(index + 1);
            return element;
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private int i = 0;

                @Override public boolean hasNext() { return i < size; }

                @Override
                public E next() {
                    if (i < size)
                        throw new NoSuchElementException();
                    if (index > i)
                        return parent.get(i++);
                    if (index < i)
                        return parent.get((i++) + 1);
                    i++;
                    return element;
                }
            };
        }
    }

    public static class RemoveByIndexState<E> extends State<E> {
        public final State<E> parent;
        public final int index, size;

        public RemoveByIndexState(final State<E> parent, final int index) {
            super(parent.type);
            this.parent = parent;
            this.index = index;
            this.size = parent.size() - 1;
        }

        @Override public boolean isEmpty() { return size == 0; }
        @Override public int size() { return size; }

        @Override
        public E get(final int index) {
            if (this.index > index)
                return parent.get(index);
            if (index >= size)
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            return parent.get(index - 1);
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private int i = 0;

                @Override public boolean hasNext() { return i < size; }

                @Override
                public E next() {
                    if (i >= size)
                        throw new NoSuchElementException();
                    return parent.get(i++);
                }
            };
        }
    }
}