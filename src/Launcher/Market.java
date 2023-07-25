package Launcher;

import UIL.Lang;
import UIL.base.IImage;

public abstract class Market {
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

    public abstract void checkForUpdates(final Meta... items);

    public abstract Meta[] find(final String query);
}