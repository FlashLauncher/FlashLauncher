package Launcher;

import UIL.base.IComponent;
import UIL.base.IContainer;
import UIL.base.IImage;

public class FLMenuItemEvent {
    public final FlashLauncher launcher;
    public final IContainer container;
    public final IImage icon;

    FLMenuItemEvent(final FlashLauncher l, final IContainer c, final IImage icon) {
        launcher = l;
        container = c;
        this.icon = icon;
    }

    public int width() { return container.width(); }
    public int height() { return container.height(); }
    public IComponent[] childs() { return container.childs(); }

    public FLMenuItemEvent add(final IComponent... components) { container.add(components); return this; }
    public FLMenuItemEvent add(final IComponent component) { container.add(component); return this; }
    public FLMenuItemEvent clear() { container.clear(); return this; }
    public FLMenuItemEvent update() { container.update(); return this; }
}