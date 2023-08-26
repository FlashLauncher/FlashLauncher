package Launcher;

import UIL.base.IImage;

public abstract class FLMenuItemListener {
    public final String id;
    public final IImage icon;
    public final Object text;

    public FLMenuItemListener(final String id, final IImage icon, final Object text) {
        this.id = id;
        this.icon = icon;
        this.text = text;
    }

    public abstract void onOpen(final FLMenuItemEvent event);
}
