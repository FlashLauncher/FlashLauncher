package Launcher.base;

import UIL.base.IComponent;
import UIL.base.IContainer;

public interface IEditorContext {
    int width();
    int height();

    IEditorContext add(final IComponent component);
    IEditorContext add(final IComponent... components);

    IContainer getContainer();
    void close();
    boolean isClosed();
    void onClose(final Runnable runnable);
}
