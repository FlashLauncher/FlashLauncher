package Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IniGroup {
    public static final String[] comments = new String[] { ";", "#" };
    public static final char[] eq = new char[] { '=', ':' };

    private final HashMap<String, Object> values = new HashMap<>();

    public IniGroup() {}
    public IniGroup(final String data, final boolean allowNSubGroups) {
        IniGroup c = this;
        for (final String line : data.replaceAll("\r", "").split("\n")) {
            {
                int i;
                if (line.startsWith("[") && (i = line.indexOf("]")) > -1) {
                    if (allowNSubGroups) {
                        String[] l = line.substring(1, i).split("\\.");
                        if (l.length == 1 && l[0].length() == 0) {
                            c = this;
                            continue;
                        }
                        if (l[0].length() != 0)
                            c = this;
                        for (final String m : l)
                            c = c.newGroup(m);
                        continue;
                    }
                    if (line.length() == 2) {
                        c = this;
                        continue;
                    }
                    c = c.newGroup(line.substring(1, i));
                    continue;
                }
            }
            if (Core.startsWith(line, comments))
                continue;
            final int i = Core.minIndexOf(line, eq);
            if (i > 0) {
                String key = line.substring(0, i), value = line.substring(i + 1);
                if (key.endsWith(" "))
                    key = key.substring(0, key.length() - 1);
                if (value.startsWith(" "))
                    value = value.substring(1);
                c.put(key, value);
            }
        }
    }

    public boolean has(final String k) { return values.containsKey(k); }
    public Object get(final String k) { return values.get(k); }
    public Set<String> keys() { return values.keySet(); }
    public Set<Map.Entry<String, Object>> entrySet() { return values.entrySet(); }

    public IniGroup newGroup(String k) {
        IniGroup g = new IniGroup();
        values.put(k, g);
        return g;
    }

    public IniGroup getAsGroup(String k) {
        Object o = get(k);
        return o != null ? (IniGroup) o : null;
    }

    public String getAsString(String k) {
        Object o = get(k);
        return o != null ? (String) o : null;
    }

    public int getAsInt(String k) { return Integer.parseInt(getAsString(k)); }
    public float getAsFloat(String k) { return Float.parseFloat(getAsString(k)); }
    public void put(String k, String v) { values.put(k, v); }

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
            if (!(v instanceof IniGroup) || (l = ((IniGroup) v).values.entrySet()).size() == 0)
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
