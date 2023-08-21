package UIL.base;

import Utils.Runnable1arg;

public interface IFrame extends IContainer {
    IFrame icon(final IImage icon);
    IFrame resizable(final boolean resizable);

    IFrame pack();
    IFrame center(final IComponent component);

    IFrame onClose(final Runnable1arg<IFrame> listener);

    default IFrame dispose() { return this; }

    // IContainer
    @Override IFrame add(final IComponent component);
    @Override IFrame add(final IComponent... components);
    @Override IFrame remove(final IComponent component);
    @Override IFrame clear();

    // IComponent
    @Override IFrame size(final int width, final int height);
    @Override IFrame pos(final int x, final int y);
    @Override IFrame visible(final boolean visible);
    @Override IFrame focus();
    @Override default IFrame background(final IColor bg) { return this; }
    @Override default IFrame foreground(final IColor fg) { return this; }
    @Override default IFrame grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IFrame update() { return this; }
}
