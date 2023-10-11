package Utils;

import java.util.Map;
import java.util.Set;

public class IniGroup {
    public static final String[] COMMENTS = new String[] { ";", "#" };
    public static final char[] EQUALS = new char[] { '=', ':' };

    private final ListMap<String, Object> values = new ListMap<>();

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

    public boolean has(final String k) { return values.containsKey(k); }
    public Object get(final String k) { return values.get(k); }
    public Set<String> keys() { return values.keySet(); }
    public Set<Map.Entry<String, Object>> entrySet() { return values.entrySet(); }

    public IniGroup newGroup(final String k) {
        return new IniGroup() {{
            values.put(k, this);
        }};
    }

    public IniGroup getAsGroup(final String k) {
        final Object o = get(k);
        return o != null ? (IniGroup) o : null;
    }

    public String getAsString(final String k) {
        final Object o = get(k);
        return o == null ? null : o.toString();
    }

    public int getAsInt(final String k) { return Integer.parseInt(getAsString(k)); }
    public float getAsFloat(final String k) { return Float.parseFloat(getAsString(k)); }
    public void put(final String k, final String v) { values.put(k, v); }
    public void remove(final String k) { values.remove(k); }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        final Set<Map.Entry<String, Object>> el = values.entrySet();
        for (final Map.Entry<String, Object> e : el) {
            final Object v = e.getValue();
            if (v != null && !(v instanceof IniGroup))
                b.append(e.getKey()).append('=').append(v).append('\n');
        }
        for (final Map.Entry<String, Object> e : el) {
            final Object v = e.getValue();
            final Set<Map.Entry<String, Object>> l;
            if (!(v instanceof IniGroup) || (l = ((IniGroup) v).values.entrySet()).isEmpty())
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
}
