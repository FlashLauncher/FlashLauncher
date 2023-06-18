package UIL;

import Launcher.FLCore;
import UIL.base.*;
import Utils.Core;

import java.io.File;
import java.util.ArrayList;

public class FSChooser {
    public static IImage file, folder;

    private final IDialog dialog;

    private File current = null;

    private final IScrollPane sp;
    private final IContainer fileContainer;
    private final IText path, filename;
    private final int width = 720, height = 380, mhf = 252, spw = 704, fw = spw - 8, cw = fw - 16, tw = cw - 16;

    private final ArrayList<IButton> buttons = new ArrayList<>();
    private final ArrayList<File> files = new ArrayList<>();

    public FSChooser(IFrame owner, String title) {
        dialog = UI.dialog(owner, title).resizable(false).size(width, height).center(owner)
                .add(
                        UI.panel().size(width, height).borderRadius(0)
                ).add(
                        UI.panel().size(spw, 48).pos(8, 8)
                                .add(
                                        UI.button(FLCore.iconRefresh).size(32, 32).pos(8, 8).on("action", this::refresh)
                                ).add(
                                        UI.button(FLCore.iconParent).size(32, 32).pos(48, 8).on("action", () -> {
                                            if (current != null)
                                                current = current.getParentFile();
                                            refresh();
                                        })
                                ).add(path =
                                        UI.text("").ha(HAlign.LEFT).size(608, 32).pos(88, 8)
                                )
                ).add(sp =
                        UI.scrollPane().size(spw, mhf).pos(8, 64).content(
                                        fileContainer = UI.panel().borderRadius(0)
                                )
                ).add(
                        UI.panel().size(spw, 48).pos(8, mhf + 72)
                                .add(
                                        filename = UI.text("File name").ha(HAlign.LEFT).size(spw - 144 - 80, 32).pos(80, 8)
                                ).add(
                                        UI.button("Select").size(128, 32).pos(spw - 136, 8)
                                )
                )
        ;
    }

    public void refresh() {
        fileContainer.clear();
        buttons.clear();
        int h = 8;
        if (current != null) {
            path.text(current.getAbsolutePath());
            final File[] l = current.listFiles();
            if (l == null) {
                fileContainer.add(UI.text("IO Exception").size(cw, 24));
                h = 24;
            } else {
                for (File child : l)
                    if (child.isDirectory()) {
                        final IButton btn;
                        fileContainer.add(btn = UI.button(child.getName(), folder).ha(HAlign.LEFT).size(cw, 24).pos(8, h).on((IButton self, IButton.IButtonActionEvent event) -> {
                            if (event.isMouse() && event.clickCount() == 2) {
                                current = child;
                                refresh();
                            } else {
                                for (IButton b : buttons)
                                    b.background(UI.TRANSPARENT);
                                buttons.clear();
                                files.clear();
                                self.background(Theme.FSC_FOREGROUND);
                                buttons.add(self);
                                files.add(child);
                                filename.text(child.getName());
                            }
                        }));
                        if (files.contains(child)) {
                            btn.background(Theme.FSC_FOREGROUND);
                            buttons.add(btn);
                        } else
                            btn.background(UI.TRANSPARENT);
                        h += 24;
                    }
                for (File child : l)
                    if (child.isFile()) {
                        final IButton btn;
                        fileContainer.add(btn = UI.button(child.getName(), file).ha(HAlign.LEFT).size(cw, 24).pos(8, h).on((IButton self, IButton.IButtonActionEvent event) -> {
                            for (IButton b : buttons)
                                b.background(UI.TRANSPARENT);
                            buttons.clear();
                            files.clear();
                            self.background(Theme.FSC_FOREGROUND);
                            buttons.add(self);
                            files.add(child);
                            filename.text(child.getName());
                        }));
                        if (files.contains(child)) {
                            btn.background(Theme.FSC_FOREGROUND);
                            buttons.add(btn);
                        } else
                            btn.background(UI.TRANSPARENT);
                        h += 24;
                    }
                for (int i = 0; i < files.size(); i++) {
                    if (!files.get(i).exists()) {
                        files.remove(i);
                        i--;
                    }
                }
            }
        } else {
            files.clear();
            path.text("Computer");
            for (File child : File.listRoots()) {
                final long t = child.getTotalSpace(), u = t - child.getFreeSpace();
                fileContainer.add(
                        UI.panel().size(cw, 56).pos(8, h)
                                .add(
                                        UI.text(child.getAbsolutePath() + "   " + Core.strSize1024(u) + " / " + Core.strSize1024(t)).size(tw, 24).pos(8, 8).ha(HAlign.LEFT)
                                ).add(
                                        UI.progressBar().size(tw, 8).pos(8, 40).maxProgress(t).progress(u)
                                ).add(
                                        UI.button().background(UI.TRANSPARENT).size(cw, 56).on((IButton self, IButton.IButtonActionEvent event) -> {
                                            if (event.isMouse() && event.clickCount() == 2) {
                                                current = child;
                                                refresh();
                                            } else {
                                                for (IButton b : buttons)
                                                    b.background(UI.TRANSPARENT);
                                                buttons.clear();
                                                files.clear();
                                                self.background(Theme.FSC_FOREGROUND);
                                                buttons.add(self);
                                                files.add(child);
                                                filename.text(child.getName());
                                            }
                                        })
                                )
                );
                h += 64;
            }
        }
        h = Math.max(mhf, h + 8);
        fileContainer.size(h == mhf ? spw : fw, h);
        sp.update();
    }

    public void start() {
        refresh();
        dialog.visible(true);
    }

    public void dispose() { dialog.dispose(); }
}
