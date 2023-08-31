package UIL.base;

import Utils.Runnable1a;

public interface IDialog extends IFrame {
    // IFrame
    @Override IDialog icon(final IImage icon);
    @Override IDialog resizable(final boolean resizable);

    @Override IDialog pack();
    @Override IDialog center(final IComponent component);

    @Override IDialog onClose(final Runnable1a<IFrame> listener);

    @Override default IDialog dispose() { return this; }

    // IContainer
    @Override IDialog add(final IComponent component);
    @Override IDialog add(final IComponent... components);
    @Override IDialog remove(final IComponent component);
    @Override IDialog clear();

    // IComponent
    @Override IDialog size(final int width, final int height);
    @Override IDialog pos(final int x, final int y);
    @Override IDialog visible(final boolean visible);
    @Override IDialog focus();
    @Override default IDialog background(final IColor bg) { return this; }
    @Override default IDialog foreground(final IColor fg) { return this; }
    @Override default IDialog grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IDialog update() { return this; }
}
