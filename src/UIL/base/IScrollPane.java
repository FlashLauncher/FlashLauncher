package UIL.base;

import Utils.RRunnable;

public interface IScrollPane extends IContainer {
    IScrollPane content(final IContainer container);
    IContainer content();


    // IContainer
    IContainer add(final IComponent component);
    IContainer add(final IComponent... components);
    IContainer remove(final IComponent component);
    IComponent[] childs();
    IContainer clear();

    // IComponent
    @Override IScrollPane size(final int width, final int height);
    @Override IScrollPane pos(final int x, final int y);
    @Override IScrollPane visible(final boolean visible);
    @Override IScrollPane focus();
    @Override default IScrollPane borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IScrollPane background(final IColor bg) { return this; }
    @Override default IScrollPane foreground(final IColor fg) { return this; }
    @Override default IScrollPane grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IScrollPane update() { return this; }
}
