package Launcher.base;

import Launcher.RunProc;
import UIL.Lang;
import UIL.LangItem;
import UIL.base.IImage;

public interface IAccount {
    LangItem LANG_REMOVE = Lang.get("launcher.remove");

    String toString();

    default boolean isCompatible(final IProfile profile) { return true; }
    default IImage getIcon() { return null; }

    void open(final IEditorContext context);

    void preLaunch(final RunProc configuration);
    void launch(final RunProc configuration);
}