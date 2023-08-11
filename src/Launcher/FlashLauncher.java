package Launcher;

import Launcher.base.IAccount;
import Launcher.base.IProfile;
import UIL.ImagePosMode;
import UIL.ImageSizeMode;
import UIL.UI;
import UIL.base.*;
import Utils.Core;
import Utils.FS;
import Utils.IniGroup;
import Utils.Version;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FlashLauncher {
    public static final IImage ICON, BACKGROUND, ICON_SETTINGS, ICON_HELP;

    private static final int width = 720, height = 380;

    public static final String ID, NAME, AUTHOR, SHORT_DESCRIPTION;
    public static final Version VERSION;

    static {
        String i = null, n = null, a = null, sd = null;
        Version v = null;
        IImage icon = null, bg = null, s = null, h = null;
        try {
            final IniGroup g = new IniGroup(new String(FS.ROOT.readFully("://fl-info.ini"), StandardCharsets.UTF_8), false);
            i = g.getAsString("id");
            n = g.getAsString("name");
            a = g.getAsString("author");
            sd = g.getAsString("shortDescription");
            v = new Version(g.getAsString("version"));

            icon = UI.image(i + "://images/grass.png");
            bg = UI.image(i + "://images/background.png");
            s = UI.image(i + "://images/settings.png");
            h = UI.image(i + "://images/help.png");
        } catch (final Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        ID = i;
        NAME = n;
        AUTHOR = a;
        SHORT_DESCRIPTION = sd;
        VERSION = v;
        ICON = icon;
        BACKGROUND = bg;
        ICON_SETTINGS = s;
        ICON_HELP = h;
    }

    private final IFrame f;
    final IMenuBar menuBar = UI.menuBar().size(48, height - 16).pos(8, 8);
    private IContainer content = null;

    IAccount account = null;
    IProfile profile = null;

    FlashLauncher() {
        synchronized (FLCore.frames) {
            FLCore.frames.add(this);
        }
        f = UI.frame(NAME)
                .icon(ICON)
                .resizable(false)
                .size(width, height)
                .add(UI.imageView(ImagePosMode.CENTER, ImageSizeMode.SCALE).image(BACKGROUND).size(width, height))
                .onClose(s -> {
                    menuBar.changed();
                    synchronized (FLCore.frames) {
                        FLCore.frames.remove(this);
                        if (FLCore.frames.size() == 0)
                            FLCore.frames.notifyAll();
                    }
                    for (final FLListener l : FLCore.listeners)
                        l.onDisposeFrame(this);
                    s.dispose();
                })
                .pack()
                .center(null)
                .visible(true);
        if (FLCore.loader != null) {
            final IContainer r = UI.panel().size(128, 160).add(UI.loader().size(96, 96).pos(16, 16));
            final IText s = UI.text().size(112, 40).pos(8, 116);
            final IProgressBar p = UI.progressBar().size(128, 4).pos(0, 156);
            f.add(r.add(s, p).pos((width - r.width()) / 2, (height - r.height()) / 2));
            try {
                final TaskGroup l = FLCore.loader;
                if (l != null) {
                    f.update();
                    synchronized (l.po) {
                        while (!l.f) {
                            Task st = null;
                            synchronized (l.tasks) {
                                for (final Task t : l.tasks)
                                    synchronized (t.po) {
                                        if (!t.f) {
                                            st = t;
                                            break;
                                        }
                                    }
                            }
                            if (st != null) {
                                p.maxProgress(l.m).progress(l.p).update();
                                s.text(st).update();
                                l.po.wait();
                            } else
                                break;
                        }
                    }
                }
            } catch (final InterruptedException ignored) {
            }
            f.remove(r);
        }
        final int cw = width - 72, ch = height - 16, scw = cw - 168;
        f.add(menuBar.addEnd(ID + ".help", ICON_HELP, null, () -> {
                    synchronized (this) {
                        if (content != null)
                            f.remove(content);
                        final IMenuBar mb = UI.menuBar().size(160, ch);
                        final IScrollPane sp = UI.scrollPane().size(scw, ch).pos(168, 0);
                        f.add(content = UI.panel().add(mb, sp).background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(cw, ch).pos(64, 8)).update();
                        final Runnable r = Core.onNotifyLoop(FLCore.HELP_ITEMS, () -> {
                            final IContainer sc = UI.panel().size(scw, ch).pos(168, 0);
                            mb.clearTop();
                            sp.content(sc);
                            for (final FLCore.MBI mbi : FLCore.HELP_ITEMS)
                                mb.add(mbi.id, mbi.icon, mbi.text, () -> mbi.runnable.run(this, sc));
                            mb.select(ID + ".launcher");
                            content.update();
                        });
                        menuBar.onChange(() -> {
                            Core.offNotifyLoop(r);
                            return true;
                        });
                    }
                })
                .addEnd(ID + ".settings", ICON_SETTINGS, null, () -> {
                    synchronized (this) {
                        if (content != null)
                            f.remove(content);
                        final IMenuBar mb = UI.menuBar().size(160, ch);
                        final IScrollPane sp = UI.scrollPane().size(scw, ch).pos(168, 0);
                        f.add(content = UI.panel().add(mb, sp).background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(cw, ch).pos(64, 8)).update();
                        final Runnable r = Core.onNotifyLoop(FLCore.SETTINGS_ITEMS, () -> {
                            final IContainer sc = UI.panel().size(scw, ch).pos(168, 0);
                            mb.clearTop();
                            sp.content(sc);
                            for (final FLCore.MBI mbi : FLCore.SETTINGS_ITEMS)
                                mb.add(mbi.id, mbi.icon, mbi.text, () -> mbi.runnable.run(this, sc));
                            mb.select(ID + ".launcher");
                            content.update();
                        });
                        menuBar.onChange(() -> {
                            Core.offNotifyLoop(r);
                            return true;
                        });
                    }
                }));
        synchronized (FLCore.menus) {
            updateMenuBar();
        }
    }

    final void updateMenuBar() {
        final int w = width - 72, h = height - 16;
        menuBar.clearTop();
        for (final String key : FLCore.menus.keySet()) {
            final List<FLCore.MBI> l = FLCore.menus.get(key);
            synchronized (l) {
                for (final FLCore.MBI e : l)
                    menuBar.add(key + "." + e.id, e.icon, () -> {
                        synchronized (this) {
                            if (content != null)
                                f.remove(content);
                            f.add(content = UI.panel().background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(w, h).pos(64, 8));
                        }
                        e.runnable.run(this, content);
                        f.update();
                    });
            }
        }
        menuBar.update();
    }
}