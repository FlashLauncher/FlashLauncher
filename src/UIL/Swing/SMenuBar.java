package UIL.Swing;

import UIL.*;
import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IImage;
import UIL.base.IMenuBar;
import Utils.BoolRunnable;
import Utils.IntRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SMenuBar extends JPanel implements IMenuBar {
    private IColor bg = Theme.BACKGROUND, fg = Theme.FOREGROUND;
    private IntRunnable borderRadius = Theme.BORDER_RADIUS;

    private final ReentrantLock chlo = new ReentrantLock();
    private final ArrayList<BoolRunnable> chl = new ArrayList<>();

    private final ReentrantLock l = new ReentrantLock();
    private final ArrayList<SButton> top = new ArrayList<>();
    private final ArrayList<SButton> bottom = new ArrayList<>();
    private final ConcurrentHashMap<String, SButton> links = new ConcurrentHashMap<>();

    private boolean isTop = true;
    private int childW = 0, sy = 0, iv = 0, y = 8, fh = 32, fy = 24;
    private float cy = fy, ch = fh;

    private static final float d = SSwing.DELTA / 2f, d2 = d / 2;
    private final STimer timer = new STimer(SSwing.DELTA) {
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
                repaint();
            } else
                stop();
        }
    };

    public SMenuBar() {
        setOpaque(false);
        setLayout(null);
        addMouseWheelListener(e -> {
            if (!e.isShiftDown()) {
                l.lock();
                final int h = y + iv, nsy = Math.max(Math.min((int) Math.round(sy - e.getPreciseWheelRotation() * e.getScrollAmount() * SSwing.MULTIPLIER), 0), h > getHeight() ? getHeight() - h : 0);
                if (isTop) {
                    final int d = nsy - sy;
                    fy += d;
                    cy += d;
                }
                sy = nsy;
                int y = 8 + sy;
                for (SButton child : top) {
                    child.pos(8, y);
                    y += 40;
                }
                l.unlock();
                repaint();
            }
        });
    }

    @Override
    protected void paintChildren(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);

        final int br = borderRadius.run(), contentHeight = y, sh = getHeight() - iv, h = Math.max(64, sh * (sh / contentHeight)), yl;
        if (br > 0) g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));

        g.setColor((Color) bg.get());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (iv != 0) {
            yl = getHeight() - iv;
            g.setColor(Color.GRAY);
            g.drawLine(8, yl, getWidth() - 8, yl);
        } else
            yl = getHeight();

        final BufferedImage img = g.getDeviceConfiguration().createCompatibleImage(getWidth(), yl, Transparency.TRANSLUCENT);
        super.paintChildren(img.createGraphics());
        g.drawImage(img, 0, 0, this);

        l.lock();
        for (final SButton btn : bottom)
            btn.paint(g.create(btn.getX(), btn.getY(), btn.width(), btn.height()));
        l.unlock();

        g.setColor((Color) fg.get());
        g.fillRoundRect(-4, Math.round(cy - ch / 2), 8, Math.round(ch), 8, 8);

        if (contentHeight > 0 && yl < contentHeight)
            g.fillRect(getWidth() - 4, Math.round((float) (sh - h) / (contentHeight - sh) * (-sy)), 4, h);

        g.dispose();
    }

    @Override
    public SMenuBar subSelect(final boolean sub) {
        fh = sub ? 8 : 32;
        timer.start();
        return this;
    }

    @Override
    public SMenuBar add(final IImage icon, final String id, final Object text, final Runnable action) {
        final SButton btn = new SButton(text, icon)
                .imageTextDist(4)
                .ha(HAlign.LEFT)
                .background(UI.TRANSPARENT)
                .size(childW, 32)
                .pos(8, y)
                .on("action", self -> {
                    fy = ((SButton) self).getY() + 16;
                    isTop = true;
                    onChange(action);
                });
        links.put(id, btn);
        l.lock();
        top.add(btn);
        l.unlock();
        super.add(btn);
        y += 40;
        return this;
    }

    @Override public SMenuBar add(final IImage icon, final String id, final Runnable action) { return add(icon, id, null, action); }

    @Override
    public SMenuBar addEnd(final IImage icon, final String id, final Runnable action) {
        if (iv == 0) iv = 8;
        final int cy = getHeight() - iv - 16;
        SButton btn = new SButton(icon)
                .ha(HAlign.LEFT)
                .background(UI.TRANSPARENT)
                .size(childW, 32)
                .pos(8, cy - 16)
                .on("action", self -> {
                    fy = cy;
                    isTop = false;
                    onChange(action);
                });
        links.put(id, btn);
        l.lock();
        bottom.add(btn);
        l.unlock();
        super.add(btn, 0);
        iv += 40;
        return this;
    }

    private void onChange(Runnable action) {
        fh = 32;
        timer.start();
        chlo.lock();
        chl.removeIf(BoolRunnable::run);
        chlo.unlock();
        action.run();
        System.gc();
    }

    @Override
    public SMenuBar changed() {
        chlo.lock();
        chl.removeIf(BoolRunnable::run);
        chlo.unlock();
        return this;
    }

    @Override
    public SMenuBar onChange(final BoolRunnable runnable) {
        chlo.lock();
        chl.add(runnable);
        chlo.unlock();
        return this;
    }

    @Override
    public SMenuBar offChange(final BoolRunnable runnable) {
        chlo.lock();
        chl.remove(runnable);
        chlo.unlock();
        return this;
    }

    @Override
    public SMenuBar select(final String id) {
        final SButton b = links.get(id);
        if (b != null) b.doClick();
        return this;
    }

    @Override
    public SMenuBar add(IComponent component) {
        super.add((Component) component.getComponent());
        return this;
    }

    @Override
    public SMenuBar remove(IComponent component) {
        super.remove((Component) component.getComponent());
        return this;
    }

    @Override public IComponent[] childs() { return (IComponent[]) getComponents(); }

    @Override
    public SMenuBar clear() {
        super.removeAll();
        repaint();
        return this;
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override public SMenuBar size(int width, int height) {
        setSize(width, height);
        childW = width - 16;
        return this;
    }
    @Override public SMenuBar pos(int x, int y) { setLocation(x, y); return this; }
    @Override public SMenuBar visible(boolean visible) { setVisible(visible); return this; }
    @Override public SMenuBar focus() { requestFocus(); return this; }
    @Override public SMenuBar getComponent() { return this; }

    @Override
    public SMenuBar background(IColor bg) {
        this.bg = bg;
        repaint();
        return this;
    }

    @Override
    public SMenuBar foreground(IColor fg) {
        this.fg = fg;
        repaint();
        return this;
    }

    @Override
    public SMenuBar borderRadius(IntRunnable borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
        return this;
    }

    @Override
    public SMenuBar borderRadius(int borderRadius) {
        this.borderRadius = () -> borderRadius;
        repaint();
        return this;
    }
}