package UIL.base;

import Utils.IntRunnable;

public interface IComponent {
    boolean visible();
    boolean isFocused();

    int width();
    int height();

    default int borderRadius() { return 0; }

    IComponent size(final int width, final int height);
    IComponent pos(final int x, final int y);
    IComponent visible(final boolean visible);
    IComponent focus();

    default IComponent borderRadius(final IntRunnable borderRadius) { return this; }
    default IComponent borderRadius(final int borderRadius) { return this; }
    default IComponent background(final IColor bg) { return this; }
    default IComponent foreground(final IColor fg) { return this; }
    default IComponent grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    default IComponent on(final String name, final Runnable runnable) { return this; }
    default IComponent off(final Runnable runnable) { return this; }

    Object getComponent();
}
