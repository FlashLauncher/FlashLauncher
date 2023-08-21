package Utils.json;

import java.util.*;

public class JsonList extends JsonElement implements List<JsonElement> {
    private final ArrayList<JsonElement> items = new ArrayList<>();

    @Override public int size() { return items.size(); }
    @Override public boolean isEmpty() { return items.isEmpty(); }
    @Override public boolean contains(final Object o) { return items.contains(o); }
    @Override public boolean add(final JsonElement o) { return items.add(o); }
    @Override public boolean remove(final Object o) { return items.remove(o); }
    @Override public boolean containsAll(final Collection<?> c) { return items.containsAll(c); }
    @Override public boolean addAll(final Collection<? extends JsonElement> c) { return items.addAll(c); }
    @Override public boolean addAll(final int index, final Collection<? extends JsonElement> c) { return items.addAll(index, c); }
    @Override public boolean removeAll(final Collection<?> c) { return items.removeAll(c); }
    @Override public boolean retainAll(final Collection<?> c) { return items.retainAll(c); }
    @Override public void clear() { items.clear(); }
    @Override public JsonElement get(final int index) { return items.get(index); }
    @Override public JsonElement set(final int index, final JsonElement element) { return items.set(index, element); }
    @Override public void add(final int index, final JsonElement element) { items.add(index, element); }
    @Override public JsonElement remove(final int index) { return items.remove(index); }
    @Override public int indexOf(final Object o) { return items.indexOf(o); }
    @Override public int lastIndexOf(final Object o) { return items.lastIndexOf(o); }
    @Override public ListIterator<JsonElement> listIterator() { return items.listIterator(); }
    @Override public ListIterator<JsonElement> listIterator(final int index) { return items.listIterator(index); }
    @Override public List<JsonElement> subList(final int fromIndex, final int toIndex) { return items.subList(fromIndex, toIndex); }
    @Override public Iterator<JsonElement> iterator() { return items.iterator(); }
    @Override public Object[] toArray() { return items.toArray(); }
    @Override public <T> T[] toArray(final T[] a) { return items.toArray(a); }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("[");
        boolean f = true;
        for (Object c : items) {
            if (f)
                f = false;
            else
                b.append(",");
            if (c instanceof String)
                b.append("\"").append(c).append("\"");
            else
                b.append(c);
        }
        return b.append("]").toString();
    }
}