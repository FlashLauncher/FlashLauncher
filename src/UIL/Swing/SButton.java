package UIL.Swing;

import UIL.*;
import UIL.base.IButton;
import UIL.base.IColor;
import UIL.base.IFont;
import UIL.base.IImage;
import Utils.IntRunnable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class SButton extends JButton implements IButton {
    private IImage image = null;
    private ImgAlign align = ImgAlign.LEFT;

    private IColor bg = Theme.BACKGROUND, fg = Theme.FOREGROUND;
    private IFont font = Theme.FONT;
    private HAlign ha = HAlign.CENTER;
    private Object text = null;

    private IntRunnable borderRadius = Theme.BORDER_RADIUS, imageTextDist = () -> 0, imageOffset = () -> 0;

    public SButton() {
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public SButton(LangItem text) { this(); text(text); }
    public SButton(String text) { this(); text(text); }
    public SButton(IImage img) { this(); image(img); }
    public SButton(Object text, IImage img) { this(); text(text); image(img); }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override public SButton size(int width, int height) { setSize(width, height); return this; }
    @Override public SButton pos(int x, int y) { setLocation(x, y); return this; }
    @Override public SButton visible(boolean visible) { setVisible(visible); return this; }
    @Override public SButton focus() { requestFocus(); return this; }

    @Override
    public SButton on(IButtonAction runnable) {
        super.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    runnable.run(SButton.this, new SButtonKeyEvent(e));
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                runnable.run(SButton.this, new SButtonMouseEvent(e));
            }
        });
        return this;
    }

    @Override
    public SButton onAction(IButtonAction runnable) {
        super.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    runnable.run(SButton.this, new SButtonKeyEvent(e));
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { runnable.run(SButton.this, new SButtonMouseEvent(e)); }
        });
        return this;
    }

    @Override
    public SButton on(String name, Runnable runnable) {
        switch (name) {
            case "action":
                super.addActionListener(e -> runnable.run());
                break;
        }
        return this;
    }

    @Override
    public SButton on(String name, IButtonRunnable runnable) {
        on(name, () -> runnable.run(this));
        return this;
    }

    @Override
    public SButton borderRadius(IntRunnable borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
        return this;
    }

    @Override
    public SButton borderRadius(int borderRadius) {
        this.borderRadius = () -> borderRadius;
        repaint();
        return this;
    }

    @Override
    public SButton imageTextDist(int imageTextDist) {
        this.imageTextDist = () -> imageTextDist;
        return this;
    }

    @Override
    public SButton imageAlign(ImgAlign align) {
        this.align = align;
        return this;
    }

    @Override
    public SButton imageOffset(int imageOffset) {
        this.imageOffset = () -> imageOffset;
        repaint();
        return this;
    }

    @Override
    public SButton getComponent() { return this; }
    @Override
    public String text() { return text.toString(); }

    @Override
    public SButton text(Object text) {
        this.text = text;
        repaint();
        return this;
    }

    @Override
    public SButton font(IFont font) {
        this.font = font;
        repaint();
        return this;
    }

    @Override
    public SButton ha(HAlign align) {
        ha = align;
        repaint();
        return this;
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

        g.setColor((Color) bg.get());
        final int br = borderRadius.run(), io = imageOffset.run(), itd = imageTextDist.run(), s = getHeight() - io * 2;
        if (br > 0)
            g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor((Color) fg.get());

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

    @Override
    public SButton background(IColor bg) {
        this.bg = bg;
        repaint();
        return this;
    }

    @Override
    public SButton foreground(IColor color) {
        fg = color;
        repaint();
        return this;
    }

    @Override
    public SButton image(IImage image) {
        this.image = image;
        repaint();
        return this;
    }

    private static class SButtonKeyEvent implements IButtonActionEvent {
        private final KeyEvent e;

        private SButtonKeyEvent(KeyEvent event) { e = event; }

        @Override public boolean isMouse() { return false; }
        @Override public int clickCount() { return 0; }
    }

    private static class SButtonMouseEvent implements IButtonActionEvent {
        private final MouseEvent e;

        private SButtonMouseEvent(MouseEvent event) { e = event; }

        @Override public boolean isMouse() { return true; }
        @Override public int clickCount() { return e.getClickCount(); }
    }
}
