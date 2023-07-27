package Launcher.base;

import Launcher.RunProc;
import UIL.base.IImage;

public interface IProfile {
    String toString();

    default boolean isCompatible(final IAccount account) { return true; }
    default IImage getIcon() { return null; }

    void preLaunch(final RunProc configuration);
    void launch(final RunProc configuration);
}