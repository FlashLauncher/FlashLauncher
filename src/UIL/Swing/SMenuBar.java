package UIL.Swing;

import UIL.*;
import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IImage;
import UIL.base.IMenuBar;
import Utils.ListMap;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Map;

public class SMenuBar extends JPanel implements IMenuBar {
    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;
    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS;
    private final ArrayList<RRunnable<Boolean>> chl = new ArrayList<>();
    private final ArrayList<SButton> top = new ArrayList<>(), bottom = new ArrayList<>();
    private final ListMap<String, SButton> links = new ListMap<>();

    private boolean isTop = true;
    private int childW = 0, sy = 0, iv = 0, y = 8, fh = 32, fy = 24;
    private float cy = fy, ch = fh;

    private static final float d = SSwing.DELTA / 2f, d2 = d / 2;
    private final SFPSTimer timer = new SFPSTimer() {
        @Override
        public void run() {
            final boolean yu = cy != fy, hu = ch != fh;
            if (yu || hu) {
                if (yu) {
                    final float dd = fy - cy;
                    cy += dd > 0 ? Math.min(dd, d) : Math.max(dd, -d);
                }
                if (hu) {
                    final float dd = fh - ch;
                    ch += dd > 0 ? Math.min(dd, d2) : Math.max(dd, -d2);
                }
                repaint(0, 0, 8, getHeight());
            } else
                stop();
        }
    };

