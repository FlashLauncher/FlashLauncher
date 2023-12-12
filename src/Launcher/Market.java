package Launcher;

import UIL.Lang;
import UIL.base.IImage;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Market {
    public static final int MENU_ICON_SIZE = 118;

    final ConcurrentLinkedQueue<Object> cl = new ConcurrentLinkedQueue<>();

    private final String id;
    private final Object n;
    private final IImage i;

    public Market(final String id, final IImage icon) {
        if (id == null)
            throw new RuntimeException("ID is empty!");
        this.id = id;
        n = Lang.get("markets." + id + ".name");
        i = icon;
    }

    public Market(final String id, final Object name, final IImage icon) {
        if (id == null)
            throw new RuntimeException("ID is empty!");
        this.id = id;
        n = name;
        i = icon;
    }

    public final String getID() { return id; }
    public final IImage getIcon() { return i; }
    public Object getName() { return n; }

    public final void addCategory(final Object category) {
        cl.add(category);
        synchronized (FLCore.markets) {
            if (FLCore.markets.contains(this))
                FLCore.markets.notifyAll();
        }
    }

    public final void removeCategory(final Object category) {
        cl.remove(category);
        synchronized (FLCore.markets) {
            if (FLCore.markets.contains(this))
                FLCore.markets.notifyAll();
        }
    }
    public final void clearCategories() {
        cl.clear();
        synchronized (FLCore.markets) {
            if (FLCore.markets.contains(this))
                FLCore.markets.notifyAll();
        }
    }

    public final Object[] getCategories() { return cl.toArray(); }

    public abstract void checkForUpdates(final Meta... items);

    public abstract Meta[] find(final String query);
}