package UIL.Swing;

import Utils.IntRunnable;
import UIL.Theme;
import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IContainer;
import UIL.base.IScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class SScrollPane extends JPanel implements IScrollPane {
    IContainer content = null;
    private IntRunnable borderRadius = Theme.BORDER_RADIUS;
    private IColor bg = Theme.BACKGROUND, fg = Theme.FOREGROUND;

    private int sx = 0, sy = 0;

    public SScrollPane() {
        setBorder(null);
        setOpaque(false);
        addMouseWheelListener(e -> {
            if (e.isShiftDown())
                sx = Math.min(Math.max(sx - e.getUnitsToScroll() * SSwing.MULTIPLIER, getWidth() - content.width()), 0);
            else
                sy = Math.min(Math.max(sy - e.getUnitsToScroll() * SSwing.MULTIPLIER, getHeight() - content.height()), 0);
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {}

    @Override
    protected void paintChildren(Graphics graphics) {
        {
            final int br = borderRadius.run();
            if (br > 0)
                graphics.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));

            final IContainer c = content;
            if (c != null)
                c.pos(sx, sy);
        }
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);
        final int chw = content.width(), chh = content.height();
        boolean vs = false, hs = false;
        int cw = getWidth(), ch = getHeight();
        if (chh > ch) {
            vs = true;
            cw -= 8;
        }
        if (chw > cw) {
            hs = true;
            ch -= 8;
            if (!vs && chh > ch) {
                vs = true;
                cw -= 8;
            }
        }

        final BufferedImage img = g.getDeviceConfiguration().createCompatibleImage(cw, ch, Transparency.TRANSLUCENT);
        final Graphics g2 = img.createGraphics();
        super.paintChildren(g2);
        g2.dispose();
        g.drawImage(img, 0, 0, this);

        if (vs || hs) {
            g.setColor((Color) bg.get());
            if (vs) g.fillRect(cw, 0, 8, ch);
            if (hs) g.fillRect(0, ch, cw, 8);

            g.setColor((Color) fg.get());
            if (vs) {
                final int h = Math.max(32, Math.round(1f * ch / content.height() * ch));
                g.fillRect(cw, Math.round((float) (ch - h) / (content.height() - getHeight()) * (-sy)), 8, h);
            }
            if (hs) {
                final int w = Math.max(32, Math.round(1f * cw / content.width() * cw));
                g.fillRect(Math.round((float) (cw - w) / (content.width() - getWidth()) * (-sx)), ch, w, 8);
            }
        }

        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public int borderRadius() { return borderRadius.run(); }
    @Override public Object getComponent() { return this; }
    @Override public SScrollPane content(IContainer container) {
        if (content != null)
            super.remove((Component) content.getComponent());
        super.add((Component) container.getComponent());
        content = container;
        sx = 0;
        sy = 0;
        repaint();
        return this;
    }

    @Override
    public SScrollPane add(IComponent component) {
        content.add(component);
        //repaint();
        return this;
    }

    @Override
    public SScrollPane remove(IComponent component) {
        content.remove(component);
        repaint();
        return this;
    }

    @Override public IComponent[] childs() { return content.childs(); }

    @Override
    public SScrollPane clear() {
        content.clear();
        repaint();
        return this;
    }

    @Override
    public SScrollPane size(int width, int height) {
        setSize(width, height);
        return this;
    }

    @Override
    public SScrollPane pos(int x, int y) {
        setLocation(x, y);
        return this;
    }

    @Override
    public SScrollPane visible(boolean visible) {
        setVisible(visible);
        return this;
    }

    @Override public SScrollPane focus() { requestFocus(); return this; }

    @Override
    public SScrollPane borderRadius(int borderRadius) {
        this.borderRadius = () -> borderRadius;
        repaint();
        return this;
    }

    @Override
    public SScrollPane borderRadius(IntRunnable borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
        return this;
    }

    @Override
    public SScrollPane background(IColor bg) {
        this.bg = bg;
        repaint();
        return this;
    }

    @Override
    public SScrollPane foreground(IColor fg) {
        this.fg = fg;
        repaint();
        return this;
    }

    @Override public SScrollPane grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override public SScrollPane update() {
        sx = Math.min(Math.max(sx, getWidth() - content.width()), 0);
        sy = Math.min(Math.max(sy, getHeight() - content.height()), 0);
        repaint();
        return this;
    }
}
