package UIL.base;

import Utils.RRunnable;

public interface IComponent {
    boolean visible();
    boolean isFocused();

    int width();
    int height();

    IComponent size(final int width, final int height);
    IComponent pos(final int x, final int y);
    IComponent visible(final boolean visible);
    IComponent focus();
    Object getComponent();

    default IComponent borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    default IComponent background(final IColor bg) { return this; }
    default IComponent foreground(final IColor fg) { return this; }
    default IComponent grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    default IComponent update() { return this; }


}
