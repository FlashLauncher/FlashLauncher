package UIL.Swing;

import UIL.*;
import UIL.base.IColor;
import UIL.base.IFont;
import UIL.base.IText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SText extends JComponent implements IText {
    private Object text = null;
    private IColor fg = Theme.FOREGROUND_COLOR;
    private IFont font = Theme.FONT;
    private HAlign ha = HAlign.CENTER;

    public SText() { setOpaque(false); }
    public SText(final Object text) { setOpaque(false); this.text = text; }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create());
        g.setRenderingHints(SSwing.RH);
        g.setColor((Color) fg.get());
        g.setFont((Font) font.get());
        final FontMetrics metrics = g.getFontMetrics();
        final int w = getWidth(), h = getHeight(), fh = metrics.getHeight();
        final ArrayList<String> lines = new ArrayList<>();
        if (text != null) {
            StringBuilder b = null;
            for (String s : text.toString().split(" ")) {
                if (b == null) {
                    b = new StringBuilder(s);
                    continue;
                }
                if (metrics.stringWidth(b + " " + s) > w) {
                    lines.add(b.toString());
                    b = new StringBuilder(s);
                    continue;
                }
                b.append(" ").append(s);
            }
            if (b != null)
                lines.add(b.toString());
        }

        int ty = (h - lines.size() * fh) / 2 + metrics.getLeading() + metrics.getAscent();
        for (String l : lines) {
            g.drawString(l, ha == HAlign.LEFT ? 0 : (w - metrics.stringWidth(l)) / 2, ty);
            ty += fh;
        }

        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public String text() { return text.toString(); }
    @Override public SText getComponent() { return this; }

    @Override
    public SText text(final Object text) {
        this.text = text;
        return this;
    }

    @Override
    public SText font(final IFont font) {
        this.font = font;
        return this;
    }

    @Override
    public SText ha(final HAlign align) {
        ha = align;
        return this;
    }

    @Override public SText size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SText pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SText visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SText focus() { requestFocus(); return this; }


    @Override
    public SText foreground(final IColor fg) {
        this.fg = fg;
        return this;
    }

    @Override
    public SText update() {
        repaint();
        return this;
    }
}