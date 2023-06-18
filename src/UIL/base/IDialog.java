package UIL.base;

import UIL.UI;
import Utils.Runnable1arg;

public interface IDialog extends IFrame {
    // IFrame
    @Override IDialog icon(IImage icon);
    @Override IDialog resizable(boolean resizable);

    @Override
    default IDialog icon(String path) throws Exception {
        icon(UI.image(path));
        return this;
    }

    @Override IDialog center(IComponent component);

    @Override IDialog onClose(final Runnable1arg<IFrame> listener);

    @Override default IDialog dispose() { return this; }

    // IContainer
    @Override IDialog add(IComponent component);
    @Override IDialog remove(IComponent component);
    @Override IDialog clear();

    // IComponent
    @Override IDialog size(int width, int height);
    @Override IDialog pos(int x, int y);
    @Override IDialog visible(boolean visible);
    @Override IDialog focus();
    @Override default IDialog borderRadius(int borderRadius) { return this; }
    @Override default IDialog background(IColor bg) { return this; }
    @Override default IDialog foreground(IColor fg) { return this; }
    @Override default IDialog grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IDialog on(String name, Runnable runnable) { return this; }
}
