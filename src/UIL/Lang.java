package UIL;

import Utils.IniGroup;
import Utils.ListMap;

import java.util.ArrayList;
import java.util.Collections;

public class Lang {
    private static final ArrayList<IniGroup> languages = new ArrayList<>();
    private static final ListMap<String, LangItem> items = new ListMap<>();

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

    public static void add(final IniGroup... langs) { synchronized (languages) { Collections.addAll(languages, langs); } }
    public static boolean remove(final IniGroup lang) { synchronized (languages) { return languages.remove(lang); } }

    public static void update() {
        synchronized (languages) {
            synchronized (items) {
                final ArrayList<String> keys = new ArrayList<>();
                for (int i = languages.size() - 1; i >= 0; i--) {
                    final IniGroup g = languages.get(i);
                    for (final String k : g.keys())
                        if (!keys.contains(k)) {
                            keys.add(k);
                            final LangItem item = items.get(k);
                            if (item == null)
                                items.put(k, new LangItem(g.getAsString(k)));
                            else
                                item.value = g.getAsString(k);
                        }
                }
            }
        }
    }
}
