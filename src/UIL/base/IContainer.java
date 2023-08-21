package UIL.base;

import Utils.RRunnable;

public interface IContainer extends IComponent {
    IContainer add(final IComponent component);
    IContainer add(final IComponent... components);

    IContainer remove(final IComponent component);
    IComponent[] childs();
    IContainer clear();

    // IComponent
    @Override IContainer size(final int width, final int height);
    @Override IContainer pos(final int x, final int y);
    @Override IContainer visible(final boolean visible);
    @Override IContainer focus();
    @Override default IContainer borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IContainer background(final IColor bg) { return this; }
    @Override default IContainer foreground(final IColor fg) { return this; }
    @Override default IContainer grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IContainer update() { return this; }
}
