package UIL;

import Utils.IniGroup;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Lang {
    private static final HashMap<String, LangItem> items = new HashMap<>();

    public static LangItem get(String key) {
        synchronized (items) {
            LangItem i = items.get(key);
            if (i == null) {
                i = new LangItem();
                i.value = key;
                items.put(key, i);
            }
            return i;
        }
    }

    public static void apply(final IniGroup lang) {
        synchronized (items) {
            for (final String k : lang.keys())
                get(k).value = lang.getAsString(k);
        }
    }
}
