package Launcher.base;

import Launcher.RunProc;
import UIL.base.IImage;

public interface IAccount {
    String toString();

    default boolean isCompatible(final IProfile profile) { return true; }
    default IImage getIcon() { return null; }

    void preLaunch(final RunProc configuration);
    void launch(final RunProc configuration);
}
