package UIL.Swing;

import UIL.base.IColor;
import UIL.base.IProgressBar;
import UIL.Theme;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SProgressBar extends JComponent implements IProgressBar {
    private long maxProgress = 100, progress = 0;

    private RRunnable<Integer> borderRadius = Theme.PB_BORDER_RADIUS;
    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create());
        final int br = borderRadius.run();
        if (br > 0)
            g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));
        final int w = getWidth(), h = getHeight(), x = Math.round(Math.max(Math.min((float) w / maxProgress * progress, w), 0));
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

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public long maxProgress() { return maxProgress; }
    @Override public long progress() { return progress; }
    @Override public SProgressBar getComponent() { return this; }

    @Override public SProgressBar size(final int width,final int height) { setSize(width, height); return this; }
    @Override public SProgressBar pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SProgressBar visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SProgressBar focus() { requestFocus(); return this; }
    @Override public SProgressBar maxProgress(final long max) { this.maxProgress = max; return this; }
    @Override public SProgressBar progress(final long progress) { this.progress = progress; return this; }
    @Override public SProgressBar borderRadius(final RRunnable<Integer> borderRadius) { this.borderRadius = borderRadius; return this; }
    @Override public SProgressBar background(final IColor bg) { this.bg = bg; return this; }
    @Override public SProgressBar foreground(final IColor fg) { this.fg = fg; return this; }
    @Override public SProgressBar grounds(final IColor bg, final IColor fg) { this.bg = bg; this.fg = fg; return this; }
    @Override public SProgressBar update() { repaint(); return this; }
}