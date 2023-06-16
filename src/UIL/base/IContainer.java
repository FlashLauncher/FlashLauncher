package UIL.base;

import Utils.RRunnable;

public interface IContainer extends IComponent {
    IContainer add(IComponent component);
    IContainer remove(IComponent component);
    IComponent[] childs();
    IContainer clear();

    // IComponent
    @Override IContainer size(int width, int height);
    @Override IContainer pos(int x, int y);
    @Override IContainer visible(boolean visible);
    @Override IContainer focus();
    @Override default IContainer borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IContainer borderRadius(int borderRadius) { return this; }
    @Override default IContainer background(IColor bg) { return this; }
    @Override default IContainer foreground(IColor fg) { return this; }
    @Override default IContainer grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
}
