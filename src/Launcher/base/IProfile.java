package Launcher.base;

import Launcher.RunProc;
import UIL.Lang;
import UIL.LangItem;
import UIL.base.IImage;

import java.io.File;

public interface IProfile {
    LangItem LANG_REMOVE = Lang.get("launcher.remove");

    String toString();

    /**
     * @since FlashLauncher 0.2.5
     */
    default String getID() { return toString(); }

    default boolean isCompatible(final IAccount account) { return true; }
    default IImage getIcon() { return null; }

    /**
     * @since FlashLauncher 0.2.2
     */
    default File home(final IAccount account) { return null; }

    void open(final IEditorContext context);

    default LaunchListener init(final RunProc configuration) { return null; }
}