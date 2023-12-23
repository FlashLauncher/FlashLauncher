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

    final IFrame frame;
    final IMenuBar menuBar = UI.menuBar().size(48, height - 16).pos(8, 8);
    private IContainer content = null;

    IAccount account = null;
    IProfile profile = null;

    FlashLauncher() {
        synchronized (FLCore.frames) {
            FLCore.frames.add(this);
        }
        frame = UI.frame(NAME)
                .icon(ICON)
                .resizable(false)
                .size(width, height)
                .add(UI.imageView(ImagePosMode.CENTER, ImageSizeMode.SCALE).image(BACKGROUND).size(width, height))
                .onClose(s -> {
                    menuBar.changed();
                    synchronized (FLCore.frames) {
                        FLCore.frames.remove(this);
                        if (FLCore.frames.isEmpty())
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
            final IProgressBar p = UI.progressBar().borderRadius(UI.ZERO).size(128, 4).pos(0, 156);
            frame.add(r.add(s, p).pos((width - r.width()) / 2, (height - r.height()) / 2));
            try {
                final TaskGroup l = FLCore.loader;
                if (l != null) {
                    frame.update();
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
            } catch (final InterruptedException ignored) {}
            frame.remove(r);
        }
        final int cw = width - 72, ch = height - 16, scw = cw - 168;
        frame.add(menuBar.addEnd("help", ICON_HELP, null, () -> {
            if (content != null)
                frame.remove(content);
            final IMenuBar mb = UI.menuBar().size(160, ch);
            final IScrollPane sp = UI.scrollPane().size(scw, ch).pos(168, 0);
            frame.add(content = UI.panel().add(mb, sp).background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(cw, ch).pos(64, 8));
            final Runnable r = Core.onNotifyLoop(FLCore.HELP_ITEMS, () -> {
                final IContainer sc = UI.panel().size(scw, ch);
                mb.clearTop();
                sp.content(sc);
                for (final FLMenuItemListener mbi : FLCore.HELP_ITEMS)
                    mb.add(mbi.id, mbi.icon, mbi.text, () -> {
                        final FLMenuItemEvent e = new FLMenuItemEvent(this, sc, mbi.icon);
                        mbi.onOpen(e);
                        menuBar.onChange(() -> {
                            synchronized (e.l) {
                                e.a = false;
                            }
                            return true;
                        });
                    });
                mb.select("launcher");
                content.update();
            });
            menuBar.onChange(() -> {
                Core.offNotifyLoop(r);
                return true;
            });
        }).addEnd("settings", ICON_SETTINGS, null, () -> {
            if (content != null)
                frame.remove(content);
            final IMenuBar mb = UI.menuBar().size(160, ch);
            final IScrollPane sp = UI.scrollPane().size(scw, ch).pos(168, 0);
            frame.add(content = UI.panel().add(mb, sp).background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(cw, ch).pos(64, 8));
            final Runnable r = Core.onNotifyLoop(FLCore.HELP_ITEMS, () -> {
                final IContainer sc = UI.panel().size(scw, ch);
                mb.clearTop();
                sp.content(sc);
                for (final FLMenuItemListener mbi : FLCore.SETTINGS_ITEMS)
                    mb.add(mbi.id, mbi.icon, mbi.text, () -> {
                        final FLMenuItemEvent e = new FLMenuItemEvent(this, sc, mbi.icon);
                        mbi.onOpen(e);
                        menuBar.onChange(() -> {
                            synchronized (e.l) {
                                e.a = false;
                            }
                            return true;
                        });
                    });
                mb.select("launcher");
                content.update();
            });
            menuBar.onChange(() -> {
                Core.offNotifyLoop(r);
                return true;
            });
        }));
        synchronized (FLCore.MENU_ITEMS) {
            updateMenuBar();
        }
        frame.update();
    }

    final void updateMenuBar() {
        final int w = width - 72, h = height - 16;
        menuBar.clearTop();
        synchronized (FLCore.MENU_ITEMS) {
            for (final FLMenuItemListener mbi : FLCore.MENU_ITEMS)
                menuBar.add(mbi.id, mbi.icon, () -> {
                    synchronized (this) {
                        if (content != null)
                            frame.remove(content);
                        frame.add(content = UI.panel().background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(w, h).pos(64, 8));
                    }
                    final FLMenuItemEvent e = new FLMenuItemEvent(this, content, mbi.icon);
                    mbi.onOpen(e);
                    menuBar.onChange(() -> {
                        synchronized (e.l) {
                            e.a = false;
                        }
                        return true;
                    });
                    frame.update();
                });
        }
        menuBar.update();
    }
}