package UIL.Swing;

import UIL.*;
import UIL.base.*;
import Utils.RRunnable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SButton extends JButton implements IButton {
    private IImage image = null;
    private ImgAlign align = ImgAlign.LEFT;

    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;
    private IFont font = Theme.FONT;
    private HAlign ha = HAlign.CENTER;
    private Object text = null;

    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS, imageTextDist = UI.ZERO, imageOffset = UI.ZERO;

    private final ConcurrentLinkedQueue<IButtonAction> actionListeners = new ConcurrentLinkedQueue<>();

    public SButton() {
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        super.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    final SButtonKeyEvent evt = new SButtonKeyEvent(e);
                    for (final IButtonAction a : actionListeners)
                        a.run(SButton.this, evt);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                final SButtonMouseEvent evt = new SButtonMouseEvent(e);
                for (final IButtonAction a : actionListeners)
                    a.run(SButton.this, evt);
            }
        });
    }
    public SButton(final LangItem text) { this(); this.text = text; }
    public SButton(final String text) { this(); this.text = text; }
    public SButton(final IImage image) { this(); this.image = image; }
    public SButton(final Object text, final IImage image) { this(); this.text = text; this.image = image; }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create());
        g.setRenderingHints(SSwing.RH);
        g.setFont((Font) font.get());
        final FontMetrics metrics = g.getFontMetrics();

        final String text;
        {
            final Object te = this.text;
            text = te != null ? te.toString() : "";
        }

        g.setColor((Color) bg.get());
        final int br = borderRadius.run(), io = imageOffset.run(), itd = imageTextDist.run(), s = getHeight() - io * 2;
        if (br > 0)
            g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor((Color) fg.get());

        final boolean t = !text.isEmpty();
        final HAlign ha = this.ha;
        final ImgAlign ia = align;

        final IImage i = image;
        final Image img = i != null ? (Image) i.getImage() : null;


        int w = t ? metrics.stringWidth(text) : 0, h = t ? metrics.getHeight() : 0, x, y;
        if (img != null)
            if (ia == ImgAlign.TOP)
                h += s + (t ? itd : 0);
            else
                w += s + (t ? itd : 0);

        x = ha == HAlign.LEFT ? img == null ? 0 : io : (getWidth() - w) / 2;
        y = (getHeight() - h) / 2;

        if (img != null)
            if (ia == ImgAlign.TOP) {
                g.drawImage(img, (getWidth() - s) / 2, y, s, s, this);
                y += s + itd;
            } else {
                g.drawImage(img, x, (getHeight() - s) / 2, s, s, this);
                x += s + itd;
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
    @Override public SButton getComponent() { return this; }

    @Override public SButton size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SButton pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SButton visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SButton focus() {
        //requestFocus();
        requestFocusInWindow();
        return this;
    }
    @Override public SButton onAction(final IButtonAction runnable) { actionListeners.add(runnable); return this; }
    @Override public SButton borderRadius(final RRunnable<Integer> borderRadius) { this.borderRadius = borderRadius; return this; }
    @Override public SButton imageTextDist(final int imageTextDist) { this.imageTextDist = () -> imageTextDist; return this; }
    @Override public SButton imageAlign(final ImgAlign align) { this.align = align; return this; }
    @Override public SButton imageOffset(final int imageOffset) { this.imageOffset = () -> imageOffset; return this; }
    @Override public SButton text(final Object text) { this.text = text; return this; }
    @Override public SButton font(final IFont font) { this.font = font; return this; }
    @Override public SButton ha(final HAlign align) { ha = align; return this; }
    @Override public SButton background(final IColor bg) { this.bg = bg; return this; }
    @Override public SButton foreground(final IColor color) { fg = color; return this; }
    @Override public SButton grounds(final IColor bg, final IColor fg) { this.bg = bg; this.fg = fg; return this; }
    @Override public SButton image(final IImage image) { this.image = image; return this; }
    @Override public SButton update() { repaint(); return this; }

    @Override
    public void doClick() {
        for (final IButtonAction a : actionListeners)
            a.run(this, null);
    }

    private static class SButtonKeyEvent implements IButtonActionEvent {
        private final KeyEvent e;

        private SButtonKeyEvent(final KeyEvent event) { e = event; }

        @Override public boolean isConsumed() { return e.isConsumed(); }
        @Override public boolean isMouse() { return false; }
        @Override public int clickCount() { return 0; }

        @Override public void consume() { e.consume(); }
    }

    private static class SButtonMouseEvent implements IButtonActionEvent {
        private final MouseEvent e;

        private SButtonMouseEvent(final MouseEvent event) { e = event; }

        @Override public boolean isConsumed() { return e.isConsumed(); }
        @Override public boolean isMouse() { return true; }
        @Override public int clickCount() { return e.getClickCount(); }

        @Override public void consume() { e.consume(); }
    }
}