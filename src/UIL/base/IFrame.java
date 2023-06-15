package UIL.base;

import UIL.UI;

public interface IFrame extends IContainer {
    IFrame icon(IImage icon);
    IFrame resizable(boolean resizable);

    default IFrame icon(String path) throws Exception {
        icon(UI.image(path));
        return this;
    }

    IFrame center(IComponent component);

    default IFrame dispose() { return this; }

    // IContainer
    @Override IFrame add(IComponent component);
    @Override IFrame remove(IComponent component);
    @Override IFrame clear();

    // IComponent
    @Override IFrame size(int width, int height);
    @Override IFrame pos(int x, int y);
    @Override IFrame visible(boolean visible);
    @Override IFrame focus();
    @Override default IFrame borderRadius(int borderRadius) { return this; }
    @Override default IFrame background(IColor bg) { return this; }
    @Override default IFrame foreground(IColor fg) { return this; }
    @Override default IFrame grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IFrame on(String name, Runnable runnable) { return this; }
}
