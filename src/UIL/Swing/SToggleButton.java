package UIL.Swing;

import UIL.HAlign;
import UIL.ImgAlign;
import UIL.LangItem;
import UIL.Theme;
import UIL.base.IColor;
import UIL.base.IFont;
import UIL.base.IImage;
import UIL.base.IToggleButton;
import Utils.RRunnable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class SToggleButton extends JButton implements IToggleButton {
    private IImage image = null;
    private ImgAlign align = ImgAlign.LEFT;
    private boolean c;

    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR, bga = Theme.BACKGROUND_ACCENT_COLOR, fga = Theme.FOREGROUND_ACCENT_COLOR;
    private IFont font = Theme.FONT;
    private HAlign ha = HAlign.CENTER;
    private Object text;

    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS, imageTextDist = () -> 0, imageOffset = () -> 0;

    private final ArrayList<IToggleButtonListener> listeners = new ArrayList<>();

    public SToggleButton(final Object text, final IImage image, final boolean checked) {
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        this.text = text;
        this.image = image;
        c = checked;
        addActionListener(e -> {
            c = !c;
            IToggleButtonListener[] l;
            synchronized (listeners) {
                l = listeners.toArray(new IToggleButtonListener[0]);
            }
            for (final IToggleButtonListener li : l)
                li.run(this, c);
        });
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);
        g.setFont((Font) font.get());
        final FontMetrics metrics = g.getFontMetrics();

        final String text;
        {
            final Object te = this.text;
            text = te != null ? te.toString() : "";
        }

        final boolean c = this.c;

        g.setColor((Color) (c ? bga : bg).get());
        final int br = borderRadius.run(), io = imageOffset.run(), itd = imageTextDist.run(), s = getHeight() - io * 2;
        if (br > 0)
            g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor((Color) (c ? fga : fg).get());

        final boolean t = text.length() > 0;
        final HAlign ha = this.ha;
        final ImgAlign ia = align;
        final Image img;
        {
            final IImage i = image;
            img = i != null ? (Image) i.getImage() : null;
        }

        int w = t ? metrics.stringWidth(text) : 0, h = t ? metrics.getHeight() : 0, x, y;
        if (img != null)
            if (ia == ImgAlign.TOP)
                h += s + (t ? itd : 0);
            else
                w += s + (t ? itd : 0);

        x = ha == HAlign.LEFT ? img == null ? 0 : io : (getWidth() - w) / 2;
        y = (getHeight() - h) / 2;

        if (img != null) {
            if (ia == ImgAlign.TOP) {
                g.drawImage(img, (getWidth() - s) / 2, y, s, s, this);
                y += s + itd;
            } else {
                g.drawImage(img, x, (getHeight() - s) / 2, s, s, this);
                x += s + itd;
            }
        }
        if (t)
            g.drawString(text, x, y + metrics.getLeading() + metrics.getAscent());

        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public String text() { return text.toString(); }
    @Override public SToggleButton getComponent() { return this; }

    @Override public SToggleButton size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SToggleButton pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SToggleButton visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SToggleButton focus() { requestFocus(); return this; }
    @Override public SToggleButton borderRadius(final RRunnable<Integer> borderRadius) { this.borderRadius = borderRadius; return this; }
    @Override public SToggleButton borderRadius(final int borderRadius) { this.borderRadius = () -> borderRadius; return this; }
    @Override public SToggleButton imageTextDist(final int imageTextDist) { this.imageTextDist = () -> imageTextDist; return this; }
    @Override public SToggleButton imageAlign(final ImgAlign align) { this.align = align; return this; }
    @Override public SToggleButton imageOffset(final int imageOffset) { this.imageOffset = () -> imageOffset; return this; }
    @Override public SToggleButton text(final Object text) { this.text = text; return this; }
    @Override public SToggleButton font(final IFont font) { this.font = font; return this; }
    @Override public SToggleButton ha(final HAlign align) { ha = align; return this; }
    @Override public SToggleButton background(final IColor bg) { this.bg = bg; return this; }
    @Override public SToggleButton foreground(final IColor color) { fg = color; return this; }
    @Override public SToggleButton grounds(final IColor bg, final IColor fg) { this.bg = bg; this.fg = fg; return this; }
    @Override public SToggleButton backgroundAccent(IColor color) { bga = color; return this; }
    @Override public SToggleButton foregroundAccent(final IColor color) { fga = color; return this; }
    @Override public SToggleButton image(final IImage image) { this.image = image; return this; }
    @Override public SToggleButton update() { repaint(); return this; }

    @Override
    public SToggleButton onChange(final IToggleButtonListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        return this;
    }
}
