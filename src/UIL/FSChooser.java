package UIL;

import UIL.base.*;
import Utils.Core;
import Utils.RRunnable1a;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private final IScrollPane sp;
    private final IContainer fileContainer;
    private final IText path, filename;
    private final int mhf = 252, spw = 704;
    private final ArrayList<IButton> buttons = new ArrayList<>();
    private final ArrayList<File> files = new ArrayList<>();
    private File current = null;
    private boolean isSuccess = false;

    /**
     * Not implemented.
     */
    public boolean allowMultipleSelection = false;

    public final ConcurrentLinkedQueue<RRunnable1a<Boolean, File>> filters = new ConcurrentLinkedQueue<>();

    public FSChooser(final IFrame owner, final String title) {
        final int height = 380, width = 720;
        dialog = UI.dialog(owner, title).resizable(false).size(width, height).center(owner);
        dialog.add(
                        UI.panel().size(width, height).borderRadius(UI.ZERO),
                        UI.panel().size(spw, 48).pos(8, 8).add(
                                UI.button(ICON_REFRESH).imageOffset(4).size(32, 32).pos(8, 8).onAction((s, e) -> refresh()),
                                UI.button(ICON_PARENT).imageOffset(4).size(32, 32).pos(48, 8).onAction((s, e) -> {
                                    if (current != null)
                                        current = current.getParentFile();
                                    refresh();
                                }),
                                path = UI.text("").ha(HAlign.LEFT).size(608, 32).pos(88, 8)
                        ),
                        sp = UI.scrollPane().size(spw, mhf).pos(8, 64).content(fileContainer = UI.panel().borderRadius(UI.ZERO)),
                        UI.panel().size(spw, 48).pos(8, mhf + 72).add(
                                filename = UI.text("File name").ha(HAlign.LEFT).size(spw - 144 - 80, 32).pos(80, 8),
                                UI.button("Select").onAction((s, e) -> {
                                    isSuccess = true;
                                    dialog.visible(false);
                                }).size(128, 32).pos(spw - 136, 8)
                        )
                )
        ;
    }

    public void refresh() {
        fileContainer.clear();
        buttons.clear();
        int h = 8;
        final int fw = spw - 8, cw = spw - 24, tw = cw - 16;
        if (current != null) {
            path.text(current.getAbsolutePath()).update();
            final File[] l = current.listFiles();
            if (l == null) {
                fileContainer.add(UI.text("IO Exception").size(cw, 24));
                h = 24;
            } else {
                for (final File child : l)
                    if (child.isDirectory()) {
                        final IButton btn;
                        fileContainer.add(btn = UI.button(child.getName(), ICON_FOLDER).imageOffset(3).ha(HAlign.LEFT).size(cw, 24).pos(8, h).onAction((s, e) -> {
                            if (e.isMouse() && e.clickCount() == 2) {
                                current = child;
                                refresh();
                            } else {
                                for (final IButton b : buttons)
                                    b.background(UI.TRANSPARENT);
                                buttons.clear();
                                buttons.add(s.background(Theme.FSC_FOREGROUND));
                                files.clear();
                                files.add(child);
                                filename.text(child.getName()).update();
                            }
                        }));
                        if (files.contains(child))
                            buttons.add(btn.background(Theme.FSC_FOREGROUND));
                        else
                            btn.background(UI.TRANSPARENT);
                        h += 24;
                    }
                m:
                for (final File child : l)
                    if (child.isFile()) {
                        for (final RRunnable1a<Boolean, File> f : filters)
                            if (!f.run(child))
                                continue m;
                        final IButton btn;
                        fileContainer.add(btn = UI.button(child.getName(), ICON_FILE).imageOffset(3).ha(HAlign.LEFT).size(cw, 24).pos(8, h).onAction((s, e) -> {
                            for (final IButton b : buttons)
                                b.background(UI.TRANSPARENT);
                            buttons.clear();
                            files.clear();
                            buttons.add(s.background(Theme.FSC_FOREGROUND));
                            files.add(child);
                            filename.text(child.getName()).update();
                        }));
                        if (files.contains(child))
                            buttons.add(btn.background(Theme.FSC_FOREGROUND));
                        else
                            btn.background(UI.TRANSPARENT);
                        h += 24;
                    }
                for (int i = 0; i < files.size(); i++)
                    if (!files.get(i).exists()) {
                        files.remove(i);
                        i--;
                    }
            }
        } else {
            files.clear();
            path.text("Computer").update();
            File[] l = null;
            if (Core.IS_LINUX)
                try (final Scanner s = new Scanner(new File("/proc/mounts"))) {
                    final ArrayList<File> al = new ArrayList<>();
                    String l2;
                    boolean r = true;
                    while (s.hasNextLine()) {
                        l2 = s.nextLine();
                        if (l2.startsWith("/dev/")) {
                            int i = l2.indexOf(' ');
                            if (i != -1) {
                                l2 = l2.substring(i + 1);
                                i = l2.indexOf(' ');
                                if (i != -1) {
                                    l2 = l2.substring(0, i);
                                    if (r && l2.equals("/"))
                                        r = false;
                                    al.add(new File(l2));
                                }
                            }
                        }
                    }
                    if (r)
                        al.add(new File("/"));
                    l = al.toArray(new File[0]);
                } catch (final Throwable ex) {
                    ex.printStackTrace();
                }
            if (l == null)
                l = File.listRoots();
            for (final File child : l) {
                final long t = child.getTotalSpace(), u = t - child.getFreeSpace();
                fileContainer.add(UI.panel().size(cw, 56).pos(8, h).add(
                        UI.text(child.getAbsolutePath() + "   " + Core.strSize1024(u) + " / " + Core.strSize1024(t)).size(tw, 24).pos(8, 8).ha(HAlign.LEFT),
                        UI.progressBar().maxProgress(t).progress(u).size(tw, 8).pos(8, 40),
                        UI.button().background(UI.TRANSPARENT).size(cw, 56).onAction((s, e) -> {
                            if (e.isMouse() && e.clickCount() == 2) {
                                current = child;
                                refresh();
                            } else {
                                for (final IButton b : buttons)
                                    b.background(UI.TRANSPARENT);
                                buttons.clear();
                                files.clear();
                                s.background(Theme.FSC_FOREGROUND);
                                buttons.add(s);
                                files.add(child);
                                filename.text(child.getName());
                            }
                        })
                ));
                h += 64;
            }
        }
        h = Math.max(mhf, h + 8);
        fileContainer.size(h == mhf ? spw : fw, h);
        sp.update();
    }

    public File[] getSelected() { return files.toArray(new File[0]); }
    public boolean start() { refresh(); isSuccess = false; dialog.visible(true); return isSuccess; }
    public void dispose() { dialog.dispose(); }
}
