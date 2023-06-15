package UIL.Swing;

import UIL.base.IColor;
import UIL.base.IProgressBar;
import Utils.IntRunnable;
import UIL.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SProgressBar extends JComponent implements IProgressBar {
    private long maxProgress = 100, progress = 0;

    private IntRunnable borderRadius = Theme.PB_BORDER_RADIUS;
    private IColor bg = Theme.BACKGROUND, fg = Theme.FOREGROUND;

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override public SProgressBar size(int width, int height) { setSize(width, height); return this; }
    @Override public SProgressBar pos(int x, int y) { setLocation(x, y); return this; }
    @Override public SProgressBar visible(boolean visible) { setVisible(visible); return this; }
    @Override public SProgressBar focus() { requestFocus(); return this; }
    @Override public SProgressBar getComponent() { return this; }

    @Override public int borderRadius() { return borderRadius.run(); }
    @Override public long maxProgress() { return maxProgress; }
    @Override public long progress() { return progress; }

    @Override
    public SProgressBar maxProgress(long max) {
        this.maxProgress = max;
        repaint();
        return this;
    }

    @Override
    public SProgressBar progress(long progress) {
        this.progress = progress;
        repaint();
        return this;
    }

    @Override
    public SProgressBar borderRadius(IntRunnable borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
        return this;
    }

    @Override
    public SProgressBar borderRadius(int borderRadius) {
        this.borderRadius = () -> borderRadius;
        repaint();
        return this;
    }

    @Override
    public SProgressBar background(IColor bg) {
        this.bg = bg;
        repaint();
        return this;
    }

    @Override
    public SProgressBar foreground(IColor fg) {
        this.fg = fg;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        final int br = borderRadius.run();
        if (br > 0) g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));
        final int w = getWidth(),h = getHeight(), x = Math.round(Math.max(Math.min((float) w / maxProgress * progress, w), 0));
        if (x < w) {
            g.setColor((Color) bg.get());
            g.fillRect(x, 0, w - x, h);
        }
        if (x > 0) {
            g.setColor((Color) fg.get());
            g.fillRect(0, 0, x, h);
        }
        g.dispose();
    }
}