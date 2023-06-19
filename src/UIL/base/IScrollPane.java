package UIL.base;

public interface IScrollPane extends IContainer {
    IScrollPane content(final IContainer container);

    // IContainer
    IScrollPane add(final IComponent component);
    IScrollPane add(final IComponent... components);
    IScrollPane remove(final IComponent component);

    // IComponent
    @Override IScrollPane size(final int width, final int height);
    @Override IScrollPane pos(final int x, final int y);
    @Override IScrollPane visible(final boolean visible);
    @Override IScrollPane focus();
    @Override default IScrollPane borderRadius(final int borderRadius) { return this; }
    @Override default IScrollPane background(final IColor bg) { return this; }
    @Override default IScrollPane update() { return this; }
}
