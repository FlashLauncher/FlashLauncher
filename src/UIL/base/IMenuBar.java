package UIL.base;

import Utils.RRunnable;

public interface IMenuBar extends IContainer {
    IMenuBar add(IImage icon, String id, Object text, Runnable action);
    IMenuBar add(IImage icon, String id, Runnable action);
    IMenuBar addEnd(IImage icon, String id, Runnable action);
    IMenuBar select(String id);
    IMenuBar subSelect(boolean sub);

    // IContainer
    @Override IMenuBar add(IComponent component);
    @Override IMenuBar remove(IComponent component);
    @Override IMenuBar clear();

    // IComponent
    @Override IMenuBar size(int width, int height);
    @Override IMenuBar pos(int x, int y);
    @Override IMenuBar visible(boolean visible);
    @Override IMenuBar focus();
    @Override default IMenuBar borderRadius(int borderRadius) { return this; }
    @Override default IMenuBar background(IColor bg) { return this; }
    @Override default IMenuBar foreground(IColor fg) { return this; }
    @Override default IMenuBar grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IMenuBar on(final String name, final Runnable runnable) { return this; }
    @Override default IMenuBar off(final Runnable runnable) { return this; }
    default IMenuBar onChange(final RRunnable<Boolean> runnable) { return this; }
    default IMenuBar offChange(final RRunnable<Boolean> runnable) { return this; }
    default IMenuBar changed() { return this; }
}
