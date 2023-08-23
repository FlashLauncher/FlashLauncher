package Launcher;

import UIL.base.IImage;

public abstract class FLMenuItemListener {
    public final String id;
    public final IImage icon;
    public final Object text;

    FLMenuItemListener(final String id, final IImage icon, final Object text) {
        this.id = id;
        this.icon = icon;
        this.text = text;
    }

    abstract void onOpen(final FLMenuItemEvent event);
}
