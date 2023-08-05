package Launcher.base;

import Launcher.RunProc;
import UIL.Lang;
import UIL.LangItem;
import UIL.base.IImage;

public interface IProfile {
    LangItem LANG_REMOVE = Lang.get("launcher.remove");

    String toString();

    default boolean isCompatible(final IAccount account) { return true; }
    default IImage getIcon() { return null; }

    void open(final IEditorContext context);

    void preLaunch(final RunProc configuration);
    void launch(final RunProc configuration);
}