package Launcher;

import UIL.FSChooser;

public class FLFSChooser extends FSChooser {
    public FLFSChooser(final FlashLauncher owner, String title) {
        super(owner == null ? null : owner.frame, title);
    }
}
