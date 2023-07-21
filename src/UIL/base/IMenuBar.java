package UIL.base;

import Utils.RRunnable;

public interface IMenuBar extends IContainer {
    IMenuBar add(final String id, final IImage icon, final Object text, final Runnable action);
    IMenuBar add(final String id, final IImage icon, final Runnable action);
    IMenuBar addEnd(final String id, final IImage icon, final Object text, final Runnable action);
    IMenuBar addEnd(final String id, final IImage icon, final Runnable action);
    IMenuBar select(final String id);
    IMenuBar subSelect(final boolean sub);
    IMenuBar clearTop();

    default IMenuBar onChange(final RRunnable<Boolean> runnable) { return this; }
    default IMenuBar offChange(final RRunnable<Boolean> runnable) { return this; }
    default IMenuBar changed() { return this; }

    // IContainer
    @Override IMenuBar add(final IComponent component);
    @Override IMenuBar add(final IComponent... components);
    @Override IMenuBar remove(final IComponent component);
    @Override IMenuBar clear();

    // IComponent
    @Override IMenuBar size(final int width, final int height);
    @Override IMenuBar pos(final int x, final int y);
    @Override IMenuBar visible(final boolean visible);
    @Override IMenuBar focus();
    @Override default IMenuBar borderRadius(final int borderRadius) { return this; }
    @Override default IMenuBar background(final IColor bg) { return this; }
    @Override default IMenuBar foreground(final IColor fg) { return this; }
    @Override default IMenuBar grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IMenuBar update() { return this; }


}
