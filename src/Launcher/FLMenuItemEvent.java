package Launcher;

import UIL.base.IComponent;
import UIL.base.IContainer;
import UIL.base.IImage;
import Utils.RRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FLMenuItemEvent {
    public final FlashLauncher launcher;
    public final IContainer container;
    public final IImage icon;
    final Object l = new Object();
    boolean a = true;
    final HashMap<Runnable, ArrayList<RRunnable<Boolean>>> m = new HashMap<>();

    FLMenuItemEvent(final FlashLauncher l, final IContainer c, final IImage icon) {
        launcher = l;
        container = c;
        this.icon = icon;
    }

    public int width() { return container.width(); }
    public int height() { return container.height(); }
    public IComponent[] childs() { return container.childs(); }

    public FLMenuItemEvent add(final IComponent... components) { container.add(components); return this; }
    public FLMenuItemEvent add(final IComponent component) { container.add(component); return this; }
    public FLMenuItemEvent remove(final IComponent component) { container.remove(component); return this; }
    public FLMenuItemEvent clear() { container.clear(); return this; }
    public FLMenuItemEvent update() { container.update(); return this; }

    public Runnable onClose(final Runnable runnable) {
        synchronized (l) {
            if (a) {
                final RRunnable<Boolean> rr = () -> {
                    runnable.run();
                    return true;
                };
                final ArrayList<RRunnable<Boolean>> l = m.get(runnable);
                if (l == null)
                    m.put(runnable, new ArrayList<RRunnable<Boolean>>() {{
                        add(rr);
                    }});
                else
                    l.add(rr);
                launcher.menuBar.onChange(rr);
            }
        }
        return runnable;
    }

    public Runnable offClose(final Runnable runnable) {
        synchronized (l) {
            if (a) {
                final ArrayList<RRunnable<Boolean>> l = m.get(runnable);
                if (l != null) {
                    launcher.menuBar.offChange(l.get(0));
                    l.remove(0);
                    if (l.isEmpty())
                        m.remove(runnable);
                }
            }
        }
        return runnable;
    }
}