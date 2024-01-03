package Launcher.base;

import Launcher.RunProc;
import UIL.Lang;
import UIL.LangItem;
import UIL.base.IImage;

public interface IAccount {
    LangItem LANG_REMOVE = Lang.get("launcher.remove");

    String toString();

    /**
     * @since FlashLauncher 0.2.5
     */
    default String getID() { return toString(); }

    default boolean isCompatible(final IProfile profile) { return true; }
    default IImage getIcon() { return null; }

    void open(final IEditorContext context);

    default LaunchListener init(final RunProc configuration) { return null; }
}
