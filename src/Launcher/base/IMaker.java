package Launcher.base;

import UIL.base.IImage;

public interface IMaker<T> {
    String toString();
    IImage getIcon();

    void make(final IMakerContext<T> maker);
}