package UIL.base;

public interface IScrollPane extends IContainer {
    IScrollPane update();
    IScrollPane content(IContainer container);

    // IContainer
    IScrollPane add(IComponent component);
    IScrollPane remove(IComponent component);

    // IComponent
    @Override IScrollPane size(int width, int height);
    @Override IScrollPane pos(int x, int y);
    @Override IScrollPane visible(boolean visible);
    @Override IScrollPane focus();
    @Override default IScrollPane borderRadius(int borderRadius) { return this; }
    @Override default IScrollPane background(IColor bg) { return this; }
}
