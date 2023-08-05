package Launcher.base;

import UIL.base.IContainer;

public interface IMakerContext<T> {
    IContainer getContainer();
    T end(T result);

    boolean isFinished();

    void onCancel(final Runnable runnable);
}
