package UIL.Swing;

import UIL.Theme;
import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IContainer;
import UIL.base.IScrollPane;
import Utils.Core;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class SScrollPane extends JPanel implements IScrollPane {
    IContainer content = null;
    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS;
    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;

    private int sx = 0, sy = 0;

    public SScrollPane() {
        setBorder(null);
        setOpaque(false);
        addMouseWheelListener(e -> {
            if (e.isShiftDown())
                sx = Math.min(Math.max(sx - e.getUnitsToScroll() * SSwing.MULTIPLIER, getWidth() - content.width()), 0);
            else
                sy = Math.min(Math.max(sy - e.getUnitsToScroll() * SSwing.MULTIPLIER, getHeight() - content.height()), 0);
            content.pos(sx, sy);
            repaint();
        });
    }

    private Area a = null;

    private long prev;

    @Override
    protected void paintComponent(final Graphics graphics) {
        final SGraphics2D g = graphics instanceof SGraphics2D ? (SGraphics2D) graphics :
                new SGraphics2D((Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create()));
        g.setRenderingHints(SSwing.RH);

        final int br = borderRadius.run(), w = getWidth(), h = getHeight(), sx = this.sx, sy = this.sy;
        a = new Area(br > 0 ? new RoundRectangle2D.Double(0, 0, w, h, br, br) : new Rectangle(0, 0, w, h));
        g.setClip(a);

        final IContainer c = content;
        if (c != null) {
            int cw = c.width(), ch = c.height(), rw = w, rh = h;
            boolean vs = false, hs = false;

            if (ch > rh) {
                vs = true;
                rw -= 8;
            }
            if (cw > rw) {
                hs = true;
                rh -= 8;
                if (!vs && ch > rh) {
                    vs = true;
                    rw -= 8;
                }
            }

            if (vs || hs) {
                g.setColor((Color) bg.get());
                if (vs) g.fillRect(rw, 0, 8, rh);
                if (hs) g.fillRect(0, rh, rw, 8);

                g.setColor((Color) fg.get());
                if (vs) {
                    final int sh = Math.max(32, Math.round(1f * rh / content.height() * rh));
                    g.fillRect(cw, Math.round((float) (rh - sh) / (content.height() - getHeight()) * (-sy)), 8, sh);
                }
                if (hs) {
                    final int sw = Math.max(32, Math.round(1f * rw / content.width() * rw));
                    g.fillRect(Math.round((float) (rw - sw) / (content.width() - getWidth()) * (-sx)), rh, sw, 8);
                }
            }

            a.intersect(new Area(new Rectangle(0, 0, rw, rh)));
        }

        if (!(graphics instanceof Graphics2D))
            g.dispose();
    }

    @Override
    protected void paintChildren(final Graphics graphics) {
        final SGraphics2D g = graphics instanceof SGraphics2D ? (SGraphics2D) graphics :
                new SGraphics2D((Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create()));

        g.setClip(a);
        super.paintChildren(g);

        if (!(graphics instanceof Graphics2D))
            g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public IComponent[] childs() { return content.childs(); }
    @Override public IContainer content() { return content; }
    @Override public Object getComponent() { return this; }

    @Override public SScrollPane content(final IContainer container) {
        if (content != null)
            super.remove((Component) content.getComponent());
        final Component c = (Component) container.getComponent();
        super.add(c);
        content = container;
        sx = c.getX();
        sy = c.getY();
        return this;
    }

    @Override public SScrollPane add(final IComponent component) { content.add(component); return this; }
    @Override public SScrollPane add(final IComponent... components) { content.add(components); return this; }
    @Override public SScrollPane remove(final IComponent component) { content.remove(component); return this; }
    @Override public SScrollPane clear() { content.clear(); return this; }
    @Override public SScrollPane size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SScrollPane pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SScrollPane visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SScrollPane focus() { requestFocus(); return this; }
    @Override public SScrollPane borderRadius(final RRunnable<Integer> borderRadius) { this.borderRadius = borderRadius; return this; }
    @Override public SScrollPane background(final IColor bg) { this.bg = bg; return this; }
    @Override public SScrollPane foreground(final IColor fg) { this.fg = fg; return this; }
    @Override public SScrollPane grounds(final IColor bg, final IColor fg) { this.bg = bg; this.fg = fg; return this; }

    @Override public SScrollPane update() {
        sx = Math.min(Math.max(sx, getWidth() - content.width()), 0);
        sy = Math.min(Math.max(sy, getHeight() - content.height()), 0);
        final boolean o = bg.alpha() == 255 && borderRadius.run() <= 0;
        if (isOpaque() != o)
            setOpaque(o);
        repaint();
        return this;
    }

    @Override public Component[] getComponents() { return ((JComponent) content).getComponents(); }
}