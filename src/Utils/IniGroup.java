package Utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @since FlashLauncher 0.2.6
 */
public class IniGroup extends ListMap<String, Object> {
    public static final String[] COMMENTS = new String[] { ";", "#" };
    public static final char[] EQUALS = new char[] { '=', ':' };

    public IniGroup() {}
    public IniGroup(final String data, final boolean allowNSubGroups) {
        IniGroup c = this;
        int i;
        for (final String line : data.replaceAll("\r", "").split("\n")) {
            if (line.startsWith("[") && (i = line.indexOf("]")) > -1) {
                final String l = Core.removeStart(line.substring(1, i), " ", "\t");
                if (l.isEmpty()) {
                    c = this;
                    continue;
                }
                if (allowNSubGroups) {
                    final String[] sl = l.split("\\.");
                    if (!sl[0].isEmpty())
                        c = this;
                    for (final String s : sl)
                        if (s.isEmpty())
                            c = c.newGroup(s);
                    continue;
                }
                c = newGroup(l);
                continue;
            }
            if (Core.startsWith(line, COMMENTS))
                continue;
            if ((i = Core.minIndexOf(line, EQUALS)) > 0) {
                final String key = line.substring(0, i), value = line.substring(i + 1);
                c.put(key.endsWith(" ") ? key.substring(0, i - 1) : key, value.startsWith(" ") ? value.substring(1) : value);
            }
        }
    }

    /**
     * @since FlashLauncher 0.2.6
     */
    public IniGroup(final Map<String, Object> map) { super(map); }

    public IniGroup newGroup(final String k) {
        final IniGroup g = new IniGroup();
        put(k, g);
        return g;
    }

    public IniGroup getAsGroup(final String k) {
        final Object o = get(k);
        return o != null ? (IniGroup) o : null;
    }

    public IniGroup group(final String k) {
        IniGroup g = getAsGroup(k);
        if (g == null)
            put(k, g = new IniGroup());
        return g;
    }

    public String getAsString(final String k) {
        final Object o = get(k);
        return o == null ? null : o.toString();
    }

    public int getAsInt(final String k) { return Integer.parseInt(getAsString(k)); }
    public float getAsFloat(final String k) { return Float.parseFloat(getAsString(k)); }
    public boolean getAsBool(final String k) { return Boolean.parseBoolean(k); }
    public boolean getAsBool(final String k, final boolean defValue) {
        String v = getAsString(k);
        if (v == null)
            return defValue;
        v = v.toLowerCase();
        return v.equals("1") || v.equals("true") || (!v.equals("0") && !v.equals("false") && defValue);
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        final Set<Map.Entry<String, Object>> el = entrySet();
        for (final Map.Entry<String, Object> e : el) {
            final Object v = e.getValue();
            if (v != null && !(v instanceof IniGroup))
                b.append(e.getKey()).append('=').append(v).append('\n');
        }
        for (final Map.Entry<String, Object> e : el) {
            final Object v = e.getValue();
            final Set<Map.Entry<String, Object>> l;
            if (!(v instanceof IniGroup) || (l = ((IniGroup) v).entrySet()).isEmpty())
                continue;
            boolean add = true;
            for (final Map.Entry<String, Object> en : l)
                if (en.getValue() != null) {
                    if (add) {
                        add = false;
                        b.append('[').append(e.getKey()).append("]\n");
                    }
                    b.append(en.getKey()).append('=').append(en.getValue()).append('\n');
                }
        }
        return b.length() > 0 ? b.deleteCharAt(b.length() - 1).toString() : "";
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public int size() {
        return super.size();
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public boolean containsKey(final Object key) {
        return super.containsKey(key);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public boolean containsValue(final Object value) {
        return super.containsValue(value);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object get(final Object key) {
        return super.get(key);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object put(final String key, final Object value) {
        return super.put(key, value);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object remove(final Object key) {
        return super.remove(key);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public void putAll(final Map m) {
        super.putAll(m);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Set<String> keySet() {
        return super.keySet();
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Collection<Object> values() {
        return super.values();
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return super.entrySet();
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object getOrDefault(final Object key, final Object defaultValue) {
        return super.getOrDefault(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public void forEach(final BiConsumer<? super String, ? super Object> action) {
        super.forEach(action);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public void replaceAll(final BiFunction<? super String, ? super Object, ?> function) {
        super.replaceAll(function);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object putIfAbsent(final String key, final Object value) {
        return super.putIfAbsent(key, value);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public boolean remove(final Object key, final Object value) {
        return super.remove(key, value);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public boolean replace(final String key, final Object oldValue, final Object newValue) {
        return super.replace(key, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object replace(final String key, final Object value) {
        return super.replace(key, value);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object computeIfAbsent(final String key, final Function<? super String, ?> mappingFunction) {
        return super.computeIfAbsent(key, mappingFunction);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override
    public Object computeIfPresent(final String key, final BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return super.computeIfPresent(key, remappingFunction);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override public Object compute(final String key, final BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return super.compute(key, remappingFunction);
    }

    /**
     * {@inheritDoc}
     * @since FlashLauncher 0.2.6
     */
    @Override public Object merge(final String key, final Object value, final BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return super.merge(key, value, remappingFunction);
    }

    /**
     * @deprecated FlashLauncher 0.2.6
     * <p> Use {@link IniGroup#containsKey(Object)} instead.
     */
    public boolean has(final String k) { return containsKey(k); }

    /**
     * @deprecated FlashLauncher 0.2.6
     * <p> Use {@link IniGroup#keySet()} instead.
     */
    public Set<String> keys() { return keySet(); }
}
