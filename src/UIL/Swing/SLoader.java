package UIL.Swing;

import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.Theme;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

public class SLoader extends JComponent implements IComponent {
    private static final float ROT = SSwing.ANIMATION * 6.28f / 10;

    private IColor fg = Theme.FOREGROUND;

    private float rot;

    private final STimer timer = new STimer(SSwing.DELTA) {
        @Override
        public void run() {
            rot += ROT;
            if (rot > 6.28f)
                rot -= 6.28f;
            repaint();
        }
    }.start();

    public SLoader() {
        setOpaque(false);
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (event.getComponent() == SLoader.this)
                    timer.start();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (event.getComponent() == SLoader.this)
                    timer.stop();
            }

            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);
        g.setColor((Color) fg.get());

        final float r2 = rot + 1.57f, r3 = rot + 3.1f, r4 = rot - 1.57f;
        final int
                w = getWidth() - 1,
                y = getHeight() / 4, h = getHeight() - y - 1,

                x1 = (int) Math.round((Math.sin(rot) + 1) / 2 * w), y1 = (int) Math.round((Math.cos(rot) + 1) / 2 * y),
                x2 = (int) Math.round((Math.sin(r2) + 1) / 2 * w), y2 = (int) Math.round((Math.cos(r2) + 1) / 2 * y),
                x3 = (int) Math.round((Math.sin(r3) + 1) / 2 * w), y3 = (int) Math.round((Math.cos(r3) + 1) / 2 * y),
                x4 = (int) Math.round((Math.sin(r4) + 1) / 2 * w), y4 = (int) Math.round((Math.cos(r4) + 1) / 2 * y),
                h1 = y1 + h, h2 = y2 + h, h3 = y3 + h, h4 = y4 + h;

        g.drawLine(x1, y1, x1, h1);
        g.drawLine(x2, y2, x2, h2);
        g.drawLine(x3, y3, x3, h3);
        g.drawLine(x4, y4, x4, h4);

        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x1, h1, x2, h2);

        g.drawLine(x2, y2, x3, y3);
        g.drawLine(x2, h2, x3, h3);

        g.drawLine(x3, y3, x4, y4);
        g.drawLine(x3, h3, x4, h4);

        g.drawLine(x4, y4, x1, y1);
        g.drawLine(x4, h4, x1, h1);

        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override public SLoader size(int width, int height) { setSize(width, height); return this; }
    @Override public SLoader pos(int x, int y) { setLocation(x, y); return this; }
    @Override public SLoader visible(boolean visible) { setVisible(visible); return this; }
    @Override public SLoader focus() { requestFocus(); return this; }

    @Override
    public SLoader foreground(IColor fg) {
        this.fg = fg;
        repaint();
        return this;
    }

    @Override public SLoader getComponent() { return this; }
}