package UIL.Swing;

import UIL.HAlign;
import UIL.Theme;
import UIL.base.ICheckBox;
import UIL.base.IColor;
import UIL.base.IFont;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;

public class SCheckBox extends JComponent implements ICheckBox {
    private IFont f = Theme.FONT;
    private IColor bg = Theme.BACKGROUND, fg = Theme.FOREGROUND;
    private RRunnable<Integer> br = Theme.BORDER_RADIUS;
    private Object t;
    private boolean c;

    public SCheckBox(final Object text, final boolean checked) {
        t = text;
        c = checked;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);

        final RRunnable<Integer> borderRadius = br;
        final int br = borderRadius == null ? 0 : borderRadius.run();
        g.setColor((Color) bg.get());
        if (br > 0)
            g.fillRoundRect(0, 0, getHeight(), getHeight(), br, br);
        else
            g.fillRect(0, 0, getHeight(), getHeight());

        final Object to = t;
        final String t = to == null ? null : to.toString();
        if (t != null && t.length() > 0) {
            g.setFont((Font) f.get());
            final FontMetrics m = g.getFontMetrics();
            g.setColor((Color) fg.get());
            //g.drawString(t, getHeight() + 8, (getHeight() - m.getHeight()) / 2 + m.getAscent());
            g.drawString(t, getHeight() + 8, (getHeight() - m.getHeight()) / 2 + m.getLeading() + m.getAscent());
        }

        g.dispose();
    }
    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public int borderRadius() { return br.run(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public boolean checked() { return c; }
    @Override public String text() { return t.toString(); }

    @Override public SCheckBox checked(final boolean checked) { c = checked; repaint(); return this; }
    @Override public SCheckBox text(final Object text) { t = text; repaint(); return this; }
    @Override public SCheckBox font(final IFont font) { f = font; repaint(); return this; }
    @Override public SCheckBox ha(final HAlign align) { return this; }
    @Override public SCheckBox size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SCheckBox pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SCheckBox visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SCheckBox focus() { requestFocus(); return this; }
    @Override public SCheckBox borderRadius(final RRunnable<Integer> borderRadius) { br = borderRadius; repaint(); return this; }
    @Override public SCheckBox borderRadius(final int borderRadius) { br = () -> borderRadius; repaint(); return this; }
    @Override public SCheckBox background(final IColor bg) { this.bg = bg; repaint(); return this; }
    @Override public SCheckBox foreground(final IColor fg) { this.fg = fg; repaint(); return this; }
    @Override public SCheckBox grounds(final IColor bg, final IColor fg) { this.bg = bg; this.fg = fg; repaint(); return this; }
    @Override public SCheckBox on(String name, Runnable runnable) { return this; }
    @Override public SCheckBox getComponent() { return this; }
}