    public SMenuBar() {
        setOpaque(false);
        setLayout(null);
        addMouseWheelListener(e -> {
            if (!e.isShiftDown()) {
                synchronized (top) {
                    final int nsy = Math.min(Math.max(sy - e.getUnitsToScroll() * SSwing.MULTIPLIER, iv), 0);
                    if (isTop) {
                        final int d = nsy - sy;
                        fy += d;
                        cy += d;
                    }
                    sy = nsy;
                    int y = 8 + sy;
                    for (final SButton child : top) {
                        child.pos(8, y);
                        y += 40;
                    }
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintChildren(final Graphics graphics) {
        final SGraphics2D g = graphics instanceof SGraphics2D ? (SGraphics2D) graphics :
                new SGraphics2D((Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create()));
        g.setRenderingHints(SSwing.RH);

        final int br = borderRadius.run(), contentHeight = y, sh = getHeight() - iv, h = Math.max(64, sh * (sh / contentHeight)), yl;
        final Area a = br > 0 ? new Area(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br)) : null;

        g.setClip(a);
        g.setColor((Color) bg.get());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (iv != 0) {
            yl = getHeight() - iv;
            g.setColor(Color.GRAY);
            g.drawLine(8, yl, getWidth() - 8, yl);
        } else
            yl = getHeight();

        g.clipRect(0, 0, getWidth(), yl);
        super.paintChildren(g);
        g.setClip(a);

        synchronized (bottom) {
            for (final SButton btn : bottom)
                btn.paint(g.create(btn.getX(), btn.getY(), btn.width(), btn.height()));
        }

        g.setColor((Color) fg.get());
        g.fillRoundRect(-4, Math.round(cy - ch / 2), 8, Math.round(ch), 8, 8);

        if (contentHeight > 0 && yl < contentHeight)
            g.fillRect(getWidth() - 4, Math.round((float) (sh - h) / (contentHeight - sh) * (-sy)), 4, h);

        if (!(graphics instanceof Graphics2D))
            g.dispose();
    }

    @Override
    public SMenuBar subSelect(final boolean sub) {
        fh = sub ? 8 : 32;
        timer.start();
        return this;
    }

    @Override
    public SMenuBar add(final String id, final IImage icon, final Object text, final Runnable action) {
        final SButton btn = new SButton(text, icon)
                .imageTextDist(4)
                .ha(HAlign.LEFT)
                .background(UI.TRANSPARENT)
                .size(childW, 32);
        final int ry;
        synchronized (top) {
            synchronized (links) {
                links.put(id, btn);
            }
            top.add(btn);
            ry = y;
            y += 40;
        }
        super.add(btn.pos(8, ry)
                .onAction((s, e) -> {
                    fy = ((SButton) s).getY() + 16;
                    isTop = true;
                    onChange(action);
                }));
        return this;
    }

    @Override public SMenuBar add(final String id, final IImage icon, final Runnable action) { return add(id, icon, null, action); }

    @Override
    public SMenuBar addEnd(final String id, final IImage icon, final Object text, final Runnable action) {
        final SButton btn = new SButton(text, icon)
                .ha(HAlign.LEFT)
                .background(UI.TRANSPARENT)
                .size(childW, 32);
        final int h = getHeight(), cy;
        synchronized (bottom) {
            if (iv == 0)
                iv = 8;
            cy = h - iv - 16;
            synchronized (links) {
                links.put(id, btn);
            }
            bottom.add(btn);
            iv += 40;
        }
        super.add(btn.pos(8, cy - 16)
                .onAction((s, e) -> {
                    fy = cy;
                    isTop = false;
                    onChange(action);
                }), 0);
        return this;
    }

    @Override public SMenuBar addEnd(final String id, final IImage icon, final Runnable action) { return addEnd(id, icon, null, action); }

    private void onChange(final Runnable action) {
        fh = 32;
        timer.start();
        synchronized (chl) {
            chl.removeIf(RRunnable::run);
        }
        action.run();
    }

    @Override
    public SMenuBar onChange(final RRunnable<Boolean> runnable) {
        synchronized (chl) {
            chl.add(runnable);
        }
        return this;
    }

    @Override
    public SMenuBar offChange(final RRunnable<Boolean> runnable) {
        synchronized (chl) {
            chl.remove(runnable);
        }
        return this;
    }

    @Override
    public SMenuBar changed() {
        synchronized (chl) {
            chl.removeIf(RRunnable::run);
        }
        return this;
    }

    @Override
    public SMenuBar select(final String id) {
        synchronized (links) {
            final SButton b = links.get(id);
            if (b != null)
                b.doClick();
        }
        return this;
    }

    @Override
    public SMenuBar add(final IComponent component) {
        super.add((Component) component.getComponent());
        return this;
    }

    @Override
    public SMenuBar add(final IComponent... components) {
        for (final IComponent c : components)
            super.add((Component) c.getComponent());
        return this;
    }

    @Override
    public SMenuBar remove(final IComponent component) {
        super.remove((Component) component.getComponent());
        return this;
    }

    @Override public IComponent[] childs() { return (IComponent[]) getComponents(); }

    @Override
    public SMenuBar clear() {
        super.removeAll();
        synchronized (top) {
            synchronized (bottom) {
                synchronized (links) {
                    iv = sy = 0;
                    y = 8;
                    links.clear();
                    top.clear();
                    bottom.clear();
                }
            }
        }
        return this;
    }

    @Override
    public SMenuBar clearTop() {
        final ArrayList<SButton> l;
        synchronized (top) {
            synchronized (links) {
                for (final Map.Entry<String, SButton> e : links.entrySet())
                    if (top.contains(e.getValue())) {
                        links.remove(e.getKey());
                        continue;
                    }
                l = new ArrayList<>(top);
                top.clear();
            }
        }
        for (final SButton b : l)
            super.remove(b);
        y = 8;
        return this;
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public SMenuBar getComponent() { return this; }

    @Override public SMenuBar size(final int width, final int height) {
        setSize(width, height);
        childW = width - 16;
        return this;
    }
    @Override public SMenuBar pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SMenuBar visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SMenuBar focus() { requestFocus(); return this; }


    @Override
    public SMenuBar background(final IColor bg) {
        this.bg = bg;
        return this;
    }

    @Override
    public SMenuBar foreground(final IColor fg) {
        this.fg = fg;
        return this;
    }

    @Override
    public SMenuBar borderRadius(final RRunnable<Integer> borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    @Override
    public SMenuBar update() {
        repaint();
        return this;
    }
}