package UIL;

import UIL.base.*;
import Utils.Core;

import java.io.File;
import java.util.ArrayList;

public class FSChooser {
    public static final IImage ICON_REFRESH, ICON_PARENT, ICON_FILE, ICON_FOLDER;

    static {
        IImage r = null, p = null, i = null, o = null;
        try {
            r = UI.image("ui-lib://images/refresh.png");
            p = UI.image("ui-lib://images/parent.png");
            i = UI.image("ui-lib://images/file.png");
            o = UI.image("ui-lib://images/folder.png");
        } catch (final Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        ICON_REFRESH = r;
        ICON_PARENT = p;
        ICON_FILE = i;
        ICON_FOLDER = o;
    }

    private final IDialog dialog;

    private File current = null;

    private final IScrollPane sp;
    private final IContainer fileContainer;
    private final IText path, filename;
    private final int mhf = 252;
    private final int spw = 704;

    private final ArrayList<IButton> buttons = new ArrayList<>();
    private final ArrayList<File> files = new ArrayList<>();

    public FSChooser(IFrame owner, String title) {
        int height = 380;
        int width = 720;
        dialog = UI.dialog(owner, title).resizable(false).size(width, height).center(owner)
                .add(
                        UI.panel().size(width, height).borderRadius(UI.ZERO)
                ).add(
                        UI.panel().size(spw, 48).pos(8, 8)
                                .add(
                                        UI.button(ICON_REFRESH).size(32, 32).pos(8, 8).onAction((s, e) -> refresh())
                                ).add(
                                        UI.button(ICON_PARENT).size(32, 32).pos(48, 8).onAction((s, e) -> {
                                            if (current != null)
                                                current = current.getParentFile();
                                            refresh();
                                        })
                                ).add(path =
                                        UI.text("").ha(HAlign.LEFT).size(608, 32).pos(88, 8)
                                )
                ).add(sp =
                        UI.scrollPane().size(spw, mhf).pos(8, 64).content(
                                        fileContainer = UI.panel().borderRadius(UI.ZERO)
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
        final int fw = spw - 8, cw = spw - 24;
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
                        fileContainer.add(btn = UI.button(child.getName(), ICON_FOLDER).ha(HAlign.LEFT).size(cw, 24).pos(8, h).onAction((s, e) -> {
                            if (e.isMouse() && e.clickCount() == 2) {
                                current = child;
                                refresh();
                            } else {
                                for (IButton b : buttons)
                                    b.background(UI.TRANSPARENT);
                                buttons.clear();
                                files.clear();
                                s.background(Theme.FSC_FOREGROUND);
                                buttons.add(s);
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
                        fileContainer.add(btn = UI.button(child.getName(), ICON_FILE).ha(HAlign.LEFT).size(cw, 24).pos(8, h).onAction((s, e) -> {
                            for (IButton b : buttons)
                                b.background(UI.TRANSPARENT);
                            buttons.clear();
                            files.clear();
                            s.background(Theme.FSC_FOREGROUND);
                            buttons.add(s);
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
                int tw = cw - 16;
                fileContainer.add(
                        UI.panel().size(cw, 56).pos(8, h)
                                .add(
                                        UI.text(child.getAbsolutePath() + "   " + Core.strSize1024(u) + " / " + Core.strSize1024(t)).size(tw, 24).pos(8, 8).ha(HAlign.LEFT)
                                ).add(
                                        UI.progressBar().size(tw, 8).pos(8, 40).maxProgress(t).progress(u)
                                ).add(
                                        UI.button().background(UI.TRANSPARENT).size(cw, 56).onAction((s, e) -> {
                                            if (e.isMouse() && e.clickCount() == 2) {
                                                current = child;
                                                refresh();
                                            } else {
                                                for (IButton b : buttons)
                                                    b.background(UI.TRANSPARENT);
                                                buttons.clear();
                                                files.clear();
                                                s.background(Theme.FSC_FOREGROUND);
                                                buttons.add(s);
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
